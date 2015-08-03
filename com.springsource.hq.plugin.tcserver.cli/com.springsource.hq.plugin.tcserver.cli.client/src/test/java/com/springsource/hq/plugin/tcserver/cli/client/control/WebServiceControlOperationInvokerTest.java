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

package com.springsource.hq.plugin.tcserver.cli.client.control;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.ParamsEquals;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ControlStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;

/**
 * Unit test of the {@link WebServiceControlOperationInvoker}
 */
public class WebServiceControlOperationInvokerTest {

    private final Connection connection = createMock(Connection.class);

    @SuppressWarnings("unchecked")
    private final ResponseHandler<ControlStatusResponse> responseHandler = createMock(ResponseHandler.class);

    private final ControlOperationInvoker controlOperationInvoker = new WebServiceControlOperationInvoker(connection, responseHandler);

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    private ControlStatusResponse getExpectedReponse() {
        final ControlStatusResponse expected = new ControlStatusResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        return expected;
    }

    /**
     * Verifies successful restart of a tc Server
     * 
     * @throws IOException
     */
    @Test
    public void testRestartServer() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/servermanagement/restart.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.restart(server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful start of a tc Server
     * 
     * @throws IOException
     */
    @Test
    public void testStartServer() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/servermanagement/start.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.start(server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful stop of a tc Server
     * 
     * @throws IOException
     */
    @Test
    public void testStopServer() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/servermanagement/stop.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.stop(server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful restart of a group of tc Servers
     * 
     * @throws IOException
     */
    @Test
    public void testRestartGroup() throws IOException {
        final Group group = new Group();
        group.setId(1234);
        group.setName("Group1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "1234" });
        expectedParams.put("groupname", new String[] { "Group1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/restart.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.restart(group);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful start of a group of tc Servers
     * 
     * @throws IOException
     */
    @Test
    public void testStartGroup() throws IOException {
        final Group group = new Group();
        group.setId(1234);
        group.setName("Group1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "1234" });
        expectedParams.put("groupname", new String[] { "Group1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/start.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.start(group);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful stop of a group of tc Servers
     * 
     * @throws IOException
     */
    @Test
    public void testStopGroup() throws IOException {
        final Group group = new Group();
        group.setId(1234);
        group.setName("Group1");
        final ControlStatusResponse expected = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "1234" });
        expectedParams.put("groupname", new String[] { "Group1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/stop.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        ControlStatusResponse response = controlOperationInvoker.stop(group);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }
}
