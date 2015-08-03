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

import org.hyperic.hq.product.PluginException;
import org.junit.Test 

class GroupmanagementControllerDeleteTest extends AbstractGroupmanagementControllerTestBase {

    @Test
    void deleteNothing() {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        controller.delete([:])
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></GroupsResponse>", results.toString())
    }
    
    @Test
    void deleteNonExistentGroupName() {
        when(mockRequest.getParameterMap()).thenReturn(['groupname':['test-group']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.delete([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>The group name 'test-group' did not match any group.</ReasonText></Error></GroupsResponse>", 
            results.toString())
    }

    @Test
    void deleteNonExistentGroupId() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1']])
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.delete([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>The group id '1' did not match any group.</ReasonText></Error></GroupsResponse>",
            results.toString())
    }

    @Test
    void deleteActualGroupByName() {
        when(mockRequest.getParameterMap()).thenReturn(['groupname':['test-group']])
        when(mockResourceGroupManager.findResourceGroupByName(mockUser, "test-group")).thenReturn(mockGroup)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.delete([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void deleteActualGroupById() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1']])
        when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.delete([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }
    
    @Test
    void deleteFails() {
        when(mockRequest.getParameterMap()).thenReturn(['groupid':['1']])
        when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
        when(mockResourceGroupManager.removeResourceGroup(mockUser,mockGroup)).thenThrow(new RuntimeException("Unexplained failure"))
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.delete([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Failure</Status><Error><ReasonText>Unexplained failure</ReasonText></Error></GroupsResponse>",
            results.toString())
    }

}
