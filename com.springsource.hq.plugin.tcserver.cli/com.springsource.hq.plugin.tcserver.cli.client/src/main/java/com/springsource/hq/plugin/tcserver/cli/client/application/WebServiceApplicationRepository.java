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

package com.springsource.hq.plugin.tcserver.cli.client.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.hyperic.hq.hqapi1.XmlResponseHandler;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;

/**
 * An implementation of {@link ApplicationRepository} that communicates with the server via web services calls
 */
public class WebServiceApplicationRepository implements ApplicationRepository {

    private static final String APPLICATION = "application";

    private static final String BASE_URI = "/hqu/tcserverclient/applicationmanagement";

    private static final String GROUP_ID = "groupid";

    private static final String GROUP_NAME = "groupname";

    private static final String HOST = "host";

    private static final String SERVER_ID = "serverid";

    private static final String SERVER_NAME = "servername";

    private static final String SERVICE = "service";

    private final ResponseHandler<ApplicationManagementResponse> responseHandler;

    private final Connection connection;

    /**
     * @param connection The {@link Connection} to the server
     */
    public WebServiceApplicationRepository(Connection connection) {
        this(connection, new XmlResponseHandler<ApplicationManagementResponse>(ApplicationManagementResponse.class));
    }

    WebServiceApplicationRepository(Connection connection, ResponseHandler<ApplicationManagementResponse> responseHandler) {
        this.connection = connection;
        this.responseHandler = responseHandler;
    }

    public ApplicationManagementResponse getApplications(Group group) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Application application, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Application application, Service service) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Application application, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(HOST, new String[] { host.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Service service) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Group group, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Application application, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Application application, Service service) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Application application, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATION, new String[] { application.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(HOST, new String[] { host.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Service service) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse getApplications(Resource resource, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        return connection.doGet(BASE_URI + "/listApplications.hqu", params, this.responseHandler);
    }

}
