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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;

/**
 * Unit test of {@link WebServiceResourceRepository}
 */
public class WebServiceResourceRepositoryTest {

    private final Connection connection = createMock(Connection.class);

    @SuppressWarnings("unchecked")
    private final ResponseHandler<ResourcesResponse> responseHandler = createMock(ResponseHandler.class);

    private final ResourceRepository resourceRepository = new WebServiceResourceRepository(connection, responseHandler);

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    /**
     * Verifies successful execution of method to retrieve tc Servers
     * 
     * @throws IOException
     */
    @Test
    public void testGetServers() throws IOException {
        final ResourcesResponse expected = new ResourcesResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        Resource resource = new Resource();
        resource.setDescription("A Description");
        resource.setName("A name");
        resource.setId(12345);
        expected.getResource().add(resource);
        EasyMock.expect(
            connection.doGet("/hqu/tcserverclient/servermanagement/listServers.hqu", new HashMap<String, String[]>(0, 1), this.responseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        final ResourcesResponse response = resourceRepository.getServers();
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful execution of method to retrieve tc Servers by group
     * 
     * @throws IOException
     */
    @Test
    public void testGetServersByGroup() throws IOException {
        final ResourcesResponse expected = new ResourcesResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        Resource resource = new Resource();
        resource.setDescription("A Description");
        resource.setName("A name");
        resource.setId(12345);
        expected.getResource().add(resource);
        final Group group = new Group();
        group.setName("Group1");
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupname", new String[] { "Group1" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/servermanagement/listServers.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        final ResourcesResponse response = resourceRepository.getServersByGroup(group);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful execution of method to retrieve tc Servers by platform
     * 
     * @throws IOException
     */
    @Test
    public void testGetServersByPlatform() throws IOException {
        final ResourcesResponse expected = new ResourcesResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        Resource resource = new Resource();
        resource.setDescription("A Description");
        resource.setName("A name");
        resource.setId(12345);
        expected.getResource().add(resource);
        final Resource platform = new Resource();
        platform.setName("mymachine");
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("platformname", new String[] { "mymachine" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/servermanagement/listServers.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        final ResourcesResponse response = resourceRepository.getServersByPlatform(platform);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful modify of server name and description
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServer() throws IOException {
        final ResourcesResponse expected = new ResourcesResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        Resource resource = new Resource();
        resource.setDescription("A Description");
        resource.setName("A name");
        resource.setId(12345);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "12345" });
        expectedParams.put("name", new String[] { "A name" });
        expectedParams.put("description", new String[] { "A Description" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/servermanagement/modify.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        final ResourcesResponse response = resourceRepository.modifyServer(resource);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }
}
