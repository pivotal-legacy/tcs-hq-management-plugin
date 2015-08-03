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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.hyperic.hq.hqapi1.XmlResponseHandler;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.GroupsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * An implementation of {@link GroupRepository} that communicates with the server via web services calls
 */
public class WebServiceGroupRepository implements GroupRepository {

    private static final String BASE_URI = "/hqu/tcserverclient/groupmanagement";

    private static final String DESCRIPTION = "description";

    private static final Map<String, String[]> EMPTY_PARAMS = new HashMap<String, String[]>(0, 1);

    private static final String GROUP_NAME = "groupname";

    private static final String GROUP_ID = "groupid";

    private static final String LOCATION = "location";

    private static final String VERSION = "version";

    private static final String NAME = "name";

    private static final String SERVER_ID = "serverid";

    private static final String SERVER_NAME = "servername";

    private final ResponseHandler<GroupsResponse> responseHandler;

    private Connection connection;

    /**
     * 
     * @param connection The {@link Connection} to the server
     */
    public WebServiceGroupRepository(Connection connection) {
        this(connection, new XmlResponseHandler<GroupsResponse>(GroupsResponse.class));
    }

    WebServiceGroupRepository(Connection connection, ResponseHandler<GroupsResponse> responseHandler) {
        this.connection = connection;
        this.responseHandler = responseHandler;
    }

    public GroupsResponse addServerToGroup(Group group, Resource server) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null && group.getId() != 0) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        if (server.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(server.getId()) });
        }
        if (server.getName() != null) {
            params.put(SERVER_NAME, new String[] { server.getName() });
        }
        return connection.doPost(BASE_URI + "/addServer.hqu", params, this.responseHandler);
    }

    public GroupsResponse createGroup(Group group, String version) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(NAME, new String[] { group.getName() });
        if (group.getDescription() != null) {
            params.put(DESCRIPTION, new String[] { group.getDescription() });
        }
        if (group.getLocation() != null) {
            params.put(LOCATION, new String[] { group.getLocation() });
        }
        if (version != null) {
            params.put(VERSION, new String[] { version });
        }
        return connection.doPost(BASE_URI + "/create.hqu", params, this.responseHandler);
    }

    public GroupsResponse deleteGroup(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(GROUP_NAME, new String[] { group.getName() });
        return connection.doPost(BASE_URI + "/delete.hqu", params, this.responseHandler);
    }

    public GroupsResponse getGroups() throws IOException {
        return connection.doGet(BASE_URI + "/list.hqu", EMPTY_PARAMS, this.responseHandler);
    }

    public GroupsResponse removeServerFromGroup(Group group, Resource server) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null && group.getId() != 0) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        if (server.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(server.getId()) });
        }
        if (server.getName() != null) {
            params.put(SERVER_NAME, new String[] { server.getName() });
        }
        return connection.doPost(BASE_URI + "/removeServer.hqu", params, this.responseHandler);
    }

}
