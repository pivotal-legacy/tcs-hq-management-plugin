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

import java.util.concurrent.Future 
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.authz.shared.PermissionManager 
import org.hyperic.hq.authz.shared.PermissionManagerFactory 
import org.hyperic.hq.control.ControlActionResult 
import org.hyperic.hq.control.shared.ControlConstants;
import org.hyperic.hq.events.shared.MaintenanceEventManager 
import org.hyperic.hq.hqu.rendit.metaclass.ResourceCategory 
import org.hyperic.hq.hqu.rendit.metaclass.ResourceGroupCategory 
import org.junit.Test

class GroupmanagementControllerControlTest extends AbstractGroupmanagementControllerTestBase {
    
    @Test
    void startWithNothing() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.start([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void startWithNothingWithTomcat7() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.start([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void startByGroupId() {        
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "start", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_COMPLETED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.start([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Success</Status></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void startByGroupIdButFailed() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "start", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_FAILED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.start([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: Failed</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void startByGroupIdButStillInProgress() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "start", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_INPROGRESS)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.start([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: In Progress</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void stopWithNothing() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.stop([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void stopWithNothingWithTomcat7() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.stop([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void stopByGroupId() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "stop", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_COMPLETED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.stop([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Success</Status></ControlStatusResponse>",
                results.toString())       
        }
    }
    
    @Test
    void stopByGroupIdButFailed() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "stop", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_FAILED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.stop([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: Failed</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void stopByGroupIdButStillInProgress() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "stop", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_INPROGRESS)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.stop([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: In Progress</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void restartWithNothing() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 6.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.restart([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void restartWithNothingWithTomcat7() {
        when(mockServerManager.findServerTypeByName("SpringSource tc Runtime 7.0")).thenReturn(mockServerType)
        when(mockServerType.id).thenReturn(1)
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(results))
        
        controller.restart([:])
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>A valid group id or name was not specified.</ReasonText></Error></ControlStatusResponse>",
            results.toString())
    }

    @Test
    void restartByGroupId() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "restart", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_COMPLETED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.restart([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Success</Status></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void restartByGroupIdButFailed() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "restart", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_FAILED)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.restart([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: Failed</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }

    @Test
    void restartByGroupIdButStillInProgress() {
        use (ResourceGroupCategory, ResourceCategory) {
            when(mockRequest.parameterMap).thenReturn(['groupid':['1'], 'serverid':['1']])
            when(mockResourceGroupManager.findResourceGroupById(mockUser, 1)).thenReturn(mockGroup)
            int instanceId = 1143
            when(mockResource.instanceId).thenReturn(instanceId)
            when (mockGroup.resources).thenReturn([mockResource])
            when(mockGroup.resource).thenReturn(mockResource)
            Future<ControlActionResult> mockFuture = (Future<ControlActionResult>)mock(Future)
            when(mockControlManager.doGroupAction(mockUser, mockGroup.resource.entityId, "restart", null, (int[])[instanceId], 90000)).thenReturn(mockFuture)
            ControlActionResult mockResult = mock(ControlActionResult)
            when(mockFuture.get()).thenReturn(mockResult)
            when(mockResult.status).thenReturn(ControlConstants.STATUS_INPROGRESS)
            when(mockResponse.writer).thenReturn(new PrintWriter(results))
         
            controller.restart([:])
            assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<ControlStatusResponse><Status>Failure</Status><Error><ReasonText>Control action failed: In Progress</ReasonText></Error></ControlStatusResponse>",
                results.toString())
        }
    }
}
