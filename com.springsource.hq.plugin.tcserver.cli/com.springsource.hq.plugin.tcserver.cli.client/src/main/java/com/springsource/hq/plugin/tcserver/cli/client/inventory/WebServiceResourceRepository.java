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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;

/**
 * Implementation of {@link ResourceRepository} responsible for retrieving and modifying resources (i.e. tc Servers)
 */
public class WebServiceResourceRepository implements ResourceRepository {

    private static final String BASE_URI = "/hqu/tcserverclient/servermanagement";

    private static final String DESCRIPTION = "description";

    private static final Map<String, String[]> EMPTY_PARAMS = new HashMap<String, String[]>(0, 1);

    private static final String GROUP_NAME = "groupname";

    private static final String NAME = "name";

    private static final String PLATFORM_NAME = "platformname";

    private static final String SERVER_ID = "serverid";

    private final ResponseHandler<ResourcesResponse> responseHandler;

    private Connection connection;

    /**
     * 
     * @param connection The {@link Connection} to the server
     */
    public WebServiceResourceRepository(Connection connection) {
        this(connection, new XmlResponseHandler<ResourcesResponse>(ResourcesResponse.class));
    }

    WebServiceResourceRepository(Connection connection, ResponseHandler<ResourcesResponse> responseHandler) {
        this.connection = connection;
        this.responseHandler = responseHandler;
    }

    public ResourcesResponse getServers() throws IOException {
        return connection.doGet(BASE_URI + "/listServers.hqu", EMPTY_PARAMS, responseHandler);
    }

    public ResourcesResponse getServersByGroup(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(GROUP_NAME, new String[] { group.getName() });
        return connection.doGet(BASE_URI + "/listServers.hqu", params, responseHandler);
    }

    public ResourcesResponse getServersByPlatform(Resource platform) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(PLATFORM_NAME, new String[] { platform.getName() });
        return connection.doGet(BASE_URI + "/listServers.hqu", params, responseHandler);
    }

    public ResourcesResponse modifyServer(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        if (resource.getName() != null) {
            params.put(NAME, new String[] { resource.getName() });
        }
        if (resource.getDescription() != null) {
            params.put(DESCRIPTION, new String[] { resource.getDescription() });
        }
        return connection.doPost(BASE_URI + "/modify.hqu", params, responseHandler);
    }

}
