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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.codec.binary.Base64
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload

import org.hyperic.hq.agent.client.AgentCommandsClient
import org.hyperic.hq.agent.client.AgentCommandsClientFactory
import org.hyperic.hq.agent.FileData
import org.hyperic.hq.agent.AgentRemoteException
import org.hyperic.hq.product.ProductPlugin

import org.hyperic.hq.appdef.shared.AgentManager
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.control.shared.ControlScheduleManager
import org.hyperic.hq.product.PluginException
import org.hyperic.util.config.ConfigResponse
import org.hyperic.hq.authz.server.session.ResourceGroup
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper;

import com.thoughtworks.xstream.XStream

class ServerconfigController extends BasemanagementController{
    
    def getFile(params){
        def taskResponse = [:]
        
        try {
            params = normalizeParams(params)
            if (isServerOperation(params)) {
                // Does the user have permissions for the resource?
                def convertedResource = convertToAppdefResource(server)
                convertedResource.checkPerms(operation:'view', user:user)
                
                taskResponse = doGetFile(server, params['file'])
            }
            else {
                throw new Exception('A specific server must be specified')
            }
        } catch (PermissionException pe) {
            failureResponse("User ${user?.name} does not have the permissions to get file")
        } catch (Exception e) {
            taskResponse.message = e.message
        }
        
        setRendered(true)
        if (taskResponse.message) {
            invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, taskResponse.message)
        }
        else {
            HttpServletResponse response = invokeArgs.response
            response.setContentType('application/octet-stream')
            response.outputStream.write(Base64.decodeBase64(taskResponse.fileData.bytes))
            response.outputStream.close()
        }
    }
    
    def putFile(params) {
        def taskResponses
        def overallStatus
        def overallMessage
        
        try {
            params = parseMultipartRequest(invokeArgs.request)
            if (isGroupOperation(params)) {
                taskResponses = putFileGroup(getGroupId(params), params['filename'], params['targetfile'], params['nobackupfile'])
            }
            else if (isServerOperation(params)) {
                taskResponses = putFileServer(getServerId(params), params['filename'], params['targetfile'], params['nobackupfile'])
            }
            else {
                throw new Exception('A specific server or group must be specified')
            }
        }
        catch (Exception e) {
            overallStatus = 'Failure'
            overallMessage = e.message
        }
        
        if (!overallStatus) {
            overallStatus = taskResponses.any { it.status == 'Failure' } ? 'Failure' : 'Success'
        }
        
        renderXml {
            ConfigurationStatusResponse {
                Status(overallStatus)
                if (overallMessage) {
                    Error { ReasonText(overallMessage) }
                }
                if (taskResponses) {
                    taskResponses.each { taskResponse ->
                        StatusResponse {
                            Status(taskResponse.status)
                            if (taskResponse.message) {
                                Error { ReasonText(taskResponse.message) }
                            }
                            ResourceName(taskResponse.resource)
                        }
                    }
                }
            }
        }
    }
    
    def getJvmOptions(params) {
        def taskResponse
        def overallStatus
        def overallMessage
        
        try {
            taskResponse = doGetJvmOptions(getServer(params))
        }
        catch (Exception e) {
            overallStatus = 'Failure'
            overallMessage = e.message
        }
        
        if (!overallStatus && taskResponse) {
            overallStatus = taskResponse.status
            overallMessage = taskResponse.message
        }
        else if (!overallStatus) {
            overallStatus = 'Failure'
            overallMessage = 'expected serverid or servername parameters'
        }
        
        renderXml {
            JvmOptionsResponse {
                Status(overallStatus)
                if (overallMessage) {
                    Error { ReasonText(overallMessage) }
                }
                JvmOptions {
                    if (taskResponse && taskResponse.jvmOptions) {
                        taskResponse.jvmOptions.each { Option(it) }
                    }
                }
            }
        }
    }
    
    def putJvmOptions(params) {
        def taskResponses
        def jvmOptions = new ArrayList()
        def overallStatus
        def overallMessage
        
        try {
            params = parseMultipartRequest(invokeArgs.request)
            def requestXml = new XmlParser().parseText(params.postdata)
            requestXml.JvmOptions[0].Option.each {
                jvmOptions.add it.text()
            }
            if (requestXml.name() == 'JvmOptionsGroupRequest') {
                def groupid
                if (requestXml.Group[0].'@id' && requestXml.Group[0].'@id' != '0') {
                    groupid = requestXml.Group[0].'@id'.toInteger()
                }
                else if (requestXml.Group[0].'@name' && requestXml.Group[0].'@name' != '') {
                    groupid = groupIdForName(requestXml.Group[0].'@name')
                }
                else {
                    throw new Exception('A specific group must be specified')
                }
                taskResponses = putJvmOptionsGroup(groupid, jvmOptions)
            }
            else if (requestXml.name() == 'JvmOptionsRequest') {
                def serverid
                if (requestXml.Resource[0].'@id' && requestXml.Resource[0].'@id' != '0') {
                    serverid = requestXml.Resource[0].'@id'.toInteger()
                }
                else if (requestXml.Resource[0].'@name' && requestXml.Resource[0].'@name' != '') {
                    serverid = serverIdForName(requestXml.Resource[0].'@name')
                }
                else {
                    throw new Exception('A specific server must be specified')
                }
                taskResponses = putJvmOptionsServer(serverid, jvmOptions)
            }
            else {
                throw new Exception('A specific server or group must be specified')
            }
        }
        catch (Exception e) {
            overallStatus = 'Failure'
            overallMessage = e.message
        }
        
        if (!overallStatus) {
            overallStatus = taskResponses.any { it.status == 'Failure' } ? 'Failure' : 'Success'
        }
        
        renderXml {
            JvmOptionsResponse {
                Status(overallStatus)
                if (overallMessage) {
                    Error { ReasonText(overallMessage) }
                }
                if (taskResponses) {
                    taskResponses.each { taskResponse ->
                        StatusResponse {
                            Status(taskResponse.status)
                            if (taskResponse.message) {
                                Error { ReasonText(taskResponse.message) }
                            }
                            ResourceName(taskResponse.resource)
                        }
                    }
                }
            }
        }
    }
    
    def revertToPreviousConfiguration(params){
        def response = [:]
        try {
            // Does the user have permissions for the resource?
            def theServer = server

            def convertedResource = convertToAppdefResource(theServer)
            convertedResource.checkPerms(operation:'modify', user:user)
            
            response.resource = theServer.name;
            
            def config = [] as ConfigResponse

            def liveDataResult = theServer.getLiveData(user, "revertToPreviousConfiguration", config)

            if (liveDataResult.errorMessage) {
                response.status = 'Failure'
                response.message = liveDataResult.errorMessage
            }
            else {
                response.status = 'Success'
                response.resource = theServer.name
            }
        } catch (PermissionException pe) {
            failureResponse("User ${user?.name} does not have the permissions to revert to the previous configuration.")
        }catch(Exception e){
            response.status = 'Failure'
            response.message = e.message
        }
        
        renderXml {
            ConfigurationStatusResponse {
                Status(response.status)
                if (response.message) {
                    Error { ReasonText(response.message) }
                }
                if (response.resource) {
                    StatusResponse {
                        Status(response.status)
                        if (response.message) {
                            Error { ReasonText(response.message) }
                        }
                        ResourceName(response.resource)
                    }
                }
            }
        }
    }
    
    private doGetFile(server, file) {
        def response = [:]
        try {
            // Does the user have permissions for the resource?
            def convertedResource = convertToAppdefResource(server)
            convertedResource.checkPerms(operation:'view', user:user)
            
            ConfigResponse config = new ConfigResponse()
            config.setValue('FILE_NAME', file)
            def liveDataResult = server.getLiveData(user, "getFile", config)
            if (liveDataResult.errorMessage) {
                response.status = 'Failure'
                response.message = liveDataResult.errorMessage
                response.resource = server.name
            }
            else {
                response.status = 'Success'
                response.resource = server.name
                response.fileData = liveDataResult.objectResult
            }
        } catch (PermissionException pe) {
            failureResponse("User ${user?.name} does not have the permissions to get file")
        } catch (Exception e) {
            response.status = 'Failure'
            response.message = e.message
        }
        return response
    }
    
    private putFileGroup(groupid, file, targetfile, nobackupfile) throws PluginException {
        def group = getGroupById(groupid)
        
        def responses = [];
        def startTime = now();
        def hasError = false;
        try {
            group.resources.each { server ->
                responses.add doPutFile(server, groupid, file, targetfile, nobackupfile)
            }
        }
        catch (Exception e) {
            hasError = false
            throw e
        }
        finally {
            def status = hasError ? 'Failure' : responses.any { it.status == 'Failure' } ? 'Failure' : 'Success'
            logControlOperation(group, null, 'putFile', targetfile, startTime, status, null)
        }
        return responses
    }
    
    private putFileServer(serverId, file, targetfile, nobackupfile) {
        def server = getServerById(serverId)
        def responses = []
        responses.add doPutFile(server, null, file, targetfile, nobackupfile)
        return responses
    } 
    
    private doPutFile(server, groupid, file, targetFile, nobackupfile) {
        def response = [:]
        def startTime = now()
        try {
            // Does the user have permissions for the resource?
            // Requires modify *and* view perms 
            def convertedResource = convertToAppdefResource(server)
            convertedResource.checkPerms(operation:'modify', user:user)

            ConfigResponse config = new ConfigResponse()
            config.setValue('FILE_NAME', targetFile)
            config.setValue('nobackupfile', Boolean.parseBoolean(nobackupfile))
            def liveDataResult = server.getLiveData(user, 'prepareFile', config)
            if (liveDataResult.errorMessage) {
                response.status = 'Failure'
                response.message = liveDataResult.errorMessage
                response.resource = server.name
            }
            else {
                def resultData = transferFileToResource(server, targetFile, file, config)
                if (resultData.errorMessage){
                    response.status = 'Failure'
                    response.message = resultData.errorMessage
                    response.resource = server.name
                }else {
                    response.status = 'Success'
                    response.resource = server.name
                }
            }
        } catch (PermissionException pe) {
            failureResponse("User ${user?.name} does not have the permissions to put file")
        } catch (Exception e) {
            response.status = 'Failure'
            response.message = e.message
        }
        finally {
            logControlOperation(server, groupid, 'putFile', targetFile, startTime, response.status, response.message)
        }
        println "RESPONSE= " + response
        return response
    }
    
    private transferFileToResource(resource, fileLocation, file, config){
        def agent = Bootstrap.getBean(AgentManager).getAgent(resource.entityId)
        def agentConnection = Bootstrap.getBean(AgentCommandsClientFactory).getClient(agent)
        def errorMessage
        def installPath = resource.config.installPath.value
        def fileDatas = new FileData[1]
        def fullPath = ("$installPath/$fileLocation").replace("\\","/")
        FileData fileData = new FileData(fullPath, file.length, 
                FileData.WRITETYPE_CREATEOROVERWRITE)
        fileDatas[0]=fileData
        def inputStream = new ByteArrayInputStream(file)
        def inputStreams = new InputStream[1]
        inputStreams[0] = inputStream
        try {
            agentConnection.agentSendFileData(fileDatas, inputStreams)
        }catch (Exception e){
            println "ERROR = $e.message"
            errorMessage = e.message
        }
        [errorMessage: errorMessage]
    }
    
    private putJvmOptionsGroup(groupid, jvmOptions) {
        def group = getGroupById(groupid)
        def responses = []
        def startTime = now()
        def hasError = false

        try {
            group.resources.each { server ->
                responses.add doPutJvmOptions(server, groupid, jvmOptions)
            }
        }
        catch (Exception e) {
            hasError = true
            throw e
        }
        finally {
            def status = hasError ? 'Failure' : responses.any { it.status == 'Failure' } ? 'Failure' : 'Success'
            def optsStr = new StringBuilder()
            jvmOptions.each {
                if (optsStr.length() != 0) {
                    optsStr.append(' ')
                }
                optsStr.append(it)
            }
            logControlOperation(group, null, 'putJvmOptions', optsStr.toString(), startTime, status, null)
        }
        return responses
    }
    
    private putJvmOptionsServer(serverid, jvmOptions) {
        def server = getServerById(serverid)
        def responses = []
        responses.add doPutJvmOptions(server, null, jvmOptions)
        return responses
    }
    
    private doGetJvmOptions(server) {
        def response = [:]
        try {
            // Does the user have permissions for the resource?
            def convertedResource = convertToAppdefResource(server)
            convertedResource.checkPerms(operation:'view', user:user)
            
            ConfigResponse config = new ConfigResponse()
            def liveDataResult = server.getLiveData(user, 'getJvmOptions', config)
            if (liveDataResult.errorMessage) {
                response.with {
                    status = 'Failure'
                    message = liveDataResult.errorMessage
                    resource = server.name
                }
            }
            else {
                response.with {
                    status = 'Success'
                    resource = server.name
                    jvmOptions = liveDataResult.objectResult
                }
            }
        } catch (PermissionException pe) {
            failureResponse("User " + user?.name + " does not have the permissions to get JVM options")
        } catch (Exception e) {
            response.status = 'Failure'
            response.message = e.message
        }
        return response
    }
    
    private doPutJvmOptions(server, groupid, jvmOptions) {
        def response = [:]
        def startTime = now()
        try {
            // Does the user have permissions for the resource?
            // Requires modify *and* view perms 
            def convertedResource = convertToAppdefResource(server)
            convertedResource.checkPerms(operation:'modify', user:user)
            
            ConfigResponse config = new ConfigResponse()
            config.setValue('JVM_OPTS', new XStream().toXML(jvmOptions))
            def liveDataResult = server.getLiveData(user, 'putJvmOptions', config)
            if (liveDataResult.errorMessage) {
                response.with {
                    status = 'Failure'
                    message = liveDataResult.errorMessage
                    resource = server.name
                }
            }
            else {
                response.status = 'Success'
                response.resource = server.name
            }
        } catch (PermissionException pe) {
            failureResponse("User " + user?.name + " does not have the permissions to set JVM options")
        } catch (Exception e) {
            response.status = 'Failure'
            response.message = e.message
        }
        finally {
            def optsStr = new StringBuilder()
            jvmOptions.each {
                if (optsStr.length() != 0) {
                    optsStr.append(' ')
                }
                optsStr.append(it)
            }
            logControlOperation(server, groupid, 'putJvmOptions', optsStr.toString(), startTime, response.status, response.message)
        }
        return response
    }
    
    private int getGroupId(params) {
        params['groupid'] ? params['groupid'].toInteger() : groupIdForName(params['groupname'])
    }
    
    private int getServerId(params) {
        params['serverid'] ? params['serverid'].toInteger() : serverIdForName(params['servername'])
    }
    
    private int serverIdForName(String servername) {
        try {
            def servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 6.0'])
            for (server in servers) {
                if(server.name == servername) {
                    return server.id
                }
            }
            servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 7.0'])
            for (server in servers) {
                if(server.name == servername) {
                    return server.id
                }
            }
        }
        catch (Exception e) {
            throw new Exception("The server name '${servername}' did not match any server resource.")
        }
    }
    
    private int groupIdForName(String groupname) {
        try {
            return Bootstrap.getBean(ResourceGroupManager).findResourceGroupByName(user, groupname).id
        }
        catch (Exception e) {
            throw new Exception("The group name '${groupname}' did not match any group.")
        }
    }
    
    private normalizeParams(params) {
        def normParms = [:]
        params.each {
            if (it.value.length == 1) {
                normParms[it.key] = it.value[0]
            }
            else {
                normParms[it.key] = it.value
            }
        }
        return normParms
    }
    
    private parseMultipartRequest(HttpServletRequest request) {
        def params = [:]
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory())
        upload.parseRequest(request).each {
            if (it.fieldName == 'filename') {
                params[it.fieldName] = it.get()
            }
            else {
                params[it.fieldName] = it.string
            }
        }
        return params
    }
    
    private logControlOperation(resource, groupid, action, description, startTime, status, errorMessage) {
        try {
            def entityId = (resource instanceof ResourceGroup) ? resource.resource.entityId : resource.entityId
            def descriptionText = description ? truncateText(500, description.toString()) : ''
            def errorText = errorMessage ? truncateText(500, errorMessage.toString()) : ''
            def group = groupid ? groupid : -1
            Bootstrap.getBean(ControlScheduleManager).createHistory(entityId, group, -1, user.name, action, descriptionText, false, startTime, now(), 0L, status, 'None', errorText)
        }
        catch (Exception e) {
            // ignore
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
}
