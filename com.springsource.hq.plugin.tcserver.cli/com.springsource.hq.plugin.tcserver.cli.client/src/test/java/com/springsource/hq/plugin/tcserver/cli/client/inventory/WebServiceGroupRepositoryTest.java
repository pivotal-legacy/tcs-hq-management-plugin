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

package com.springsource.hq.plugin.tcserver.cli.client.inventory;

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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.GroupsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;

/**
 * Unit test of the {@link WebServiceGroupRepository}
 */
public class WebServiceGroupRepositoryTest {

    private final Connection connection = createMock(Connection.class);

    @SuppressWarnings("unchecked")
    private final ResponseHandler<GroupsResponse> responseHandler = createMock(ResponseHandler.class);

    private final WebServiceGroupRepository groupRepository = new WebServiceGroupRepository(connection, responseHandler);

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    /**
     * Verifies successful addition of a server to a group
     * 
     * @throws IOException
     */
    @Test
    public void testAddServerToGroupByGroupName() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setName("Group1");
        final Resource server = new Resource();
        server.setName("server1");
        server.setId(1234);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("serverid", new String[] { "1234" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/addServer.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.addServerToGroup(expectedGroup, server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful addition of a server to a group
     * 
     * @throws IOException
     */
    @Test
    public void testAddServerToGroupByGroupId() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setId(123);
        final Resource server = new Resource();
        server.setName("server1");
        server.setId(1234);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("groupid", new String[] { "123" });
        expectedParams.put("serverid", new String[] { "1234" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/addServer.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.addServerToGroup(expectedGroup, server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful creation of a group
     * 
     * @throws IOException
     */
    @Test
    public void testCreateGroup() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setDescription("Some description");
        expectedGroup.setLocation("Melbourne");
        expectedGroup.setName("Group1");
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("description", new String[] { "Some description" });
        expectedParams.put("name", new String[] { "Group1" });
        expectedParams.put("location", new String[] { "Melbourne" });
        expectedParams.put("version", new String[] { "7.0" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/create.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.createGroup(expectedGroup, "7.0");
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful deletion of a group
     * 
     * @throws IOException
     */
    @Test
    public void testDeleteGroup() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setName("Group1");
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupname", new String[] { "Group1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/delete.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.deleteGroup(expectedGroup);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Tests successful retrieval of groups
     * 
     * @throws IOException
     */
    @Test
    public void testGetGroups() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setDescription("Some description");
        expectedGroup.setId(1234);
        expectedGroup.setLocation("Melbourne");
        expectedGroup.setName("Group1");
        expected.getGroup().add(expectedGroup);
        EasyMock.expect(connection.doGet("/hqu/tcserverclient/groupmanagement/list.hqu", new HashMap<String, String[]>(0, 1), this.responseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.getGroups();
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful removal of a server from a group
     * 
     * @throws IOException
     */
    @Test
    public void testRemoveServeFromGroupByGroupName() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setName("Group1");
        final Resource server = new Resource();
        server.setName("server1");
        server.setId(1234);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("serverid", new String[] { "1234" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/removeServer.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.removeServerFromGroup(expectedGroup, server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful removal of a server from a group
     * 
     * @throws IOException
     */
    @Test
    public void testRemoveServeFromGroupByGroupId() throws IOException {
        final GroupsResponse expected = new GroupsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Group expectedGroup = new Group();
        expectedGroup.setId(123);
        final Resource server = new Resource();
        server.setName("server1");
        server.setId(1234);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("groupid", new String[] { "123" });
        expectedParams.put("serverid", new String[] { "1234" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/groupmanagement/removeServer.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        GroupsResponse response = groupRepository.removeServerFromGroup(expectedGroup, server);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

}
