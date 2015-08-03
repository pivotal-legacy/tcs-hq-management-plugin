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

import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.util.config.ConfigResponse
import org.hyperic.hq.appdef.server.session.AppdefResource
import org.hyperic.hq.appdef.shared.AgentManager
import org.hyperic.hq.appdef.shared.AppdefEntityConstants
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.agent.client.AgentCommandsClientFactory
import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.authz.shared.ResourceGroupManager

import org.hyperic.hq.agent.client.AgentCommandsClient
import org.hyperic.hq.control.shared.ControlScheduleManager
import org.hyperic.hq.agent.FileData
import org.hyperic.hq.agent.FileDataResult
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.util.config.ConfigResponse
import org.hyperic.hq.autoinventory.shared.AutoinventoryManager
import org.hyperic.hq.product.PluginException
import org.hyperic.hq.context.Bootstrap

import com.thoughtworks.xstream.XStream

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Service
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Host

import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream

class ApplicationmanagementController extends BasemanagementController {

    private static final String RESOURCE_TYPE_NAME_TC_RUNTIME_6_0 = "SpringSource tc Runtime 6.0";
    
    ServletFileUploadFactory servletFileUploadFactory = new DefaultServletFileUploadFactory()

    protected void init() {
        //		onlyAllowSuperUsers()
    }
    
    private getConfigResponse(){
        def config = [] as ConfigResponse
        def parameterMap = invokeArgs.request.parameterMap
        for (parameter in parameterMap){
            if (parameter.key != 'applications'){
                config.setValue(parameter.key, parameter.value[0])
            }
        }
        def int i=1
        for (application in parameterMap.applications){
            config.setValue("application$i", application)
            i++
        }
        config
    }
    
    private Closure getResultXML(list) { { doc ->
            if (!list.isEmpty()){
                Result() {
                    list.each { service ->
                        Service(name: service.name) {
                            service.hosts.each { host ->
                                Host(name: host.name) {
                                    host.applications.each { application ->
                                        Application(name: application.name,
                                                status: application.status,
                                                sessions: application.sessionCount,
                                                version: application.version)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Closure getResourceNameXML(resourceName){ { doc -> ResourceName(resourceName) }
    }
    
    private hasApplicationError(results){
        for (result in results){
            if (result.errorMessage){
                return true
            }
            for (item in result.objectResult){
                if (item.hasError()){
                    return true
                }
            }
        }
        return false
    }
    
    private hasResourceErrors(resourceMap){
        boolean hasErrors = false
        for (entry in resourceMap){
            if (entry.value){
                hasErrors = true
                break
            }
        }
        hasErrors
    }
    
    private renderApplicationListingResponse(List resultServices, resourceMap, errorMessage=null){
        renderXml() {
            out << ApplicationManagementResponse() {
                if (errorMessage){
                    out << getFailureXML(errorMessage)
                } else {
                    if (hasResourceErrors(resourceMap)){
                        out << getFailureXML()
                    }else {
                        out << getSuccessXML()
                    }
                    out << getResultXML(resultServices)
                }
                for (entry in resourceMap){
                    StatusResponse() {
                        out << getResourceNameXML(entry.key)
                        if (entry.value){
                            out << getFailureXML(entry.value)
                        }else {
                            out << getSuccessXML()
                        }
                    }
                }
            }
        }
    }
    
    private renderStatusResponse(results, errorMessage){
        renderXml() {
            out << ApplicationManagementResponse() {
                if (errorMessage){
                    out << getFailureXML(errorMessage)
                } else if (hasApplicationError(results)){
                    out << getFailureXML()
                } else {
                    out << getSuccessXML()
                }
                for (result in results){	
                    StatusResponse() {
                        out << getResourceNameXML(result.resourceName)
                        if (result.errorMessage){
                            out << getFailureXML(result.errorMessage)
                        }else {
                            result.objectResult.each {applicationStatus ->
                                if (applicationStatus.hasError()){
                                    out << getFailureXML(applicationStatus.resultMessage)
                                } else {
                                    out << getSuccessXML()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private executeMethod(method, resource, config){
        def errorMessage
        def objectResult
        def resourceName
        
        def liveDataResult = resource.getLiveData(user, method, config)
        resourceName= "$resource.name ($resource.id)"
        if (liveDataResult.hasError()){
            errorMessage = liveDataResult.errorMessage
        }else {
            objectResult = liveDataResult.objectResult
        }
        [objectResult:objectResult, errorMessage:errorMessage, resourceName: resourceName]
    }
    
    private createService(serviceName, hostName, application){
        def newService = new Service()
        newService.setName(serviceName)
        def newHost = new Host()
        newHost.name = hostName
        newHost.applications.add(application)
        newService.hosts.add(newHost)
        newService
    }
    
    private doesListContainName(list, name){
        for (object in list){
            if (object.name == name){
                return true
            }
        }
        return false
    }
    
    private addApplication(serviceResults, serviceName, hostName, application){
        def services = []
        services.addAll(serviceResults)
        if (doesListContainName(services, serviceName)){
            for (service in services){
                if (service.name == serviceName){
                    def hosts = []
                    hosts.addAll(service.hosts)
                    if (doesListContainName(hosts, hostName)){
                        for (host in hosts){
                            if (host.name == hostName && !host.applications.contains(application)){
                                host.applications.add(application)
                            }
                        }
                    }else {
                        def newHost = new Host()
                        newHost.name = hostName
                        newHost.applications.add(application)
                        service.hosts.add(newHost)
                    }
                }
            }
        } else {
            serviceResults.add(createService(serviceName, hostName, application))
        }
    }
    
    private countOtherResources(resourceServiceMap, serviceName, hostName, applicationName, applicationStatus){
        def count = 0
        for (resourceServiceEntry in resourceServiceMap){
            if (resourceServiceEntry.value.containsKey(serviceName)){
                for (serviceHostEntry in resourceServiceEntry.value){
                    if (serviceHostEntry.value.containsKey(hostName)){
                        for (hostAppEntry in serviceHostEntry.value){
                            for (application in hostAppEntry.value){
                                if (application.name == applicationName && application.status == applicationStatus){
                                    count++
                                }
                            }
                        }
                    }
                }
            }
        }
        count
    }
    
    private countOtherResources(resourceServiceMap, serviceName, hostName, applicationName, applicationVersion, applicationStatus){
        def count = 0
        for (resourceServiceEntry in resourceServiceMap){
            if (resourceServiceEntry.value.containsKey(serviceName)){
                for (serviceHostEntry in resourceServiceEntry.value){
                    if (serviceHostEntry.value.containsKey(hostName)){
                        for (hostAppEntry in serviceHostEntry.value){
                            for (application in hostAppEntry.value){
                                if (application.name == applicationName && application.version == applicationVersion && application.status == applicationStatus){
                                    count++
                                }
                            }
                        }
                    }
                }
            }
        }
        count
    }
    
    private aggregate(resourceServiceMap){
        def aggregatedResults = []
        def shMap = [:]
        def resourceCount = resourceServiceMap.size()
        for (resourceServiceEntry in resourceServiceMap){
            for (serviceHostEntry in resourceServiceEntry.value){
                for (hostAppEntry in serviceHostEntry.value){
                    for (application in hostAppEntry.value){
                        def aggregatedApplication = new Application()
                        aggregatedApplication.setName(application.name)
                        aggregatedApplication.setSessionCount(application.sessionCount)
                        
                        def count
                        if(isResourceMultiRevisionCapable(resourceServiceEntry.key)) {
                            count = countOtherResources(resourceServiceMap, serviceHostEntry.key, hostAppEntry.key, application.name, application.version, application.status)
                            aggregatedApplication.setVersion(application.version)
                        } else {
                            count = countOtherResources(resourceServiceMap, serviceHostEntry.key, hostAppEntry.key, application.name, application.status)
                        }
                        
                        def status
                        if (count == resourceCount){
                            status = application.status
                        }else {
                            status = "Mixed"
                        }

                        aggregatedApplication.setStatus(status)
                        addApplication(aggregatedResults, serviceHostEntry.key, hostAppEntry.key, aggregatedApplication)
                    }
                }
            }
        }
        aggregatedResults
    }

    private boolean isResourceMultiRevisionCapable(Resource resource) {
        boolean isGroup = resource.isGroup()
    
        String resourceTypeName
    
        if (!isGroup) {
            resourceTypeName = convertToAppdefResource(resource).appdefResourceType.name
            return !RESOURCE_TYPE_NAME_TC_RUNTIME_6_0.equals(resourceTypeName)
        } else {
            Iterator iterator = resource.getGroupMembers(user).iterator()
            if (iterator.hasNext()) {
                Resource member = iterator.next()
                if (member != null) {
                    return isResourceMultiRevisionCapable(member)
                }
            }
            return false
        }
    }
    
    private filterListResults(resultsMap, serviceName, hostName, applicationName){
        def resourceServiceMap = [:]
        for (entry in resultsMap){
            def serviceHostAppMap = [:]
            def services = filterObjects(entry.value, serviceName)
            for (service in services){
                def hosts = filterObjects(service.hosts, hostName)
                for (host in hosts){
                    def applications = filterObjects(host.applications, applicationName)
                    def hostAppsMap = [:]
                    if (serviceHostAppMap.containsKey(service.name)){
                        hostAppsMap = serviceHostAppMap.get(service.name)
                    }
                    hostAppsMap.put(host.name, applications)
                    serviceHostAppMap.put(service.name, hostAppsMap)
                }
            }
            resourceServiceMap.put(entry.key, serviceHostAppMap)
        }
        resourceServiceMap
    }
    
    private filterObjects(list, name){
        def all = []
        for (item in list){
            if (!name || name == item.name){
                all.add(item)
            }
        }
        all
    }	
    
    def listApplications(parameters){
        try {
            ConfigResponse config= configResponse
            def results = [:]
            def agentErrorMessages = [:]
            def serverErrorMessage
            try {
                def resourceListing = getResourceListing()
                for (resource in resourceListing.resources){ 
                    // Does the user have permissions for the resource? 
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    config.setValue("MULTI_REVISION_CAPABLE", isResourceMultiRevisionCapable(resource))
                    def executeResults = executeMethod("listApplications", resource, config)
                    if (executeResults.errorMessage){
                        agentErrorMessages.put(executeResults.resourceName, executeResults.errorMessage)
                    }else {
                        results.put(resource, executeResults.objectResult)
                        agentErrorMessages.put(executeResults.resourceName, null)
                    }
                }
            }catch(PermissionException pe) {
                serverErrorMessage = "User ${user?.name} does not have the permissions to list applications"
            }
            def filteredResults = filterListResults(results, configResponse.getValue("service"), configResponse.getValue("host"), configResponse.getValue("application"))
            def aggregatedResults = aggregate(filteredResults)
            if (aggregatedResults.isEmpty()) {
                throw new PluginException("There are no applications associated with this resource.")
            }
            renderApplicationListingResponse(aggregatedResults, agentErrorMessages, serverErrorMessage)
        } catch (Exception e) {
            renderStatusResponse(null, e.message)
        }
    }
    
    private getFileName(filePath){
        def fileNameStart = filePath.replace("\\", "/").lastIndexOf("/") +1
        def fileName
        if (fileNameStart > 0){
            fileName = filePath.substring(fileNameStart, filePath.length())
        } else {
            fileName = filePath
        }
        return fileName
    }
    
    private getStreamData(config) {
        def file
        def resourceId
        def fileName=""
        def contextPath
        def service=""
        def host=""
        def parameterMap = [:]
        
        ServletFileUpload upload = servletFileUploadFactory.createServletFileUploader()
        List fileItems  = upload.parseRequest(invokeArgs.request)
        def iter = fileItems.iterator()
        while (iter.hasNext()) {
            def item = iter.next()
            if (item.fieldName == "filename"){
                fileName=getFileName(item.name) 
                file = item.get()
            } else if (item.fieldName == "contextpath"){
                contextPath=item.string
            } else if (item.fieldName == "service"){
                service=item.string
            } else if (item.fieldName == "host"){
                host=item.string
            } else {
                parameterMap.put(item.fieldName, [item.string])
            }
        }
        [file:file, fileName:fileName, contextPath: contextPath, 
                    service:service, host:host, parameterMap:parameterMap]
    }	
    
    private uploadApplicationOntoRemoteSystem(client, resource, streamData, config){
        def liveDataResult = resource.getLiveData(user, "getTemporaryWebAppDirectory", config)
        
        def fileDatas = new FileData[1]
        def applicationLocation = "$liveDataResult.objectResult/$streamData.fileName"
        FileData fileData = new FileData(applicationLocation, streamData.file.size(), 
                FileData.WRITETYPE_CREATEOROVERWRITE)
        fileDatas[0]=fileData
        def inputStream = new ByteArrayInputStream(streamData.file)
        def inputStreams = new InputStream[1]
        inputStreams[0] = inputStream
        def fileDataResults = client.agentSendFileData(fileDatas, inputStreams)
        fileDataResults[0].fileName
    }
    
    def deployApplication(parameters){
        def deployResult
        if (parameters.isEmpty()){
            deployResult = deployLocalApplication()
        }else {
            deployResult = deployRemoteApplication()
        }
        for (resourceResultEntry in deployResult.resourceResults){
            if (!resourceResultEntry.value.errorMessage){
                Bootstrap.getBean(AutoinventoryManager).toggleRuntimeScan(user, resourceResultEntry.key.entityId, true)
            }
        }
        renderStatusResponse(deployResult.resourceResults.values(), deployResult.errorMessage)
    }
    
    private removeTemporaryWarFiles(configs, resources, fileNames){
        int i=0
        for (i; i <  configs.size(); i++){
            configs.get(i).setValue("fileName", fileNames.get(i))
            resources.get(i).getLiveData(user, "removeTemporaryWarFile", configs.get(i))
        }
    }
    
    def deployLocalApplication(){
        def tempResources = []
        def tempFileNames = []
        def tempConfigs = []
        
        def streamData = getStreamData()
        def resourceResults = [:]
        def config = [] as ConfigResponse
        def errorMessage
        def overallStartTime = now()
        def groupResource
        config.setValue("service", streamData.service)
        config.setValue("host", streamData.host)
        def contextPath = streamData.contextPath
        if (!contextPath){
            def fileName = streamData.fileName
            contextPath = fileName.endsWith('.war')?fileName.substring(0, fileName.length() -4):fileName
        }
        config.setValue("contextpath", contextPath)        
        
        try {
            def resourceListing = getResourceListing(streamData.parameterMap)
            groupResource = resourceListing.groupResource
            
            
            for (resource in resourceListing.resources){
                config.setValue("MULTI_REVISION_CAPABLE", isResourceMultiRevisionCapable(resource))
                
                // Does the user have permissions for the resource? 
                AppdefResource convertedResource = convertToAppdefResource(resource)
                convertedResource.checkPerms(operation:'control', user:user)
                
                def startTime = now()
                def uploadResult
                
                def agent = Bootstrap.getBean(AgentManager).getAgent(resource.entityId)
                def agentConnection = Bootstrap.getBean(AgentCommandsClientFactory).getClient(agent)
                
                uploadResult = uploadApplicationOntoRemoteSystem(agentConnection, resource, streamData, config)
                tempConfigs.add(config)
                tempResources.add(resource)
                tempFileNames.add(uploadResult)
                
                if (uploadResult){
                    config.setValue("remotepath", uploadResult)
                }
                
                def executeResult = executeMethod("deployApplication", resource, config)
                updateControlHistory(resource, groupResource, "deployApplication", startTime, now(), getMethodArguments(config), executeResult.errorMessage, hasApplicationError([executeResult]), executeResult.objectResult)
                resourceResults.put(resource, executeResult)
            }
        }catch(PermissionException pe) {
            errorMessage = "User ${user?.name} does not have the permissions to deploy application"
        } catch (Exception e){ 
            errorMessage = e.message
        }
        if (groupResource){
            updateControlHistory(groupResource, null, "deployApplication", overallStartTime, now(), getMethodArguments(config), errorMessage, hasApplicationError(resourceResults.values()), [])
        }
        removeTemporaryWarFiles(tempConfigs, tempResources, tempFileNames)
        [resourceResults:resourceResults, errorMessage:errorMessage]
    }
    
    /**
     * Called by all the non-multipart methods (start, stop, undeploy, deployRemote, reload).
     */
    private runCommand(command){
        def config= configResponse
        def results = []
        def errorMessage
        def overallStartTime = now()
        def groupResource
        try {
            def resourceListing = getResourceListing()
            groupResource = resourceListing.groupResource
            for (resource in resourceListing.resources){ 
                
                // Does the user have permissions for the resource? 
                def convertedResource = convertToAppdefResource(resource)
                convertedResource.checkPerms(operation:'control', user:user)
                
                def startTime = now()
                def executeResult = executeMethod(command, resource, config)
                results.add(executeResult)
                updateControlHistory(resource, groupResource, command, startTime, now(), getMethodArguments(config), executeResult.errorMessage, hasApplicationError([executeResult]), executeResult.objectResult)
            }
        }catch(PermissionException pe) {
            errorMessage = "User ${user?.name} does not have the permissions to control application"
        }catch (Exception e){
            errorMessage = e.message
        }
        if (groupResource){
            updateControlHistory(groupResource, null, command, overallStartTime, now(), getMethodArguments(config), errorMessage, hasApplicationError(results), [])
        }
        renderStatusResponse(results, errorMessage)
    }
    
    def startApplications(parameters){
        runCommand("startApplications")
    }
    
    def stopApplications(parameters){
        runCommand("stopApplications")
    }
    
    def reloadApplications(parameters){
        runCommand("reloadApplications")
    }
    
    def undeployApplications(parameters){
        runCommand("undeployApplications")
    }
    
    private deployRemoteApplication(){
        def config= configResponse
        def resourceResults = [:]
        def errorMessage
        def overallStartTime = now()
        def groupResource
        def contextPath = config.getValue("contextpath")
        if (!contextPath){
            def fileName = config.getValue("remotepath")
            if (fileName){
                fileName = getFileName(fileName)
                contextPath = fileName.endsWith('.war')?fileName.substring(0, fileName.length() -4):fileName
                config.setValue("contextpath", contextPath)
            }
        }                
        
        try {
            def resourceListing = getResourceListing()
            groupResource = resourceListing.groupResource
            
            
            for (resource in resourceListing.resources){ 
                config.setValue("MULTI_REVISION_CAPABLE", isResourceMultiRevisionCapable(resource))
                
                // Does the user have permissions for the resource? 
                def convertedResource = convertToAppdefResource(resource)
                convertedResource.checkPerms(operation:'control', user:user)
                
                def startTime = now()
                def executeResult = executeMethod("deployApplication", resource, config)
                updateControlHistory(resource, groupResource, "deployApplication", startTime, now(), getMethodArguments(config), executeResult.errorMessage, hasApplicationError([executeResult]), executeResult.objectResult)
                resourceResults.put(resource, executeResult)
            }
        }catch(PermissionException pe) {
            errorMessage = "User ${user?.name} does not have the permissions to deploy application"
        }catch (Exception e){
            errorMessage = e.message
        }
        if (groupResource){
            updateControlHistory(groupResource, null, "deployApplication", overallStartTime, now(), getMethodArguments(config), errorMessage, hasApplicationError(resourceResults.values()), [])
        }
        [resourceResults:resourceResults, errorMessage:errorMessage]
    }
    
    private updateControlHistory(resource, groupResource, method, startTime, stopTime, arguments, resultErrorMessage, hasApplicationErrors, resultListing){
        def status = "Successful"
        def errorMessage = ""
        if (resultErrorMessage){
            errorMessage = resultErrorMessage
            status = "Failure occurred"
        }else if (hasApplicationErrors){
            status = "Failure occurred"
            if (!resultListing.isEmpty()){
                for (entry in resultListing){
                    if (entry.hasError()){
                        errorMessage = errorMessage + entry.resultMessage + "\n"
                    }
                }
            }else {
                errorMessage = "See individual resources for detailed errors"
            }
        }
        def groupId = -1
        if (groupResource){
            groupId = groupResource.id
        }
        Bootstrap.getBean(ControlScheduleManager).createHistory(resource.entityId, groupId, -1, user.name, 
                method, truncateText(500,arguments.toString()), false, startTime, stopTime, 0L, status, "None", truncateText(500,errorMessage))
    }
    
    private getMethodArguments(config) {
        def keys = config.getKeys()
        def methodArguments = []
        for (int i = 1; i <= keys.size(); i++) {
            if (keys.contains("application" + i)) {
                methodArguments.add(config.getValue("application" + i))
            }
        }
        if (keys.contains("contextpath")){
            methodArguments.add(config.getValue("contextpath"))
        }
        if (keys.contains("remotepath")){
            methodArguments.add(config.getValue("remotepath"))
        }
        return methodArguments
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

    private getResourceListing(parameterMap=null) throws PluginException {
        def allMembers = []
        def groupResource
        def queryParameter = ""
        if (!parameterMap){
            parameterMap = invokeArgs.request.parameterMap
        }
        try {
            if (isServerOperation(parameterMap)) {
                allMembers.add(getServer(parameterMap))
            } else if (isGroupOperation(parameterMap)) {
                def group = getGroup(parameterMap)
                if (group.resources.isEmpty()) {
                    throw new PluginException("The group '${group.name}' does not contain any resources.")
                }
                allMembers.addAll(getGroup(parameterMap).resources)
            } else {
                throw new PluginException("A valid server id, server name, group id, or group name was not specified.")
            }
        }catch (PluginException p){
            throw p
        } catch (Exception e){
            throw new PluginException("Unable to find resource from parameter: " 
            + queryParameter + ". Error message: " + e.message)
        }
        [resources:allMembers, groupResource:groupResource]
    }
}
