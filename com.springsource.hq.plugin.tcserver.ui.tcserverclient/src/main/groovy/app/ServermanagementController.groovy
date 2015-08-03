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

import org.hyperic.hq.control.shared.ControlManager
import org.hyperic.hq.control.shared.ControlConstants
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.appdef.shared.PlatformManager
import org.hyperic.util.pager.PageControl
import org.hyperic.hq.bizapp.shared.MeasurementBoss

import org.hyperic.hq.hqu.rendit.helpers.RoleHelper

import org.hyperic.hq.autoinventory.shared.AutoinventoryManager
import org.hyperic.hq.autoinventory.ScanConfigurationCore
import org.hyperic.hq.autoinventory.ServerSignature
import org.hyperic.hq.context.Bootstrap
import org.hyperic.hq.product.PluginException

import com.springsource.hq.plugin.tcserver.util.control.ControlActionHelper;


class ServermanagementController extends BasemanagementController{
   
    private static final String SERVER_TYPE_NAME_6 = "SpringSource tc Runtime 6.0"

    private static final String SERVER_TYPE_NAME_7 = "SpringSource tc Runtime 7.0"
    
    private static final String SERVER_TYPE_NAME_8 = "Pivotal tc Runtime 8.0"
    
    private static final String ACTION_START = "start"
    
    private static final String ACTION_STOP = "stop"
    
    private static final String ACTION_RESTART = "restart"
    
    private final ControlActionHelper controlActionHelper = new ControlActionHelper()
    
    private executeAutoDiscovery(resource, user){
        ScanConfigurationCore scanConfigurationCore = new ScanConfigurationCore()
        ServerSignature serverSignature = new ServerSignature()
        serverSignature.setServerTypeName(SERVER_TYPE_NAME_6)
        ServerSignature serverSignature2 = new ServerSignature()
        serverSignature.setServerTypeName(SERVER_TYPE_NAME_7)
        ServerSignature serverSignature3 = new ServerSignature()
        serverSignature.setServerTypeName(SERVER_TYPE_NAME_8)
        ServerSignature[] signatureArray = new ServerSignature[3]
        signatureArray[0] = serverSignature
        signatureArray[1] = serverSignature2
        signatureArray[2] = serverSignature3
        scanConfigurationCore.setServerSignatures(signatureArray)
        Bootstrap.getBean(AutoinventoryManager).startScan(user, resource.getPlatform().entityId, scanConfigurationCore, null, null, null)
    }
    
    def listServers(params){
        def parameterMap = invokeArgs.request.parameterMap
        try {
            def permittedResources = []
            
            if (parameterMap.groupname) {
                def group = getGroup(parameterMap)
                if (group == null) {
                    throw new PluginException("There is no group with the name ${parameterMap.groupname[0]}")
                }
                for(resource in group.resources) {
                    // Does the user have permissions for the resource? 
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    permittedResources << resource 
                }
                resourceResponse(permittedResources)
            } else if (parameterMap.platformname) {
                def resources = []
                def platformId = Bootstrap.getBean(PlatformManager).getPlatformByName(user,parameterMap.platformname[0]).id
                def servers = Bootstrap.getBean(ServerManager).getServersByPlatform(user,platformId, 
                        Bootstrap.getBean(ServerManager).findServerTypeByName(SERVER_TYPE_NAME_6).getId(),false, PageControl.PAGE_ALL)
                
                for (server in servers) {
                    resources.add(Bootstrap.getBean(ResourceManager).findResource(server.entityId))
                }
                servers = Bootstrap.getBean(ServerManager).getServersByPlatform(user,platformId,
                    Bootstrap.getBean(ServerManager).findServerTypeByName(SERVER_TYPE_NAME_7).getId(),false, PageControl.PAGE_ALL)
            
                for (server in servers) {
                    resources.add(Bootstrap.getBean(ResourceManager).findResource(server.entityId))
                }
                servers = Bootstrap.getBean(ServerManager).getServersByPlatform(user,platformId,
                    Bootstrap.getBean(ServerManager).findServerTypeByName(SERVER_TYPE_NAME_8).getId(),false, PageControl.PAGE_ALL)
            
                for (server in servers) {
                    resources.add(Bootstrap.getBean(ResourceManager).findResource(server.entityId))
                }

                for (resource in resources) {
                    // Does the user have permissions for the resource? 
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    permittedResources << resource
                }
                resourceResponse(permittedResources)
            } else {
                def resources = resourceHelper.findByPrototype([byPrototype:SERVER_TYPE_NAME_6])
                
                for(resource in resources) {
                    // Does the user have permissions for the resource? 
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    permittedResources << resource
                }
                
                resources = resourceHelper.findByPrototype([byPrototype:SERVER_TYPE_NAME_7])
                
                for(resource in resources) {
                    // Does the user have permissions for the resource? 
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    permittedResources << resource
                }
                
                resources = resourceHelper.findByPrototype([byPrototype:SERVER_TYPE_NAME_8])
                
                for(resource in resources) {
                    // Does the user have permissions for the resource?
                    def convertedResource = convertToAppdefResource(resource)
                    convertedResource.checkPerms(operation:'view', user:user)
                    permittedResources << resource
                }
                
                resourceResponse(permittedResources)
            }
        } catch (PermissionException pe) {
            println "User $user?.name does not have the permissions to list server"
            failureResponse("User $user?.name does not have the permissions to list server")
        }catch(Exception e) {
            failureResponse(e.getMessage())
        }
    }
    
    def start(params) {
        doControlAction(ACTION_START)
        executeAutoDiscovery(server, user)
    }
    
    def stop(params) {
        doControlAction(ACTION_STOP)
    }
    
    def restart(params) {
        doControlAction(ACTION_RESTART)
        executeAutoDiscovery(server, user)
    }
    
    def modify(params) {
        def parameterMap = invokeArgs.request.parameterMap
        if (parameterMap.serverid) {
            try {
                def serverResource = Bootstrap.getBean(ServerManager).getServerById(
                        getServerById(parameterMap.serverid[0].toInteger()).entityId.id)
                
                // Does the user have permissions for the resource? 
                def convertedResource = convertToAppdefResource(server)
                convertedResource.checkPerms(operation:'modify', user:user)
                
                if(parameterMap.name) {
                    serverResource.name = parameterMap.name[0]
                }
                if(parameterMap.description) {
                    serverResource.description = parameterMap.description[0]
                }
                Bootstrap.getBean(ServerManager).updateServer(user, serverResource.serverValue)
                renderXml() {
                    ResourcesResponse() { out << getSuccessXML() }
                }
            } catch (PermissionException pe) {
                failureResponse("User $user?.name does not have the permissions to modify server")
            }catch(Exception e) {
                failureResponse(e.getMessage())
            }
        }else {
            failureResponse("A server id was not specified")
        }
    }
    
    private doControlAction(String action) {
        try {            
            log.info("Executing action: $action")
            
            if (ACTION_START == action) {
                this.controlActionHelper.startServer(user, server)
            } else if (ACTION_STOP == action) {
                this.controlActionHelper.stopServer(user, server)
            } else {
                this.controlActionHelper.restartServer(user, server, false)
            }
            
            renderXml() {
                ControlStatusResponse() {
                    out << getSuccessXML()
                }
            }
        } catch (PermissionException pe) {
            renderXml() {
                ControlStatusResponse() {
                    out << getFailureXML("User $user.name does not have the permissions to control server")
                }
            }
        }catch (Exception e){
            renderXml() {
                ControlStatusResponse() {
                    out << getFailureXML(e.getMessage() )
                }
            }
        }
    }
    
    private getServerStatus(user,entityId) {
        try {
            double availability = Bootstrap.getBean(MeasurementBoss).getAvailability(user,entityId)
            if(availability == 0d) {
                return "Stopped"
            }else if(availability == 1d) {
                return "Running"
            } else {
                return "Unknown"
            }
        }catch(Exception e) {
            return "Unknown"
        }
    }
    
    private Closure getResourceXML(user, r, boolean verbose, boolean children) { { doc ->
            Resource(id : r.id,
                    name : r.name,
                    description : r.description,
                    status: getServerStatus(user,r.entityId)) {
                        if (verbose) {
                            def config = r.getConfig()
                            config.each { k, v ->
                                if (v.type == "configResponse") {
                                    ResourceConfig(key: k, value: v.value)
                                }
                            }
                            config.each { k, v ->
                                if (v.type == "cprop") {
                                    ResourceProperty(key: k, value: v.value)
                                }
                            }
                        }
                        if (children) {
                            r.getViewableChildren(user).each { child ->
                                out << getResourceXML(user, child, verbose, children)
                            }
                        }
                    }
        }
    }
    
    private Closure getResourceNameXML(resourceName){ { doc -> ResourceName(resourceName) }
    }
    
    private resourceResponse(resources) {
        renderXml() {
            ResourcesResponse() {
                out << getSuccessXML()
                // Only return this resource w/ it's config
                for (resource in resources){
                    out << getResourceXML(user, resource, false, false)
                }
            }
        }
    }
    
    private failureResponse(reason=null) {
        renderXml() {
            out << ResourcesResponse() { out << getFailureXML(reason) }
        }
    }
}
