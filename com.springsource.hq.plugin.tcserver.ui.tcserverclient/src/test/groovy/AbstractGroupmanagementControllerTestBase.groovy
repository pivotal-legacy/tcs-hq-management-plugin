/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

import java.io.Writer

import javax.servlet.ServletContext 
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.hyperic.hq.appdef.server.session.ServerType
import org.hyperic.hq.appdef.shared.PlatformManager
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.appdef.shared.ServiceManager 
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.authz.server.session.ResourceGroup
import org.hyperic.hq.authz.shared.AuthzSubjectManager
import org.hyperic.hq.authz.shared.PermissionManager 
import org.hyperic.hq.authz.shared.PermissionManagerFactory 
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.context.Bootstrap 
import org.hyperic.hq.control.shared.ControlManager
import org.hyperic.hq.control.shared.ControlScheduleManager 
import org.hyperic.hq.events.shared.AlertDefinitionManager 
import org.hyperic.hq.events.shared.AlertManager 
import org.hyperic.hq.events.shared.EventLogManager 
import org.hyperic.hq.events.shared.MaintenanceEventManager 
import org.hyperic.hq.hqu.rendit.RequestInvocationBindings
import org.hyperic.hq.livedata.shared.LiveDataManager 
import org.hyperic.hq.measurement.shared.MeasurementManager 
import org.junit.After;
import org.junit.Before

abstract class AbstractGroupmanagementControllerTestBase {

    ResourceManager mockResourceManager
    ResourceGroupManager mockResourceGroupManager
    PlatformManager mockPlatformManager
    AuthzSubjectManager mockAuthzSubjectManager
    ServerManager mockServerManager
    ControlManager mockControlManager
    
    AuthzSubject mockUser
    ResourceGroup mockGroup
    Resource mockResource
    ServerType mockServerType
    
    HttpServletRequest mockRequest
    HttpServletResponse mockResponse
    
    RequestInvocationBindings bindings
    GroupmanagementController controller
    Writer results

    @Before
    void setUp() {
        mockResourceManager = mock(ResourceManager)
        mockResourceGroupManager = mock(ResourceGroupManager)
        mockPlatformManager = mock(PlatformManager)
        mockAuthzSubjectManager = mock(AuthzSubjectManager)
        mockServerManager = mock(ServerManager)
        mockControlManager = mock(ControlManager)
        
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
        
        controller = new GroupmanagementController()
        controller.invokeArgs = bindings
    }
    
    @After
    void tearDown() {
        mockResourceManager = null
        mockResourceGroupManager = null
        mockPlatformManager = null
        mockAuthzSubjectManager = null
        mockServerManager = null
        mockControlManager = null
        
        mockUser = null
        mockGroup = null
        mockResource = null
        mockServerType = null
        
        mockRequest = null
        mockResponse = null
        
        bindings = null
        controller = null
        results = null
        
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

    }

}
