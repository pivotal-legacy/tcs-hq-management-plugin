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

import java.io.Writer

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.hyperic.hq.appdef.server.session.ServerType
import org.hyperic.hq.appdef.shared.PlatformManager
import org.hyperic.hq.appdef.shared.ServerManager
import org.hyperic.hq.authz.server.session.AuthzSubject
import org.hyperic.hq.authz.server.session.Resource
import org.hyperic.hq.authz.server.session.ResourceGroup
import org.hyperic.hq.authz.shared.AuthzSubjectManager
import org.hyperic.hq.authz.shared.ResourceGroupManager
import org.hyperic.hq.authz.shared.ResourceManager
import org.hyperic.hq.hqu.rendit.RequestInvocationBindings
import org.junit.Test

class GroupmanagementControllerCreateGroupTest extends AbstractGroupmanagementControllerTestBase {

    @Test
    void createWithNoGroupName() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>A group name must be specified</ReasonText></Error></GroupsResponse>",
            results.toString())
    }

    @Test
    void createAndFailWhenCreatingResourceGroup() {
        when(mockRequest.getParameterMap()).thenReturn(['name':['test-group']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockResourceGroupManager.createResourceGroup(any(), any(), any(), any())).thenThrow(new RuntimeException("This should fail"))
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
         assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>This should fail</ReasonText></Error></GroupsResponse>",
            results.toString())
   }

    @Test
    void createAndFailWhenCreatingResourceGroupWithTomcat7() {
        when(mockRequest.getParameterMap()).thenReturn([name:['test-group'], version:['7.0']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockResourceGroupManager.createResourceGroup(any(), any(), any(), any())).thenThrow(new RuntimeException("This should fail"))
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
         assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>This should fail</ReasonText></Error></GroupsResponse>",
            results.toString())
   }

    @Test
    void create() {
        when(mockRequest.getParameterMap()).thenReturn(['name':['test-group']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void createWithTomcat7() {
        when(mockRequest.getParameterMap()).thenReturn(['name':['test-group'], 'version':['7.0']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void createWithLocationAndDescription() {
        when(mockRequest.getParameterMap()).thenReturn(['name':['test-group'], 'location':['test-location'], 'description':['this is a test']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void createWithLocationAndDescriptionWithTomcat7() {
        when(mockRequest.getParameterMap()).thenReturn(['name':['test-group'], 'location':['test-location'], 'description':['this is a test'], 'version':['7.0']])
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.create([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

}
