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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.hyperic.hq.hqapi1.XmlResponseHandler;

import com.springsource.hq.plugin.tcserver.cli.client.schema.ControlStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * 
 * Implementation of {@link ControlOperationInvoker} that invokes control operations through web service calls to the
 * server
 */
public class WebServiceControlOperationInvoker implements ControlOperationInvoker {

    private static final String GROUP_BASE_URI = "/hqu/tcserverclient/groupmanagement";

    private static final String GROUP_ID = "groupid";

    private static final String GROUP_NAME = "groupname";

    private static final String SERVER_BASE_URI = "/hqu/tcserverclient/servermanagement";

    private static final String SERVER_ID = "serverid";

    private static final String SERVER_NAME = "servername";

    private final ResponseHandler<ControlStatusResponse> responseHandler;

    private final Connection connection;

    /**
     * 
     * @param connection The {@link Connection} to the server
     */
    public WebServiceControlOperationInvoker(Connection connection) {
        this(connection, new XmlResponseHandler<ControlStatusResponse>(ControlStatusResponse.class));
    }

    WebServiceControlOperationInvoker(Connection connection, ResponseHandler<ControlStatusResponse> responseHandler) {
        this.connection = connection;
        this.responseHandler = responseHandler;
    }

    public ControlStatusResponse restart(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        return connection.doPost(GROUP_BASE_URI + "/restart.hqu", params, this.responseHandler);
    }

    public ControlStatusResponse restart(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doPost(SERVER_BASE_URI + "/restart.hqu", params, this.responseHandler);
    }

    public ControlStatusResponse start(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        return connection.doPost(GROUP_BASE_URI + "/start.hqu", params, this.responseHandler);
    }

    public ControlStatusResponse start(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doPost(SERVER_BASE_URI + "/start.hqu", params, this.responseHandler);
    }

    public ControlStatusResponse stop(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        return connection.doPost(GROUP_BASE_URI + "/stop.hqu", params, this.responseHandler);
    }

    public ControlStatusResponse stop(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doPost(SERVER_BASE_URI + "/stop.hqu", params, this.responseHandler);
    }

}
