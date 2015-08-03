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

import javax.servlet.http.HttpServletResponse

import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.autoinventory.ScanConfigurationCore
import org.hyperic.hq.autoinventory.ServerSignature
import org.hyperic.hq.autoinventory.shared.AutoinventoryManager
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.control.shared.ControlScheduleManager
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.util.config.ConfigResponse

import com.springsource.hq.plugin.tcserver.util.control.ControlActionFailedException
import com.springsource.hq.plugin.tcserver.util.control.ControlActionHelper
import com.sun.xml.internal.ws.transport.http.client.HttpResponseProperties;
import com.thoughtworks.xstream.XStream

class TomcatserverconfigController extends BaseController {
    
    private static final String QUERY_PARAMETER_CSRF_NONCE = "org.apache.catalina.filters.CSRF_NONCE"
    
    private final ControlActionHelper controlActionHelper = new ControlActionHelper()
	
	def index(params) {
		if(viewedResource.isGroup()) {
			render(inline: "This functionality is not available for Groups.")
			return
		}
		def readOnly = isResourceReadOnly(viewedResource, user)
		def username = invokeArgs.user.name
		log.info("Setting param readOnly=$readOnly")
		log.info("Setting param username=$username")
		render(locals:[ 
				eid: params.getOne('eid'),
				sessionId: invokeArgs.request.session.id,
				readOnly: readOnly,
				username: username,
                csrfNonce: params.getOne(QUERY_PARAMETER_CSRF_NONCE)
				])                
    }
	
	protected void init() {
		setXMLMethods(['getConfiguration'])
	}
	
	/**
	 * Retrieve the configuration for the server.
	 */
	def getConfiguration(params){
		def config = [] as ConfigResponse
		def httpResponse = invokeArgs.response
		
		try {
			// Does the user have permissions for the resource?
			def convertedResource = convertToAppdefResource(viewedResource)
			convertedResource.checkPerms(operation:'view', user:user)
			
			def liveDataResult = viewedResource.getLiveData(user, "getConfiguration", config)
			def errorMessage = "";
			if (liveDataResult.errorMessage){
				httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Agent exception occurred on " + viewedResource.name + ": " + liveDataResult.errorMessage);
			}else {
				httpResponse.setContentType("text/xml")
				def xstream = new XStream()
				xstream.toXML(liveDataResult.objectResult, httpResponse.outputStream)
			}
		} catch (PermissionException pe) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"Unable to access resource [" 
					+ viewedResource.name 
					+ "]: Invalid permissions");
		}
	}
	/**
	 * This is a light weight hook which allows the tomcatserverconfig Spring MVC app to test session status.
	 * If the session is expired, then HQU will send a redirect back to the caller.
	 * @param params
	 * @return
	 */
	def checkSessionStatus(params) {
		invokeArgs.response.setStatus(HttpServletResponse.SC_OK)
	}
	
	private getDataStreamAsString(){
		def contentType = invokeArgs.request.contentType
		def stringBuilder = new StringBuilder()
		def errorMessage
		if (contentType != null && (contentType.contains("text/xml") || contentType.contains("text/plain"))){
			def reader = new BufferedReader(invokeArgs.request.reader)
			def lineRead
			while ((lineRead = reader.readLine())!=null) {
				stringBuilder.append(lineRead + "\n")
			}
		} else {
			errorMessage = "Invalid media type:" + contentType
			invokeArgs.response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, errorMessage)
		}
		[resultString:stringBuilder.toString(), errorMessage: errorMessage]
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
	
	private sendToAgent(config, methodName){
		def startTime = now()
		def liveDataResult = viewedResource.getLiveData(user, methodName, config)
		def errorMessage = null
		def status
		if (liveDataResult.errorMessage){
			errorMessage = "Agent exception occurred on " + viewedResource.name + ": " + liveDataResult.errorMessage
			invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
			status = "Error"
		}else {
			invokeArgs.response.setStatus(HttpServletResponse.SC_OK)
			status = "Successful"
		}
		Bootstrap.getBean(ControlScheduleManager.class).createHistory(viewedResource.entityId, -1, -1, user.name, 
				methodName, "None", false, startTime, now(), 0L, status, "None", truncateText(500,errorMessage))
	}
	
	def saveConfiguration(params){
		def httpResponse = invokeArgs.response
		
		try {
			// Does the user have permissions for the resource?
			def convertedResource = convertToAppdefResource(viewedResource)
			convertedResource.checkPerms(operation:'modify', user:user)
			
			def config = [] as ConfigResponse
			def streamResult = dataStreamAsString
			if (streamResult.errorMessage){
				Bootstrap.getBean(ControlScheduleManager.class).createHistory(viewedResource.entityId, -1, -1, user.name, 
						"saveConfiguration", "None", false, now(), now(), 0L, "Error", "None", streamResult.errorMessage)
			}else {
				config.setValue("SETTINGS", streamResult.resultString)
				sendToAgent(config, "saveConfiguration")
			}
            httpResponse.writer.print(getCsrfNonce(httpResponse))
            setRendered(true)
		} catch (PermissionException pe) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"Unable to access resource [" 
					+ viewedResource.name 
					+ "]: Invalid permissions");
		}
	}
	
	def saveConfigurationFile(params){
		def httpResponse = invokeArgs.response
		
		try {
			// Does the user have permissions for the resource?
			def convertedResource = convertToAppdefResource(viewedResource)
			convertedResource.checkPerms(operation:'modify', user:user)
			
			def config = [] as ConfigResponse
			def uploadStreamData = getStreamData()
			def fileName = uploadStreamData.fileName
			def file = uploadStreamData.file
			if (!(file && fileName)){
				Bootstrap.getBean(ControlScheduleManager.class).createHistory(viewedResource.entityId, -1, -1, user.name, 
						"putFile", fileName, false, now(), now(), 0L, "Error", "None", "Upload error")
				throw new Exception("file and fileName are required parameters")
			} else {
				config.setValue("FILE_NAME", fileName)
				config.setValue("FILE_DATA", file)
				sendToAgent(config, "putFile")
			}
            httpResponse.writer.print(getCsrfNonce(httpResponse))
			setRendered(true)
			invokeArgs.response.setStatus(HttpServletResponse.SC_OK)
		} catch (PermissionException pe) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"Unable to access resource [" 
					+ viewedResource.name 
					+ "]: Invalid permissions");
		}catch(Exception e){
			invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to save file: " + e.message);
		}
	}
	
	private getStreamData() {
		def fileName
		def file
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory())
		List fileItems  = upload.parseRequest(invokeArgs.request)
		def iter = fileItems.iterator()
		while (iter.hasNext()) {
			def item = iter.next()
			if (item.fieldName.equals("file")){
				file = item.string
			} else if (item.fieldName.equals("fileName")){
				fileName = item.string
			}
		}
		[fileName:fileName, file:file]
	}
	
	private executeAutoDiscovery(resource, user){
		ScanConfigurationCore scanConfigurationCore = new ScanConfigurationCore()
		ServerSignature tcRuntime6ServerConfig = new ServerSignature();
		tcRuntime6ServerConfig.setServerTypeName("SpringSource tc Runtime 6.0");
        ServerSignature tcRuntime7ServerConfig = new ServerSignature();
        tcRuntime7ServerConfig.setServerTypeName("SpringSource tc Runtime 7.0");
        ServerSignature tcRuntime8ServerConfig = new ServerSignature();
        tcRuntime7ServerConfig.setServerTypeName("Pivotal tc Runtime 8.0");
		ServerSignature[] signatureArray = new ServerSignature[3]
		signatureArray[0] = tcRuntime6ServerConfig
        signatureArray[1] = tcRuntime7ServerConfig
        signatureArray[2] = tcRuntime8ServerConfig
		scanConfigurationCore.setServerSignatures(signatureArray)
		Bootstrap.getBean(AutoinventoryManager.class).startScan(user, resource.getPlatform().entityId, scanConfigurationCore, null, null, null)
	}
	
	/**
	 * Restarts the servers and blocks for a result. If a timeout occurs the error
	 * is fed to the {@link HttpServletResponse#sendError} method. 
	 */
	def restartServer(params){		
		try {
            this.controlActionHelper.restartServer(user, viewedResource, false)            
            performAutoDiscoveryIfNecessary(user, viewedResource, invokeArgs)
            invokeArgs.response.writer.print(getCsrfNonce(invokeArgs.response))
            setRendered(true)
		} catch (ControlActionFailedException cafe) {
            invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to restart $viewedResource.name: $cafe.message")
        } catch (PermissionException pe) {
            invokeArgs.response.sendError(HttpServletResponse.SC_FORBIDDEN, "$user.name does not have necessary permissions to restart server $viewedResource.name")
        }
	}
    
    private def performAutoDiscoveryIfNecessary(user, viewResource, invokeArgs) {
        try {
             if (invokeArgs.request.parameterMap.getOne("isJmxListenerChanged")?.toBoolean()) {       
                 executeAutoDiscovery(viewedResource, user)
             }
        } catch (PermissionException pe) {
            invokeArgs.response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Restart completed. Failed to perform auto-discovery scan: the user '" + user.name + "' does not have the necessary permissions on platform '" + viewedResource.platform.name + "'.");                                  } catch (Exception e) {
                invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Restart completed. Failed to perform auto-discovery scan: " + e.message)
		}
	}
	
	def revertToPreviousConfiguration(params){
		def httpResponse = invokeArgs.response        
		
		try {
			// Does the user have permissions for the resource?
			def convertedResource = convertToAppdefResource(viewedResource)
			convertedResource.checkPerms(operation:'modify', user:user)
			def config = [] as ConfigResponse
			sendToAgent(config, "revertToPreviousConfiguration")
                        
			httpResponse.writer.print(getCsrfNonce(httpResponse))
            setRendered(true)
		} catch (PermissionException pe) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"Unable to access resource [" 
					+ viewedResource.name 
					+ "]: Invalid permissions");
		}catch(Exception e){
			invokeArgs.response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to revert configuration: " + e.message);
		}
	}
    
    private String getCsrfNonce(def response) {
        String encodedUrl = response.encodeURL("/");
        int index = encodedUrl.indexOf(QUERY_PARAMETER_CSRF_NONCE);
        
        String csrfNonce = null;
        
        if (index >= 0) {
            csrfNonce = encodedUrl.substring(index + QUERY_PARAMETER_CSRF_NONCE.length() + 1);
            
            index = csrfNonce.indexOf("&");
            
            if (index >= 0) {
                csrfNonce = csrfNonce.substring(0, index);
            }
        }
        
        return csrfNonce;        
    }
	
	/**
	 * Determines if a resource is read-only for a given user. Currently only 
	 * handles the modify operation. 
	 */
	private isResourceReadOnly(resource, user) {
		def readOnly = true
		
		try {
			def convertedResource = convertToAppdefResource(viewedResource)
			convertedResource.checkPerms(operation:'modify', user:user)
			readOnly = false
		} catch (PermissionException pe) {
			// Do nothing because readOnly is true by default 
		}
		return readOnly
	}
	
	/**
	 * Utility method to convert an AppdefResource to a Resource for the 
	 * purposes of permission checking. 
	 */
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
}

