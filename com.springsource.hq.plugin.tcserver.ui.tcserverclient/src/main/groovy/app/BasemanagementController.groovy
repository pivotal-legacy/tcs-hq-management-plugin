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

import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.product.PluginException
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.authz.shared.PermissionException

import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory

class BasemanagementController extends BaseController {

    protected Closure getFailureXML(reason=null) { { doc ->
            Status("Failure")
            if (reason){
                Error() { ReasonText(reason) }
            }
        }
    }
    
    protected Closure getSuccessXML() { { doc ->  Status("Success") }
    }
      
    protected parseMultipartRequest(request) {
        def params = []
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory())
        upload.parseRequest(request).each { 
            params[it.fieldName] = it.string
        }
        return params
    }

    protected getServer(parameterMap = null) throws PluginException {
        if (!parameterMap){
            parameterMap = invokeArgs.request.parameterMap
        }
        def server
        if (parameterMap.serverid) {
            def serverId = parameterMap.serverid[0].toInteger()
            server = getServerById(serverId)
        }else if (parameterMap.servername){
            def serverName = parameterMap.servername[0]
            def servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 6.0'])
            for (candidate in servers) {
                if(candidate.name.equals(serverName)) {
                    server = candidate
                    break
                }
            }
            servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 7.0'])
            for (candidate in servers) {
                if(candidate.name.equals(serverName)) {
                    server = candidate
                    break
                }
            }
            if (!server) {
                throw new PluginException("The server name '${serverName}' did not match any server resource.")
            }
        } else {
            throw new PluginException("A valid server id or name was not specified.")
        }
        return server
    }
    
    protected getServerById(int serverId) {
        def servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 6.0'])
        def server
        for (candidate in servers) {
            if(candidate.id == serverId) {
                server = candidate
                break
            }
        }
        servers = resourceHelper.findByPrototype([byPrototype:'SpringSource tc Runtime 7.0'])
        for (candidate in servers) {
            if(candidate.id == serverId) {
                server = candidate
                break
            }
        }
        if (!server) {
            throw new PluginException("The server id '${serverId}' did not match any server resource.")
        }
        return server
    }
    
    protected getGroup(parameterMap = null) throws PluginException {
        if (!parameterMap){
            parameterMap = invokeArgs.request.parameterMap
        }
        def group
        if (parameterMap.groupid) {
            def groupId = parameterMap.groupid[0].toInteger()
            group = getGroupById(groupId)
        }else if (parameterMap.groupname){
            def groupName = parameterMap.groupname[0]
            group = Bootstrap.getBean(ResourceGroupManager).findResourceGroupByName(user, groupName)
                
            if (!group) {
                throw new PluginException("The group name '${groupName}' did not match any group.")
            }                
        } else {
            throw new PluginException("A valid group id or name was not specified.")
        }
        return group
    }
    
    protected getGroupById(int groupId) {
        def group
        try {
            group = resourceHelper.findGroup(groupId)
        } catch (PermissionException pe) {
            throw pe
        } catch (any) {
            throw new PluginException("The group id '${groupId}' did not match any group.")
        }       
        
        if (!group) {
            throw new PluginException("The group id '${groupId}' did not match any group.")
        }
        return group
    }
    
    def index(params) {
        def file = new File(getPluginDir(), "public")
        def zipFile
        def listing = file.list()
        for (fileName in listing){
            if (fileName.endsWith(".zip") && fileName.startsWith("pivotal-tcserver-scripting-client-")){
                zipFile = fileName
                break
            }
        }
        
        render(locals:[plugin: getPlugin(), fileName:zipFile ] )
    }
    
    /**
     * Utility method to convert an AppdefResource to a Resource for the 
     * purposes of permission checking. 
     */
    def convertToAppdefResource(resource) {
        if (resource){
            if (resource.isPlatform()) {
                return resource.toPlatform()
            } else if (resource.isServer()) {
                return resource.toServer()
            } else if (resource.isService()) {
                return resource.toService()
            } else {
                throw new Exception("Unable to convert resource: $resource")
            }
        } else {
            throw new Exception("Unable to invoke command on null resource")
        }
    }

    protected boolean isGroupOperation(params) {
        params['groupid'] || params['groupname']
    }

    protected boolean isServerOperation(params) {
        params['serverid'] || params['servername']
    }
}
