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

import static org.junit.Assert.*
import static org.mockito.Mockito.*

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application 
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.ApplicationStatus
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Host
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Service 
import java.io.Writer

import javax.servlet.ServletContext 
import javax.servlet.ServletInputStream 
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.fileupload.FileItem 
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.servlet.ServletRequestContext 
import org.hyperic.hq.agent.FileDataResult 
import org.hyperic.hq.agent.client.AgentCommandsClient
import org.hyperic.hq.agent.client.AgentCommandsClientFactory
import org.hyperic.hq.appdef.Agent 
import org.hyperic.hq.appdef.server.session.AppdefResourceType
import org.hyperic.hq.appdef.server.session.Server 
import org.hyperic.hq.appdef.server.session.ServerType
import org.hyperic.hq.appdef.shared.AgentManager 
import org.hyperic.hq.appdef.shared.PlatformManager
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.appdef.shared.ServiceManager 
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.authz.server.session.ResourceGroup
import org.hyperic.hq.authz.server.session.ResourceType 
import org.hyperic.hq.authz.shared.AuthzConstants 
import org.hyperic.hq.authz.shared.AuthzSubjectManager
import org.hyperic.hq.authz.shared.PermissionManager 
import org.hyperic.hq.authz.shared.PermissionManagerFactory 
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.autoinventory.shared.AutoinventoryManager
import org.hyperic.hq.context.Bootstrap 
import org.hyperic.hq.control.shared.ControlManager
import org.hyperic.hq.control.shared.ControlScheduleManager 
import org.hyperic.hq.events.shared.AlertDefinitionManager 
import org.hyperic.hq.events.shared.AlertManager 
import org.hyperic.hq.events.shared.EventLogManager 
import org.hyperic.hq.events.shared.MaintenanceEventManager 
import org.hyperic.hq.hqu.rendit.RequestInvocationBindings
import org.hyperic.hq.hqu.rendit.metaclass.AppdefCategory
import org.hyperic.hq.hqu.rendit.metaclass.ResourceCategory 
import org.hyperic.hq.hqu.rendit.metaclass.ResourceGroupCategory 
import org.hyperic.hq.livedata.shared.LiveDataManager 
import org.hyperic.hq.livedata.shared.LiveDataResult 
import org.hyperic.hq.measurement.shared.MeasurementManager 
import org.hyperic.util.config.ConfigResponse 
import org.hyperic.lather.LatherValue
import org.junit.Before
import org.junit.Test

class ApplicationmanagementControllerTest {

    ResourceManager mockResourceManager
    ResourceGroupManager mockResourceGroupManager
    PlatformManager mockPlatformManager
    AuthzSubjectManager mockAuthzSubjectManager
    ServerManager mockServerManager
    ControlManager mockControlManager
    LiveDataManager mockLiveDataManager
    AgentManager mockAgentManager
    AgentCommandsClientFactory mockAgentCommandsClientFactory
    AutoinventoryManager mockAutoInventoryManager
    
    AuthzSubject mockUser
    ResourceGroup mockGroup
    Resource mockResource
    ServerType mockServerType
    
    HttpServletRequest mockRequest
    HttpServletResponse mockResponse
    
    RequestInvocationBindings bindings
    ApplicationmanagementController controller
    Writer results
    
    @Before
    void setUp() {
        mockResourceManager = mock(ResourceManager)
        mockResourceGroupManager = mock(ResourceGroupManager)
        mockPlatformManager = mock(PlatformManager)
        mockAuthzSubjectManager = mock(AuthzSubjectManager)
        mockServerManager = mock(ServerManager)
        mockControlManager = mock(ControlManager)
        mockLiveDataManager = mock(LiveDataManager)
        mockAgentManager = mock(AgentManager)
        mockAgentCommandsClientFactory = mock(AgentCommandsClientFactory)
        mockAutoInventoryManager = mock(AutoinventoryManager)
        
        Bootstrap.setBean(ResourceManager, mockResourceManager)
        Bootstrap.setBean(ResourceGroupManager, mockResourceGroupManager)
        Bootstrap.setBean(PlatformManager, mockPlatformManager)
        Bootstrap.setBean(AuthzSubjectManager, mockAuthzSubjectManager)
        Bootstrap.setBean(ServerManager, mockServerManager)
        Bootstrap.setBean(ControlManager, mockControlManager)
        Bootstrap.setBean(LiveDataManager, mock(LiveDataManager))
        Bootstrap.setBean(ServiceManager, mock(ServiceManager))
        Bootstrap.setBean(MeasurementManager, mock(MeasurementManager))
        Bootstrap.setBean(AlertDefinitionManager, mock(AlertDefinitionManager))
        Bootstrap.setBean(AlertManager, mock(AlertManager))
        Bootstrap.setBean(EventLogManager, mock(EventLogManager))
        Bootstrap.setBean(ControlScheduleManager, mock(ControlScheduleManager))
        Bootstrap.setBean(LiveDataManager, mockLiveDataManager)
        Bootstrap.setBean(AgentManager, mockAgentManager)
        Bootstrap.setBean(AgentCommandsClientFactory, mockAgentCommandsClientFactory)
        Bootstrap.setBean(AutoinventoryManager, mockAutoInventoryManager)
        
        PermissionManager mockPermissionManager = mock(PermissionManager)
        MaintenanceEventManager mockMaintEventManager = mock(MaintenanceEventManager)
        
        PermissionManagerFactory.setInstance(mockPermissionManager)
        when(mockPermissionManager.getMaintenanceEventManager()).thenReturn(mockMaintEventManager)

        mockUser = mock(AuthzSubject)
        mockGroup = mock(ResourceGroup)
        mockResource = mock(Resource)
        mockServerType = mock(ServerType)
        
        mockRequest = mock(HttpServletRequest)
        mockResponse = mock(HttpServletResponse)
        ServletContext mockContext = mock(ServletContext)
        bindings = new RequestInvocationBindings("requestUri", "ctxPath", "pathInfo",
            "servletPath", "queryStr", mockUser, mockRequest, mockResponse, mockContext)

        results = new StringWriter()
        
        controller = new ApplicationmanagementController()
        controller.invokeArgs = bindings
    }
    
    @Test
    void listApplicationsWithNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.listApplications([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void listApplicationsWithNonExistentServerId() {
        when(mockRequest.getParameterMap()).thenReturn(['serverid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.listApplications([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '1' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
            results.toString())
    }

    @Test
    void listApplicationsWithNonExistentServerName() {
        when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.listApplications([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
            results.toString())
    }

    @Test
    void listApplicationsWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            AppdefResourceType mockAppdefResourceType = mock(AppdefResourceType)
            when(mockAppdefResourceType.name).thenReturn("SpringSource tc Runtime 6.0")
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            when(mockServer.appdefResourceType).thenReturn(mockAppdefResourceType)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['1'],
                'service':['test-service'],
                'host':['test-host'],
                'version':['1'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(1);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServer.appdefResourceType).thenReturn(mockAppdefResourceType)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><Result><Service name='test-service'><Host name='test-host'><Application name='test-app' status='' sessions='0' version='0'/><Application name='another-test-app' status='' sessions='0' version='0'/></Host></Service></Result><StatusResponse><ResourceName>null (1)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            AppdefResourceType mockAppdefResourceType = mock(AppdefResourceType)
            when(mockAppdefResourceType.name).thenReturn("SpringSource tc Runtime 7.0")
            
            Server mockServer = mock(Server)
            when(mockResource.toServer()).thenReturn(mockServer)
            
            when(mockServer.appdefResourceType).thenReturn(mockAppdefResourceType)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['1'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(1);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><Result><Service name='test-service'><Host name='test-host'><Application name='test-app' status='' sessions='0' version='0'/><Application name='another-test-app' status='' sessions='0' version='0'/></Host></Service></Result><StatusResponse><ResourceName>null (1)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithNonExistentGroupId() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.listApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The group id '1' did not match any group.</ReasonText></Error></ApplicationManagementResponse>",
            results.toString())
    }

    //@Test
    void listApplicationsWithGroupId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['groupid':['1'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResourceManager.findResourceById(1)).thenReturn(mockResource)
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><Result><Service name='test-service'><Host name='test-host'><Application name='test-app' status='' sessions='0'/><Application name='another-test-app' status='' sessions='0'/></Host></Service></Result><StatusResponse><ResourceName>null (0)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerIdThatDoesntMatch() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['2'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResourceManager.findResourceById(1)).thenReturn(mockResource)
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '2' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerName() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            AppdefResourceType mockAppdefResourceType = mock(AppdefResourceType)
            when(mockAppdefResourceType.name).thenReturn("SpringSource tc Runtime 7.0")
            
            when(mockServer.appdefResourceType).thenReturn(mockAppdefResourceType)
            
            when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            Resource mockPrototype = mock(Resource)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(mockPrototype)
            when(mockResourceManager.findResourcesOfPrototype(any(), any())).thenReturn([mockResource])
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><Result><Service name='test-service'><Host name='test-host'><Application name='test-app' status='' sessions='0' version='0'/><Application name='another-test-app' status='' sessions='0' version='0'/></Host></Service></Result><StatusResponse><ResourceName>test-server (0)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerNameWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            AppdefResourceType mockAppdefResourceType = mock(AppdefResourceType)
            when(mockAppdefResourceType.name).thenReturn("SpringSource tc Runtime 7.0")
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            Resource mockPrototype = mock(Resource)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(mockPrototype)
            when(mockResourceManager.findResourcesOfPrototype(any(), any())).thenReturn([mockResource])
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            when(mockServer.appdefResourceType).thenReturn(mockAppdefResourceType)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><Result><Service name='test-service'><Host name='test-host'><Application name='test-app' status='' sessions='0' version='0'/><Application name='another-test-app' status='' sessions='0' version='0'/></Host></Service></Result><StatusResponse><ResourceName>test-server (0)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerNameThatDoesntMatch() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['servername':['test-other-server'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            Resource mockPrototype = mock(Resource)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(mockPrototype)
            when(mockResourceManager.findResourcesOfPrototype(any(), any())).thenReturn([mockResource])
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-other-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithServerNameThatDoesntMatchWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['servername':['test-other-server'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            Resource mockPrototype = mock(Resource)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(mockPrototype)
            when(mockResourceManager.findResourcesOfPrototype(any(), any())).thenReturn([mockResource])
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-other-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithNoApps() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['1'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(1);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Host host = new Host()
            host.setName("test-host")
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>There are no applications associated with this resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void listApplicationsWithNoAppsWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['1'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(1);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            config.setValue("MULTI_REVISION_CAPABLE", false)
            
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "listApplications", config)).thenReturn(mockResults)
            
            Host host = new Host()
            host.setName("test-host")
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(service)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.listApplications([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>There are no applications associated with this resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationRemotelyWithNoServerIdOrServerName() {
        when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
        ServletInputStream mockInput = mock(ServletInputStream)
        when(mockRequest.inputStream).thenReturn(mockInput)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.deployApplication(['foo':'bar'])

        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
            results.toString())
    }

    @Test
    void deployApplicationWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(42);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);           
            when(mockResource.instanceId).thenReturn(4668)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "/test-context-path")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory) 
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("contextpath")
            when(item2.string).thenReturn("/test-context-path")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("service")
            when(item3.string).thenReturn("test-service")
            
            FileItem item4 = mock(FileItem)
            when(item4.fieldName).thenReturn("host")
            when(item4.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3, item4] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("service", "test-service")
            config2.setValue("host", "test-host")
            config2.setValue("remotepath", "test-file.war")
            config2.setValue("contextpath", "/test-context-path")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication([:])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationWithServerIdwithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            def prototype = mock(Resource.class)
            when(mockResource.id).thenReturn(42);
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "/test-context-path")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory)
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("contextpath")
            when(item2.string).thenReturn("/test-context-path")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("service")
            when(item3.string).thenReturn("test-service")
            
            FileItem item4 = mock(FileItem)
            when(item4.fieldName).thenReturn("host")
            when(item4.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3, item4] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("service", "test-service")
            config2.setValue("host", "test-host")
            config2.setValue("remotepath", "test-file.war")
            config2.setValue("contextpath", "/test-context-path")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication([:])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationWithServerIdAndNoContextPath() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "test-file")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory)
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("service")
            when(item2.string).thenReturn("test-service")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("host")
            when(item3.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("host", "test-host")
            config2.setValue("remotepath", "test-file.war")
            config2.setValue("service", "test-service")
            config2.setValue("contextpath", "test-file")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication([:])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationWithServerIdAndNoContextPathWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "test-file")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory)
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("service")
            when(item2.string).thenReturn("test-service")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("host")
            when(item3.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("host", "test-host")
            config2.setValue("remotepath", "test-file.war")
            config2.setValue("service", "test-service")
            config2.setValue("contextpath", "test-file")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication([:])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationRemotelyWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "/test-context-path")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory)
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("contextpath")
            when(item2.string).thenReturn("/test-context-path")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("service")
            when(item3.string).thenReturn("test-service")
            
            FileItem item4 = mock(FileItem)
            when(item4.fieldName).thenReturn("host")
            when(item4.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3, item4] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("serverid", "42")
            config2.setValue("host", "test-host")
            config2.setValue("application1", "test-app")
            config2.setValue("service", "test-service")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication(['foo':'bar'])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void deployApplicationRemotelyWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
        
            when(mockRequest.contentType).thenReturn("multipart/mixedboundary=foobar")
            ServletInputStream mockInput = mock(ServletInputStream)
            when(mockRequest.inputStream).thenReturn(mockInput)
            ServletRequestContext ctx = new ServletRequestContext(mockRequest)
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            AppdefResourceType appdefResourceType = mock(AppdefResourceType)
            when(mockServer.appdefResourceType).thenReturn(appdefResourceType)
            when(appdefResourceType.name).thenReturn('SpringSource tc Runtime 6.0')
            
            Agent mockAgent = mock(Agent)
            when(mockAgentManager.getAgent(mockResource.entityId)).thenReturn(mockAgent)
            AgentCommandsClient mockAgentCommandsClient = mock(AgentCommandsClient)
            when(mockAgentCommandsClientFactory.getClient(mockAgent)).thenReturn(mockAgentCommandsClient)
            def config = [] as ConfigResponse
            config.setValue("service", "test-service")
            config.setValue("host", "test-host")
            config.setValue("contextpath", "/test-context-path")
            config.setValue("MULTI_REVISION_CAPABLE", false)
            println "config is now $config"
            LiveDataResult mockResults = mock(LiveDataResult)
            println "$mockResource getLiveData($mockUser, 'getTemporaryWebAppDirectory', $config)"
            when(mockResults.objectResult).thenReturn([new ApplicationStatus()])
            when(mockResource.getLiveData(mockUser, "getTemporaryWebAppDirectory", config)).thenReturn(mockResults)
            ServletFileUploadFactory mockFactory = mock(ServletFileUploadFactory)
            controller.servletFileUploadFactory = mockFactory
            
            FileItem item1 = mock(FileItem)
            when(item1.fieldName).thenReturn("filename")
            when(item1.name).thenReturn("test-file.war")
            byte[] stubData = new byte[10]
            when(item1.get()).thenReturn(stubData)
            
            FileItem item2 = mock(FileItem)
            when(item2.fieldName).thenReturn("contextpath")
            when(item2.string).thenReturn("/test-context-path")
            
            FileItem item3 = mock(FileItem)
            when(item3.fieldName).thenReturn("service")
            when(item3.string).thenReturn("test-service")
            
            FileItem item4 = mock(FileItem)
            when(item4.fieldName).thenReturn("host")
            when(item4.string).thenReturn("test-host")

            List<FileItem> fileItems = [item1, item2, item3, item4] as List<FileItem>
            ServletFileUpload mockFileUploader = mock(ServletFileUpload)
            when(mockFactory.createServletFileUploader()).thenReturn(mockFileUploader)
            when(mockFileUploader.parseRequest(mockRequest)).thenReturn(fileItems)
            
            FileDataResult[] fileDataResults = new FileDataResult[1]
            fileDataResults[0] = new FileDataResult("test-file.war", 0L, 0L)
            when(mockAgentCommandsClient.agentSendFileData(any(), any())).thenReturn(fileDataResults)

            def config2 = [] as ConfigResponse
            config2.setValue("serverid", "42")
            config2.setValue("host", "test-host")
            config2.setValue("application1", "test-app")
            config2.setValue("service", "test-service")
            config2.setValue("MULTI_REVISION_CAPABLE", false)

            when(mockResource.getLiveData(mockUser, "deployApplication", config2)).thenReturn(mockResults)
            when(mockResource.name).thenReturn("test-resource")
            when(mockResource.id).thenReturn(42)
            when(mockResults.hasError()).thenReturn(false)
            
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.deployApplication(['foo':'bar'])
            
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-resource (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void startApplicationsWithNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.startApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void stopApplicationsWithNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.stopApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void reloadApplicationsWithNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.reloadApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void undeployApplicationsWithNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.undeployApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>A valid server id, server name, group id, or group name was not specified.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void startApplicationsWithNonExistentServerId() {
        when(mockRequest.getParameterMap()).thenReturn(['serverid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.startApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '1' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void stopApplicationsWithNonExistentServerId() {
        when(mockRequest.getParameterMap()).thenReturn(['serverid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.stopApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '1' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void reloadApplicationsWithNonExistentServerId() {
        when(mockRequest.getParameterMap()).thenReturn(['serverid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.reloadApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '1' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void undeployApplicationsWithNonExistentServerId() {
        when(mockRequest.getParameterMap()).thenReturn(['serverid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.undeployApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server id '1' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void startApplicationsWithNonExistentServerName() {
        when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.startApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void stopApplicationsWithNonExistentServerName() {
        when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.stopApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void reloadApplicationsWithNonExistentServerName() {
        when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.reloadApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }
    
    @Test
    void undeployApplicationsWithNonExistentServerName() {
        when(mockRequest.getParameterMap()).thenReturn(['servername':['test-server']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.undeployApplications([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Failure</Status><Error><ReasonText>The server name 'test-server' did not match any server resource.</ReasonText></Error></ApplicationManagementResponse>",
                results.toString())
    }

    @Test
    void startApplicationsWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "startApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.startApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>", 
                results.toString())
        }
    }

    @Test
    void startApplicationsWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "startApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.startApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void stopApplicationsWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "stopApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.stopApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>", 
                results.toString())
        }
    }

    @Test
    void stopApplicationsWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "stopApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.stopApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void reloadApplicationsWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "reloadApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.reloadApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>", 
                results.toString())
        }
    }

    @Test
    void reloadApplicationsWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "reloadApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.reloadApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

    @Test
    void undeployApplicationsWithServerId() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "undeployApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.undeployApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>", 
                results.toString())
        }
    }

    @Test
    void undeployApplicationsWithServerIdWithTomcat7() {
        use (ResourceCategory, ResourceGroupCategory, AppdefCategory) {
            
            ResourceType mockGroupType = mock(ResourceType)
            when(mockGroupType.id).thenReturn(AuthzConstants.authzServer)
            
            Resource mockResource = mock(Resource)
            when(mockResource.resourceType).thenReturn(mockGroupType)
            
            Server mockServer = mock(Server)
            
            when(mockRequest.getParameterMap()).thenReturn(['serverid':['42'],
                'service':['test-service'],
                'host':['test-host'],
                'applications':['test-app']])
            when(mockResource.id).thenReturn(42);
            def prototype = mock(Resource.class)
            when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
            when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
            when(mockResource.instanceId).thenReturn(4668)
            when(mockServer.id).thenReturn(4668)
            when(mockServerManager.findServerById(4668)).thenReturn(mockServer)
            when(mockServer.getAuthzOp("view")).thenReturn("view")
            when(mockUser.name).thenReturn("Tester")
            when(mockServer.resource).thenReturn(mockResource)
            
            ConfigResponse config = controller.getConfigResponse()
            LiveDataResult mockResults = mock(LiveDataResult)
            when(mockResource.getLiveData(mockUser, "undeployApplications", config)).thenReturn(mockResults)
            
            when(mockResource.name).thenReturn("test-server")
            when(mockResource.id).thenReturn(42)
            
            Application app1 = new Application()
            app1.setName("test-app")
            Application app2 = new Application()
            app2.setName("another-test-app")
            
            Host host = new Host()
            host.setName("test-host")
            host.getApplications().add(app1)
            host.getApplications().add(app2)
            
            Service service = new Service()
            service.setName("test-service")
            service.getHosts().add(host)
            
            when(mockResults.getObjectResult()).thenReturn(new ApplicationStatus())
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
            
            controller.undeployApplications([:])
            
            println results.toString()
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ApplicationManagementResponse><Status>Success</Status><StatusResponse><ResourceName>test-server (42)</ResourceName><Status>Success</Status></StatusResponse></ApplicationManagementResponse>",
                results.toString())
        }
    }

}
