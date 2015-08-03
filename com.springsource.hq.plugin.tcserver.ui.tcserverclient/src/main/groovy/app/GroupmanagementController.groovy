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

import java.util.concurrent.Future;

import org.hyperic.hq.control.ControlActionResult;
import org.hyperic.hq.authz.server.session.Resource;
import org.hyperic.hq.authz.server.session.ResourceGroup
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.control.shared.ControlManager
import org.hyperic.hq.control.shared.ControlConstants
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.authz.server.session.ResourceGroup.ResourceGroupCreateInfo
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.appdef.shared.AppdefEntityConstants
import org.hyperic.hq.appdef.shared.AppdefEntityTypeID
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.common.NotFoundException

class GroupmanagementController extends BasemanagementController{
    
    //	This needs to be slightly longer than agent's timeout - default is 90 seconds
    private static final int CONTROL_TIMEOUT = 90000
    
    private final ResourceGroupManager resourceGroupManager = (ResourceGroupManager)Bootstrap.getBean(ResourceGroupManager)
    
    private final ResourceManager resourceManager = (ResourceManager)Bootstrap.getBean(ResourceManager)
    
    def addServer(params){
        try {        
            if (this.resourceGroupManager.isMember(group, server)) {
                failureResponse("Cannot add server '${serverIdentifier}' to group '${groupIdentifier}' as it is already a member of the group")
            } else {
                if (group.getResourcePrototype() != server.getPrototype()) {
                    failureResponse("Cannot add server '${serverIdentifier}' to group '${groupIdentifier}' as it is not compatible with the group's type")   
                } else {
                    this.resourceGroupManager.addResource(user, group, server)
                    successResponse()
                }
            }                
        }catch(Exception e) {
            failureResponse(e.message)
        }
    }
    
    def removeServer(params) {
        try {              
            this.resourceGroupManager.removeResources(user,group, [server])
            successResponse()     
        }catch(Exception e) {
            failureResponse(e.message)
        }
    }
    
    def create(params) {
        def parameterMap = invokeArgs.request.parameterMap
        if(parameterMap.name) {            
            def groupVersion = getGroupVersion(parameterMap)             
            try {
                def serverType = Bootstrap.getBean(ServerManager).findServerTypeByName("SpringSource tc Runtime ${groupVersion}")
                def prototype =  Bootstrap.getBean(ResourceManager).findResourcePrototype(new AppdefEntityTypeID(AppdefEntityConstants.APPDEF_TYPE_SERVER, serverType.id))
                ResourceGroupCreateInfo info =
                        new ResourceGroupCreateInfo(parameterMap.name[0], getGroupDescription(parameterMap), AppdefEntityConstants.APPDEF_TYPE_GROUP_COMPAT_PS, prototype,
                        getGroupLocation(parameterMap), 0, false, false)
                this.resourceGroupManager.createResourceGroup(user, info, [], [])
                successResponse()
            } catch (NotFoundException nfe) {
                failureResponse("'${groupVersion}' is not a valid version")
            } catch (Exception e) {
                failureResponse(e.message)
            }
        }else {
            failureResponse("A group name must be specified")
        }
    }
    
    def getGroupDescription(parameterMap) {
        if(parameterMap.description) {
            return parameterMap.description[0]
        } else {
            return ""
        }
    }
    
    def getGroupLocation(parameterMap) {
        if(parameterMap.location) {
            return parameterMap.location[0]
        } else {
            return ""
        }
    }
    
    def getGroupVersion(parameterMap) {
        if (parameterMap.version) {
            return parameterMap.version[0]
        } else {
            return "6.0"
        }
    }
    
    def delete(params) {
        try {
            this.resourceGroupManager.removeResourceGroup(user,group)
            successResponse()
        }catch(Exception e) {
            failureResponse(e.message)
        }
    }
    
    def list(params) {
        try {
            def tcRuntimeGroups = this.resourceGroupManager.getCompatibleResourceGroups(user, getTcRuntime60ResourcePrototype())
            tcRuntimeGroups.addAll(this.resourceGroupManager.getCompatibleResourceGroups(user, getTcRuntime70ResourcePrototype()))
            
            renderXml() {
                out << GroupsResponse() {
                    out << getSuccessXML()
                    for (g in  tcRuntimeGroups.sort {a, b ->
                        a.name <=> b.name
                    }) {
                        out << getGroupXML(g)
                    }
                }
            }
        }catch(Exception e) {
            failureResponse(e.message)
        }
    }
    
    private def getTcRuntime60ResourcePrototype() {
        return getServerResourcePrototype("SpringSource tc Runtime 6.0")
    }
    
    private def getTcRuntime70ResourcePrototype() {
        return getServerResourcePrototype("SpringSource tc Runtime 7.0")
    }
    
    private def getServerResourcePrototype(String serverTypeName) {
        def serverType = Bootstrap.getBean(ServerManager).findServerTypeByName(serverTypeName)        
        return Bootstrap.getBean(ResourceManager).findResourcePrototype(new AppdefEntityTypeID(AppdefEntityConstants.APPDEF_TYPE_SERVER, serverType.id))
    }
    
    def start(params) {
        doControlAction("start")
    }
    
    def restart(params) {
        doControlAction("restart")
    }
    
    def stop(params) {
        doControlAction("stop")
    }
    
    private successResponse() {
        renderXml() {
            out << GroupsResponse() { out << getSuccessXML() }
        }
    }
    
    private failureResponse(reason=null) {
        renderXml() {
            out << GroupsResponse() { out << getFailureXML(reason) }
        }
    }
    
    private Closure getGroupXML(g) { { doc ->
            Group(id          : g.id,
                    name        : g.name,
                    description : g.description,
                    location    : g.location) {
                        for (r in g.resources) {
                            Resource(id : r.id,
                                    name : r.name)
                        }
                        for (r in g.roles) {
                            Role(id : r.id,
                                    name : r.name)
                        }
                    }
        }
    }
    
    private Closure getResourceNameXML(resourceName){ { doc -> ResourceName(resourceName) }
    }
    
    private doControlAction(String action) {
        try {
            def orderArray = []
            group.resources.each{resource ->
                orderArray.add(resource.entityId.ID)
            }
            
            Future<ControlActionResult> controlActionFuture = Bootstrap.getBean(ControlManager).doGroupAction(user, group.resource.entityId, action, null, (int[])orderArray, CONTROL_TIMEOUT)
            
            String status = controlActionFuture.get().getStatus();
            if (status == ControlConstants.STATUS_COMPLETED) {
                renderXml() {
                    ControlStatusResponse() { out << getSuccessXML() }
                }
            } else {
                renderXml() {
                    ControlStatusResponse() { out << getFailureXML("Control action failed: $status") }
                }
            }
        } catch (Exception e){
            renderXml() {
                ControlStatusResponse() {
                    out << getFailureXML(e.message )
                }
            }
        }
    }	
    
    private getGroupIdentifier() {
        def groupIdentifier
        def parameterMap = invokeArgs.request.parameterMap
        if (parameterMap.groupid) {
            groupIdentifier = parameterMap.groupid[0].toInteger()
        } else if (parameterMap.groupname){
            groupIdentifier = parameterMap.groupname[0]
        }
        return groupIdentifier
    }
    
    private getServerIdentifier() {
        def serverIdentifier
        def parameterMap = invokeArgs.request.parameterMap
        if (parameterMap.serverid) {
            serverIdentifier = parameterMap.serverid[0].toInteger()
        } else if (parameterMap.servername){
            serverIdentifier = parameterMap.servername[0]
        }
        return serverIdentifier
    }
}
