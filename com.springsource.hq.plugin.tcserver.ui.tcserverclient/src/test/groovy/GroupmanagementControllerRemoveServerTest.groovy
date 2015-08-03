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

class GroupmanagementControllerRemoveServerTest extends AbstractGroupmanagementControllerTestBase {

    @Test
    void removeServer() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1'], 'serverid':['1']])
        when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
        def prototype = mock(Resource.class) 
        when(mockResource.id).thenReturn(1);
        when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 6.0')).thenReturn(prototype)
        when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.removeServer([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
        verify(mockResourceGroupManager).removeResources(mockUser, mockGroup, [mockResource])
    }

    @Test
    void removeServerWithTomcat7() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1'], 'serverid':['1']])
        when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
        def prototype = mock(Resource.class)
        when(mockResource.id).thenReturn(1);
        when(mockResourceManager.findResourcePrototypeByName('SpringSource tc Runtime 7.0')).thenReturn(prototype)
        when(mockResourceManager.findResourcesOfPrototype(eq(prototype), any())).thenReturn([mockResource]);
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.removeServer([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
        verify(mockResourceGroupManager).removeResources(mockUser, mockGroup, [mockResource])
    }

    @Test
    void removeServerFromNonExistentGroup() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1'], 'serverid':['1']])
        when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenThrow(new RuntimeException("Invalid server"))
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.removeServer([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>The group id '1' did not match any group.</ReasonText></Error></GroupsResponse>", results.toString())
    }


}
