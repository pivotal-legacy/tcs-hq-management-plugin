/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.io.File
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.LinkedHashMap
import java.util.Map

import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.FilenameUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.hyperic.hq.agent.FileDataResult
import org.hyperic.hq.agent.FileData
import org.hyperic.hq.agent.client.AgentCommandsClient
import org.hyperic.hq.agent.client.AgentCommandsClientFactory
import org.hyperic.hq.appdef.Agent
import org.hyperic.hq.appdef.shared.AgentManager
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.control.shared.ControlScheduleManager

import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.livedata.shared.LiveDataResult
import org.hyperic.util.config.ConfigResponse
import org.hyperic.util.security.MD5
import org.json.JSONArray
import org.json.JSONObject

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application
import com.springsource.hq.plugin.tcserver.util.application.ApplicationIdentifier
import com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils
import com.springsource.hq.plugin.tcserver.util.control.ControlActionFailedException
import com.springsource.hq.plugin.tcserver.util.control.ControlActionHelper

class TomcatappmgmtController extends BaseController {
    
    private static final String RESOURCE_TYPE_NAME_TC_RUNTIME_6_0 = "SpringSource tc Runtime 6.0";
    
    private static final String SERVICE_NAME = 'SERVICE_NAME'
    
    private static final String HOST_NAME = 'HOST_NAME'
    
    private static final String APPLICATION = "APPLICATION1"
        
    private final ControlActionHelper controlActionHelper = new ControlActionHelper()
    
    private final ControlScheduleManager controlScheduleManager = Bootstrap.getBean(ControlScheduleManager.class)
    
    private final AgentCommandsClientFactory agentCommandsClientFactory = Bootstrap.getBean(AgentCommandsClientFactory.class)

    private final AgentManager agentManager = Bootstrap.getBean(AgentManager.class)
    
    private final Log log = LogFactory.getLog(this.getClass())

    protected void init() {
        setJSONMethods(['invokeCommand'])
    }

    private formContextPath(fileName, contextPath){
        if ("".equals(contextPath)) {
            def endFileNameIndex = fileName.lastIndexOf(".war")
            if (endFileNameIndex > 0){
                contextPath = fileName.substring(0,endFileNameIndex)
            }
        } else if ("/".equals(contextPath) || "/ROOT".equalsIgnoreCase(contextPath) || "root".equalsIgnoreCase(contextPath)) {
            contextPath = "ROOT"
        }
        
        if (!contextPath.startsWith("/") && !contextPath.equalsIgnoreCase("ROOT")) {
            contextPath = "/" + contextPath
        }
        return contextPath
    }

    private getCommandHistoryName(command){
        def index = command.indexOf("Apps")
        if (index > 0){
            return command.substring(0, index) + "Applications"
        }
        index = command.indexOf("App")
        if (index > 0){
            return command.substring(0, index) + "Application"
        }
    }

    private getStreamData() {
        def inputStream
        def fileName = ""
        def contextPath = ""
        def serviceName = ""
        def hostName = ""
        def coldDeployValue = ""
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory())
        List<FileItem> fileItems  = upload.parseRequest(invokeArgs.request)

        for (FileItem fileItem : fileItems) {
            if (fileItem.fieldName.equals("fileName")){
                fileName = FilenameUtils.getName(fileItem.name)
                inputStream = fileItem.inputStream
            } else if (fileItem.fieldName.equals("contextPath")){
                contextPath = fileItem.string
            } else if (fileItem.fieldName.equals("serviceName")){
                serviceName = fileItem.string
            } else if (fileItem.fieldName.equals("hostName")){
                hostName = fileItem.string
            } else if (fileItem.fieldName.equals("coldDeploy")) {
                coldDeployValue = fileItem.string
            }
        }

        [inputStream:inputStream, fileName:fileName, contextPath: formContextPath(fileName, contextPath),
            serviceName:serviceName, hostName:hostName, coldDeployValue:coldDeployValue]
    }

    private getServiceHostMappings(){
        Resource groupOrInstance = viewedResource
        List<Resource> instances = getInstances(groupOrInstance)

        JSONArray errorMessages = new JSONArray()

        JSONArray listArray = new JSONArray()
        Map<String, Set<String>> mappings = new LinkedHashMap()

        String initialService = null
        String initialHost = null

        for (Resource instance : instances) {
            try {
                LiveDataResult liveDataResult = instance.getLiveData(user, "getServiceHostMappings", new ConfigResponse())
                if (liveDataResult.hasError()) {
                    recordFailure(instance, errorMessages, 'getServiceHostMappings', liveDataResult.errorMessage)
                } else {
                    Map<String, List<String>> servicesAndHosts = liveDataResult.objectResult
                    servicesAndHosts.each { entry ->
                        Set<String> hosts = mappings.get(entry.key)
                        if (!entry.value.isEmpty()) {
                            if (hosts == null) {
                                hosts = new TreeSet<String>()
                                mappings.put(entry.key, hosts)
                            }
                            hosts.addAll(entry.value)

                            if (!initialService) {
                                initialService = entry.key
                                initialHost = entry.value[0]
                            }
                        }
                    }
                }
            } catch (Exception e){
                recordFailure(instance, errorMessages, 'getServiceHostMappings', e)
            }
        }

        mappings.each{entry ->
            listArray.put(key:"${entry.key}", value: new JSONArray(entry.value))
        }
        [listArray:listArray, initialService:initialService, initialHost:initialHost, errorMessages:errorMessages]
    }
    
    def recordFailure(Resource instance, JSONArray errorMessages, String operation, Exception failure) {
        recordFailure(instance, errorMessages, operation, failure.message)
    }
    
    def recordFailure(Resource instance, JSONArray errorMessages, String operation, String failureMessage) {
        errorMessages.put(key: linkTo(getInstanceIdentifier(instance), [resource:instance.entityId]), value: "Failed to perform operation '${operation}': '${failureMessage}'")
    }

    def manageApplications(params) {
        def eid = viewedResource.entityId
        boolean multiRevisionCapable = isGroupOrInstanceMultiRevisionCapable(viewedResource)
        def resultStatuses = new JSONObject()

        def serviceHostMappings = getServiceHostMappings()
        JSONArray serviceListing = serviceHostMappings.listArray
        JSONArray errorMessages = serviceHostMappings.errorMessages

        String hostName = serviceHostMappings.initialHost
        String serviceName = serviceHostMappings.initialService

        JSONArray resourceEntryErrors = new JSONArray()

        if (isFileUpload(invokeArgs)) {
            def deployResult = deployAppFromFileUpload(params)

            resultStatuses = deployResult.resultStatuses
            resourceEntryErrors = deployResult.resourceEntryErrors

            for (int i = 0; i < deployResult.errorMessages.length(); i++) {
                errorMessages.put(deployResult.errorMessages.get(i))
            }
        }

        ListApplicationsResult result = listApplicationsOnInstances(viewedResource, serviceName, hostName)

        render(locals:[
                eid:eid,
                appListing:result.applications,
                serviceListing:serviceListing,
                errorMessages: errorMessages,
                resultStatuses: resultStatuses,
                resourceEntryErrors: resourceEntryErrors,
                cumulativeResults: result.applicationsDetail,
                selectedService:serviceName,
                selectedHost:hostName,
                readOnly:isReadOnly(viewedResource, user),
                multiRevisionCapable:multiRevisionCapable
                ])
    }

    private boolean isFileUpload(invokeArgs) {
        def contentType = invokeArgs.request.contentType
        return (contentType != null && contentType.contains("multipart/form-data"))
    }

    private deployAppFromFileUpload(parameters) {
        def uploadStreamData = getStreamData()
        boolean coldDeploy = isColdDeploy(uploadStreamData.coldDeployValue)

        Resource groupOrInstance = viewedResource

        String contextPath = uploadStreamData.contextPath
        String service = uploadStreamData.serviceName
        String host = uploadStreamData.hostName

        File temporaryWarFile = File.createTempFile("temporary", ".war")

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5")
            new DigestOutputStream(new FileOutputStream(temporaryWarFile), messageDigest) << uploadStreamData.inputStream

            long warFileSize = temporaryWarFile.length()
            String md5Checksum = new MD5().getDigestString(messageDigest.digest())

            if (coldDeploy) {
                return coldDeployWarFile(groupOrInstance, temporaryWarFile.absolutePath, contextPath, service, host, { instance, targetLocation ->
                    transferWarFileToInstance(instance, new FileInputStream(temporaryWarFile), warFileSize, md5Checksum, targetLocation, FileData.WRITETYPE_CREATEONLY)
                })                
            } else {
                return hotDeployWarFile(groupOrInstance, temporaryWarFile.absolutePath, contextPath, service, host, { instance ->
                    return transferWarFileToInstance(instance, new FileInputStream(temporaryWarFile), warFileSize, md5Checksum)
                })
            }
        } finally {
            temporaryWarFile.delete()
        }
    }

    private boolean isReadOnly(Resource groupOrInstance, AuthzSubject user) {
        boolean readOnly = false

        try {
            Resource instance

            if (groupOrInstance.isGroup()) {
                instance = groupOrInstance.getGroupMembers(user).get(0)
            } else {
                instance = groupOrInstance
            }

            checkPermissionsForModifyOperation(instance, user)

        } catch (PermissionException pe) {
            readOnly = true
        }

        return readOnly
    }

    def invokeCommand(parameters) {
        String method = parameters.getOne('METHOD')

        if ("stopApps" == method) {
            return stopApps(parameters)
        } else if ("startApps" == method) {
            return startApps(parameters)
        } else if ("reloadApps" == method) {
            return reloadApps(parameters)
        } else if ("undeployApps" == method) {
            return undeployApps(parameters)
        } else if ("listApps" == method) {
            return listApps(parameters)
        } else if ("deployApp" == method) {
            return deployAppAlreadyOnAgentHost(parameters)
        }
    }

    private deployAppAlreadyOnAgentHost(parameters) {
        String warFilePath = parameters.getOne('WAR_FILE_LOCATION')
        String contextPath = formContextPath(new File(warFilePath).name, parameters.getOne("DEPLOY_PATH"))
        String serviceName = parameters.getOne(SERVICE_NAME)
        String hostName = parameters.getOne(HOST_NAME)
        boolean coldDeploy = "on".equals(parameters.getOne("REMOTE_COLD_DEPLOY"))

        if (coldDeploy) {
            return coldDeployWarFile(viewedResource, warFilePath, contextPath, serviceName, hostName, { instance, targetLocation ->
                if (!fileExistsOnAgent(viewedResource, warFilePath)) {
                    throw new InstanceOperationFailedException("Failure - Application ${warFilePath} doesn't exist on server.")
                }
                ConfigResponse config = new ConfigResponse()
                config.setValue("SOURCE", warFilePath)
                config.setValue("DESTINATION", targetLocation)
                LiveDataResult result = instance.getLiveData(user, "copyFile", config)
                if (result.hasError()) {
                    throw new InstanceOperationFailedException(result.errorMessage)
                }                
            })
        } else {
            return hotDeployWarFile(viewedResource, warFilePath, contextPath, serviceName, hostName, { instance ->
                return warFilePath
            })
        }
    }

    private boolean fileExistsOnAgent(Resource instance, String filename) {
        return instance.getLiveData(user, "fileExists", new ConfigResponse(["FILENAME": filename])).objectResult
    }

    private boolean fileExistsOnServer(String filename) {
        return new File(filename).exists()
    }

    private coldDeployWarFile(Resource groupOrInstance, String warFile, String contextPath, String service, String host, Closure copyWarFile) {
        String operation = "coldDeployApp"
        String operationDescription = "deploy applications"
        String pathSeparator = ""
        if (!contextPath.startsWith("/")) {
            pathSeparator = "/"
        }

        String controlHistoryArguments = "${contextPath}, ${warFile}"

        return performOperation(service, host, operation, operationDescription, controlHistoryArguments, OperationType.MODIFY, { instance, deployResults, errors ->
            ConfigResponse config = createConfig(service, host)

            long startTime = now()

            Map<String, String> entryErrors = new HashMap();
            
            Set<Application> applicationsOnInstance = listSetOfApplicationsOnInstance(instance, config)
            String newRevision = ApplicationUtils.getNewRevisionForApplication(applicationsOnInstance, contextPath)
            
            if (!isGroupOrInstanceMultiRevisionCapable(instance) && newRevision != null) {
                String response = "Failure - Application ${contextPath} already exists."
                OperationResult operationResult = new OperationResult(response, true)
                recordOperationResult(operationResult, deployResults, errors, contextPath, getInstanceIdentifier(instance))
                entryErrors.put(operation, response)
            } else {
                String appBaseDirectory = getAppBaseDirectory(instance, config)
                String warFileName = determineWarFileName(contextPath, newRevision)

                OperationResult operationResult
                try {
                    controlActionHelper.stopServer(user, instance)
                    copyWarFile(instance, "${appBaseDirectory}${pathSeparator}${warFileName}")
                    controlActionHelper.startServer(user, instance)
    
                    operationResult = new OperationResult("Ok - Application ${contextPath} has been cold deployed", false)
                } catch (ControlActionFailedException e) {
                    String response = "Failure - " + e.getMessage()
                    operationResult = new OperationResult(response, true)
                    entryErrors.put(contextPath, response)
                }

                recordOperationResult(operationResult, deployResults, errors, contextPath, getInstanceIdentifier(instance))
            }

            long stopTime = now()

            if (!entryErrors.isEmpty()) {
                recordFailureControlHistory(instance, user, operation, controlHistoryArguments, startTime, stopTime, entryErrors)
            } else {
                recordSuccessControlHistory(instance, user, operation, controlHistoryArguments, startTime, stopTime)
            }
        });
    }            

    private String getAppBaseDirectory(Resource instance, ConfigResponse config) {
        return instance.getLiveData(user, "getAppBase", config).objectResult
    }

    private hotDeployWarFile(Resource groupOrInstance, String warFile, String contextPath, String service, String host, Closure copyWarFile) {
        String operation = "deployApp"
        String operationDescription = "deploy applications"
        String controlHistoryArguments = "${contextPath}, ${warFile}"
        
        boolean multiRevisionCapable = isGroupOrInstanceMultiRevisionCapable(groupOrInstance)

        return performOperation(service, host, operation, operationDescription, controlHistoryArguments, OperationType.MODIFY, { instance, deployResults, errors ->
            String warFileOnInstance = copyWarFile(instance)            

            ConfigResponse config = createConfig(service, host)
            config.setValue("WAR_FILE_LOCATION", warFileOnInstance)
            config.setValue("DEPLOY_PATH", contextPath)
            config.setValue("MULTI_REVISION_CAPABLE", multiRevisionCapable)
            
            OperationResult operationResult = performOperationOnInstance(instance, operation, config, controlHistoryArguments)
            recordOperationResult(operationResult, deployResults, errors, contextPath, getInstanceIdentifier(instance))
        })
    }

    private String transferWarFileToInstance(Resource instance, InputStream warFileStream, long warFileSize, String md5Checksum) {
        LiveDataResult liveDataResult = instance.getLiveData(user, "getTemporaryWebAppDirectory", new ConfigResponse())

        if (liveDataResult.hasError()) {
            throw new InstanceOperationFailedException(liveDataResult.errorMessage)
        } else {
            String temporaryWebAppDirectory = liveDataResult.objectResult
            // must grab the correct temp path with the valid file separators.
            // Front-slash works on windows and linux.
            String temporaryWarFilePath = temporaryWebAppDirectory + "/" + "temp.war";

            transferWarFileToInstance(instance, warFileStream, warFileSize, md5Checksum, temporaryWarFilePath, FileData.WRITETYPE_CREATEOROVERWRITE)
            return temporaryWarFilePath
        }
    }

    private void transferWarFileToInstance(Resource instance, InputStream warFileStream, long warFileSize, String md5Checksum, String locationOnInstance, int fileWriteMode) {
        FileData fileData = new FileData(locationOnInstance, warFileSize, fileWriteMode)
        fileData.setMD5CheckSum(md5Checksum)
        Agent agent = agentManager.getAgent(instance.entityId)
        try {
            AgentCommandsClient commandsClient = agentCommandsClientFactory.getClient(agent)
            FileDataResult[] fileDataResults = commandsClient.agentSendFileData([fileData] as FileData[], [warFileStream] as InputStream[])
            fileDataResults.each { fileDataResult ->
                log.debug("Transferred: " + fileDataResult.toString())
            }
            if(!fileExistsOnAgent(instance, locationOnInstance)) {
                throw new InstanceOperationFailedException("Transfer of war file to instance failed")
            }
        } catch (Exception e){
            throw new InstanceOperationFailedException(e.message)
        }
        ConfigResponse config = new ConfigResponse()
        config.setValue("FILE_LOCATION", locationOnInstance)
        LiveDataResult result = instance.getLiveData(user, "changeFilePermissionsAndOwnership", config)
        if (result.hasError()) {
            throw new InstanceOperationFailedException(result.errorMessage)
        }
    }

    private listApps(parameters) {
        Resource groupOrInstance = viewedResource

        String service = parameters.getOne(SERVICE_NAME)
        String host = parameters.getOne(HOST_NAME)

        ListApplicationsResult result = listApplicationsOnInstances(groupOrInstance, service, host)

        return [resultStatuses:new JSONObject(),
            statusChanges:result.applications,
            resourceEntryErrors:new JSONArray(),
            errorMessages:result.errorMessages,
            cumulativeResults:result.applicationsDetail]
    }

    private JSONArray processGroupResult(JSONArray applications) {

        Map<ApplicationIdentifier, Map<String, String>> runningApplications = findAllRunningApplications(applications)

        JSONArray processedApplications = new JSONArray()

        runningApplications.each { Map.Entry<ApplicationIdentifier, Map<String, String>> entry ->
            def application = new JSONArray()
            application.put("${entry.value.running} of " + applications.length()
                    + " servers are running")
            application.put(entry.value.sessions)
            application.put(entry.key.version)
            processedApplications.put(key:"${entry.key.name}", value:application)
        }

        JSONArray result = new JSONArray()
        result.put(key:"groupPlaceholder", value:processedApplications)

        return result
    }

    private Map<ApplicationIdentifier, Map<String, String>> findAllRunningApplications(JSONArray applications) {
        Map<ApplicationIdentifier, String> runningApplications = new TreeMap<ApplicationIdentifier, String>()

        for (int i = 0; i < applications.length(); i++) {
            def application = applications.get(i)
            String instanceName = application.key
            JSONArray applicationsOnInstance = application.value
            for (int j = 0; j < applicationsOnInstance.length(); j++) {
                def applicationOnInstance = applicationsOnInstance.get(j)
                String applicationName = applicationOnInstance.key
                JSONArray applicationDetails = applicationOnInstance.value
                int applicationVersion = applicationDetails.get(2)
                
                ApplicationIdentifier applicationIdentifier = new ApplicationIdentifier(applicationName, applicationVersion)
                
                Map<String, String> applicationInfo = runningApplications.get(applicationIdentifier)
                
                if (applicationInfo == null) {
                    applicationInfo = new HashMap<String, String>();
                    applicationInfo.put("running", Integer.valueOf(0));
                    applicationInfo.put("sessions", Integer.valueOf(0));
                    runningApplications.put(applicationIdentifier, applicationInfo);
                }
                
                Integer runningCount = applicationInfo.get("running");
                Integer sessions = applicationInfo.get("sessions");                                               
                
                String applicationStatus = applicationDetails.getString(0)
                
                if ("Running".equals(applicationStatus)) {
                    runningCount = runningCount.plus(1)
                    applicationInfo.put("running", runningCount)
                }
                
                sessions = sessions.plus(applicationDetails.getInt(1))
                applicationInfo.put("sessions", sessions)
            }
        }

        return runningApplications
    }

    private stopApps(parameters) {
        return performApplicationControlOperation(parameters, "stopApps", "stop applications")
    }

    private startApps(parameters) {
        return performApplicationControlOperation(parameters, "startApps", "start applications")
    }

    private reloadApps(parameters) {
        return performApplicationControlOperation(parameters, "reloadApps", "reload applications")
    }

    private undeployApps(parameters) {
        return performApplicationModifyOperation(parameters, "undeployApps", "undeploy applications")
    }

    private performApplicationControlOperation(Map parameters, String operation, String operationDescription) {
        return performApplicationOperation(parameters, operation, operationDescription, OperationType.CONTROL)
    }

    private performApplicationModifyOperation(Map parameters, String operation, String operationDescription) {
        return performApplicationOperation(parameters, operation, operationDescription, OperationType.MODIFY)
    }

    private performApplicationOperation(Map parameters, String operation, String operationDescription, OperationType operationType) {
        String service = parameters.getOne(SERVICE_NAME)
        String host = parameters.getOne(HOST_NAME)
        String controlHistoryArguments = ""

        parameters.APPLICATIONS.each { application ->
            controlHistoryArguments = controlHistoryArguments + application + " "
        }
        controlHistoryArguments = truncateText(500, controlHistoryArguments)

        performOperation(service, host, operation, operationDescription, controlHistoryArguments, operationType, { instance, applicationResults, resourceEntryErrors ->
            String instanceIdentifier = getInstanceIdentifier(instance)

            for (String application : parameters.APPLICATIONS) {
                OperationResult result = performApplicationOperationOnInstance(instance, operation, service, host, application)
                recordOperationResult(result, applicationResults, resourceEntryErrors, application, instanceIdentifier)
            }
        })
    }

    private recordOperationResult(OperationResult operationResult, JSONArray results, JSONArray errors, String resultKey, String errorKey) {
        results.put(key:resultKey, value:operationResult.response)
        errors.put(resource:errorKey, hasEntryErrors:operationResult.hadErrors)
    }

    private performOperation(String service, String host, String operation, String operationDescription, String controlHistoryArguments, OperationType operationType, Closure operationToPerform) {
        Resource groupOrInstance = viewedResource

        List<Resource> instances = getInstances(groupOrInstance)

        JSONArray results = new JSONArray()
        JSONArray errorMessages = new JSONArray()
        JSONArray resourceEntryErrors = new JSONArray()

        long startTime = now()
        for (Resource instance : instances) {
            String instanceIdentifier = getInstanceIdentifier(instance)
            try {
                JSONArray instanceResults = new JSONArray()

                if (OperationType.CONTROL == operationType) {
                    checkPermissionsForControlOperation(instance, user)
                } else if (OperationType.MODIFY == operationType) {
                    // TODO A user won't be able to undeploy unless they have the modify permission - this permission check is redundant
                    checkPermissionsForModifyOperation(instance, user)
                }

                operationToPerform(instance, instanceResults, resourceEntryErrors)
                results.put(key:instanceIdentifier, value:instanceResults)

            } catch (PermissionException pe) {
                errorMessages.put(key:instanceIdentifier, value:"${user.name} must be assigned to a role with the Server ${operationType} permission to be able to ${operationDescription}")
            } catch (InstanceOperationFailedException iofe) {
                recordFailure(instance, errorMessages, operation, iofe)
            }
        }
        long stopTime = now()

        if (groupOrInstance.isGroup()) {
            if (errorMessages.length() != 0) {
                String errorMessage = ""

                for(int i = 0; i < errorMessages.length(); i++) {
                    errorMessage = errorMessage + errorMessages.getString(i) + "\n"
                }

                recordFailureControlHistory(groupOrInstance, user, operation, controlHistoryArguments, startTime, stopTime, errorMessage)
            } else {
                recordSuccessControlHistory(groupOrInstance, user, operation, controlHistoryArguments, startTime, stopTime)
            }
        }

        JSONObject resultStatuses = new JSONObject()
        resultStatuses.put("result", results)

        ListApplicationsResult result = listApplicationsOnInstances(groupOrInstance, service, host)
        JSONObject statusChanges = result.applications

        return [resultStatuses:resultStatuses,
            statusChanges:statusChanges,
            resourceEntryErrors:resourceEntryErrors,
            errorMessages:errorMessages,
            cumulativeResults:result.applicationsDetail]
    }

    private ListApplicationsResult listApplicationsOnInstances(Resource groupOrInstance, String service, String host) {

        List<Resource> instances = getInstances(groupOrInstance)

        JSONArray errorMessages = new JSONArray();
        JSONArray applicationDetails = new JSONArray()

        for (Resource instance : instances) {
            try {
                JSONArray applicationsOnInstance = listApplicationsOnInstance(instance, service, host)
                applicationDetails.put(key:getInstanceIdentifier(instance), value: applicationsOnInstance)
            } catch (InstanceOperationFailedException iofe) {
                recordFailure(instance, errorMessages, 'listApps', iofe)
            }
        }

        JSONArray results


        if (groupOrInstance.isGroup()) {
            results = processGroupResult(applicationDetails)
        } else {
            results = applicationDetails
        }

        JSONObject resultsMap = new JSONObject()
        resultsMap.put("result", results)

        return new ListApplicationsResult(resultsMap, errorMessages, applicationDetails)
    }

    private JSONArray listApplicationsOnInstance(Resource instance, String service, String host) throws InstanceOperationFailedException {
        ConfigResponse config = createConfig(service, host)
        config.setValue("MULTI_REVISION_CAPABLE", isGroupOrInstanceMultiRevisionCapable(instance))
        return listApplicationsOnInstance(instance, config)
    }
    
    private JSONArray listApplicationsOnInstance(Resource instance, ConfigResponse config) throws InstanceOperationFailedException {        
        return convertSetToJSONArray(listSetOfApplicationsOnInstance(instance, config))
    }
    
    private Set<Application> listSetOfApplicationsOnInstance(Resource instance, ConfigResponse config) throws InstanceOperationFailedException {
        LiveDataResult liveDataResult = instance.getLiveData(user, "listApps", config)
        if (liveDataResult.hasError()) {
            throw new InstanceOperationFailedException(liveDataResult.errorMessage)
        } else {
            return liveDataResult.getObjectResult()
        }
    }

    private String getInstanceIdentifier(Resource instance) {
        return "${instance.name} (${instance.id})"
    }

    private checkPermissionsForControlOperation(Resource resource, AuthzSubject user) throws PermissionException {
        checkPermissionsForOperation(resource, OperationType.CONTROL, user)
    }

    private checkPermissionsForViewOperation(Resource resource, AuthzSubject user) throws PermissionException {
        checkPermissionsForOperation(resource, OperationType.VIEW, user)
    }

    private checkPermissionsForModifyOperation(Resource resource, AuthzSubject user) throws PermissionException {
        checkPermissionsForOperation(resource, OperationType.MODIFY, user)
    }

    private checkPermissionsForOperation(Resource resource, OperationType operationType, AuthzSubject user) throws PermissionException {
        convertToAppdefResource(resource).checkPerms(operation:operationType.toString(), user:user)
    }

    private List<Resource> getInstances(Resource groupOrInstance) {
        List<Resource> instances

        if (groupOrInstance.isGroup()) {
            instances = groupOrInstance.getGroupMembers(user).findAll {it.entityId.isServer()}
        } else {
            instances = new ArrayList<Resource>()
            instances.add(groupOrInstance)
        }

        return instances
    }

    private OperationResult performApplicationOperationOnInstance(Resource instance, String operation, String service, String host, String application) {

        ConfigResponse config = createConfig(service, host)
        config.setValue(APPLICATION, application)

        return performOperationOnInstance(instance, operation, config, application)
    }

    private OperationResult performOperationOnInstance(Resource instance, String operation, ConfigResponse config, String controlHistoryArguments) {
        long startTime = now()

        LiveDataResult liveDataResult = instance.getLiveData(user, operation, config)

        long stopTime = now()

        if (liveDataResult.hasError()) {
            throw new InstanceOperationFailedException(liveDataResult.errorMessage)
        } else {
            Map<String, String> entryErrors = new HashMap<String, String>()

            liveDataResult.objectResult.each { entry ->
                if (entry.value.contains("Failure")){
                    entryErrors.put(entry.key, entry.value)
                }
            }

            if (!entryErrors.isEmpty()) {
                recordFailureControlHistory(instance, user, operation, controlHistoryArguments, startTime, stopTime, entryErrors)
            } else {
                recordSuccessControlHistory(instance, user, operation, controlHistoryArguments, startTime, stopTime)
            }

            String response = liveDataResult.objectResult.entrySet().iterator().next().getValue()
            return new OperationResult(response, !entryErrors.isEmpty())
        }
    }

	private void recordSuccessControlHistory(Resource instance, AuthzSubject user, String operation, String application, long startTime, long stopTime) {
	    String status = "Success"
        String errorMessage = ""

        recordControlHistory(instance, user, operation, application, startTime, stopTime, status, errorMessage)
	}

    private void recordFailureControlHistory(Resource instance, AuthzSubject user, String operation, String arguments, long startTime, long stopTime, Map<String, String> errors) {
        String errorMessage = ""
        errors.each { entry ->
            errorMessage = errorMessage + entry.key + ": " + entry.value + "\n"
        }
        errorMessage = truncateText(500, errorMessage)

        recordFailureControlHistory(instance, user, operation, arguments, startTime, stopTime, errorMessage)
    }

    private void recordFailureControlHistory(Resource instance, AuthzSubject user, String operation, String arguments, long startTime, long stopTime, String errorMessage) {
        String status = "Failure"
        recordControlHistory(instance, user, operation, arguments, startTime, stopTime, status, errorMessage)
    }

    private void recordControlHistory(Resource instance, AuthzSubject user, String operation, String arguments, long startTime, long stopTime, String status, String errorMessage) {
        AppdefEntityID entityId = instance.entityId
        int groupId = -1
        int batchId = -1
        String subjectName = user.name
        String action = getCommandHistoryName(operation)
        boolean scheduled = false
        long scheduleTime = 0L
        String description = "None"

        this.controlScheduleManager.createHistory(entityId, groupId, batchId, subjectName, action, arguments, scheduled, startTime, stopTime, scheduleTime, status, description, errorMessage)
    }

    private ConfigResponse createConfig(String service, String host) {
        ConfigResponse config = new ConfigResponse()

        config.setValue(SERVICE_NAME, service)
        config.setValue(HOST_NAME, host)

        return config
    }

    private removeTemporaryWarFiles(configs, resources, fileNames){
        int i=0
        for (i; i <  configs.size(); i++){
            configs.get(i).setValue("fileName", fileNames.get(i))
            resources.get(i).getLiveData(user, "removeTemporaryWarFile", configs.get(i))
        }
    }

    private truncateText(maxSize, text){
        def truncatedText = text
        if (text != null){
            if (text.length() > maxSize){
                truncatedText = text.substring(0,maxSize-3) + "..."
            }
        }
        return truncatedText
    }

    private convertSetToJSONArray(applicationSet){
        def listArray = new JSONArray()
        applicationSet.each{ application ->
            def applicationArray = new JSONArray()
            log.debug "application = [$application.name, $application.version, $application.status, $application.sessionCount]"
            applicationArray.put(application.status)
            applicationArray.put(application.sessionCount)
            applicationArray.put(application.version)
            listArray.put(key:application.name, value:applicationArray)
        }
        listArray
    }

    private convertToAppdefResource(resource) {
        if (resource.isPlatform())
            return resource.toPlatform()
        else if (resource.isServer())
            return resource.toServer()
        else if (resource.isService())
            return resource.toService()
        else
            throw new Exception("Unable to convert resource: " + resource)
    }

    private boolean isColdDeploy(coldDeployValue) {
        def coldDeploy = false
        if (coldDeployValue == "on") {
            coldDeploy = true
        }
        return coldDeploy
    }

    private boolean isGroupOrInstanceMultiRevisionCapable(Resource groupOrInstance) {
        boolean isGroup = groupOrInstance.isGroup()
        
        String resourceTypeName
        
        if (!isGroup) {
            resourceTypeName = convertToAppdefResource(groupOrInstance).appdefResourceType.name
            return !RESOURCE_TYPE_NAME_TC_RUNTIME_6_0.equals(resourceTypeName)
        } else {
            Iterator iterator = groupOrInstance.getGroupMembers(user).iterator()
            if (iterator.hasNext()) {
                Resource member = iterator.next()
                if (member != null) {
                    return isGroupOrInstanceMultiRevisionCapable(member)
                }
            }
            return false
        }                   
    }     
    
    private String determineWarFileName(String contextPath, String revision) {
        if (revision != null) {
            return contextPath + "##" + revision + ".war"
        } else {
            return contextPath + ".war" 
        }
    }  
    
    private String getAppBasePath(Resource instance, AuthzSubject user, ConfigResponse config) {
        def liveDataResult = instance.getLiveData(user, "getAppBase", config)
        return liveDataResult.objectResult
    }
}

