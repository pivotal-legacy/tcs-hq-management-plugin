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

import org.hyperic.hq.appdef.server.session.ServerType 
import org.hyperic.hq.appdef.shared.AppdefEntityConstants 
import org.hyperic.hq.appdef.shared.AppdefEntityTypeID 
import org.hyperic.hq.appdef.shared.ServerManager 
import org.hyperic.hq.authz.server.session.Resource 
import org.hyperic.hq.authz.server.session.ResourceGroup 
import org.hyperic.hq.authz.shared.PermissionManager;
import org.hyperic.hq.authz.shared.PermissionManagerFactory;
import org.hyperic.hq.events.shared.MaintenanceEventManager;
import org.hyperic.hq.hqu.rendit.metaclass.ResourceGroupCategory 
import org.junit.Before;
import org.junit.Test

class GroupmanagementControllerListTest extends AbstractGroupmanagementControllerTestBase {
    
    private static final int SERVER_TYPE_7_0_ID = 1;
    
    private static final int SERVER_TYPE_6_0_ID = 2;
    
    private final ServerType serverType70 = mock(ServerType)
    
    private final ServerType serverType60 = mock(ServerType)
    
    private final Resource resourcePrototype70 = mock(Resource)
    
    private final Resource resourcePrototype60 = mock(Resource)
    
    @Before
    void defaultBehaviour() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(serverType60)
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(serverType70)
        
        when(serverType70.id).thenReturn(SERVER_TYPE_7_0_ID)
        when(serverType60.id).thenReturn(SERVER_TYPE_6_0_ID)
        
        when(mockResourceManager.findResourcePrototype(new AppdefEntityTypeID(AppdefEntityConstants.APPDEF_TYPE_SERVER, SERVER_TYPE_7_0_ID))).thenReturn(resourcePrototype70)
        when(mockResourceManager.findResourcePrototype(new AppdefEntityTypeID(AppdefEntityConstants.APPDEF_TYPE_SERVER, SERVER_TYPE_6_0_ID))).thenReturn(resourcePrototype60)
        
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
    }

    @Test
    void listNoServers() {                                        
        controller.list([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void listEmptyList() {
        when(mockResourceGroupManager.getCompatibleResourceGroups(mockUser, resourcePrototype70)).thenReturn([])
        when(mockResourceGroupManager.getCompatibleResourceGroups(mockUser, resourcePrototype60)).thenReturn([])

        controller.list([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status></GroupsResponse>", results.toString())
    }

    @Test
    void listSomeServers() {
        ResourceGroup group1 = mock(ResourceGroup)
        when(group1.name).thenReturn("Alpha")
        
        ResourceGroup group2 = mock(ResourceGroup)
        when(group2.name).thenReturn("Bravo")
        
        ResourceGroup group3 = mock(ResourceGroup)
        when(group3.name).thenReturn("Charlie")
        
        when(mockResourceGroupManager.getCompatibleResourceGroups(mockUser, resourcePrototype70)).thenReturn([group1, group3])
        when(mockResourceGroupManager.getCompatibleResourceGroups(mockUser, resourcePrototype60)).thenReturn([group2])

        use (ResourceGroupCategory) {
            controller.list([:])
        }
        
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<GroupsResponse><Status>Success</Status><Group id='0' name='Alpha' description='' location=''></Group><Group id='0' name='Bravo' description='' location=''></Group><Group id='0' name='Charlie' description='' location=''></Group></GroupsResponse>", results.toString())
    }
}
