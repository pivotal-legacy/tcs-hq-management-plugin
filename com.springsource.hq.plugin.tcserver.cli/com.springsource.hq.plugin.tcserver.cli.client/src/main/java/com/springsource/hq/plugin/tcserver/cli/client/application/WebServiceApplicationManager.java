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

import java.io.File;
import java.io.FileNotFoundException;
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
 * Implementation of {@link ApplicationManager} that manages applications through the server via web services calls
 */
public class WebServiceApplicationManager implements ApplicationManager {

    private static final String APPLICATIONS = "applications";

    private static final String BASE_URI = "/hqu/tcserverclient/applicationmanagement";

    private static final String CONTEXT_PATH = "contextpath";

    private static final String GROUP_ID = "groupid";

    private static final String GROUP_NAME = "groupname";

    private static final String HOST = "host";

    private static final String REMOTE_PATH = "remotepath";

    private static final String SERVER_ID = "serverid";

    private static final String SERVER_NAME = "servername";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private final ResponseHandler<ApplicationManagementResponse> responseHandler;

    private final Connection connection;

    /**
     * @param connection The {@link Connection} to the server
     */
    public WebServiceApplicationManager(Connection connection) {
        this(connection, new XmlResponseHandler<ApplicationManagementResponse>(ApplicationManagementResponse.class));
    }

    WebServiceApplicationManager(Connection connection, ResponseHandler<ApplicationManagementResponse> responseHandler) {
        this.connection = connection;
        this.responseHandler = responseHandler;
    }

    public ApplicationManagementResponse deployApplication(Group group, File application, Service service, Host host) throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!application.exists()) {
            throw new FileNotFoundException("Cannot find file: " + application.getAbsolutePath());
        }
        if (group.getName() != null) {
            params.put(GROUP_NAME, group.getName());
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, Integer.toString(group.getId()));
        }
        params.put(SERVICE, service.getName());
        params.put(HOST, host.getName());
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, application, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Group group, File application, Service service, Host host, String contextPath)
        throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!application.exists()) {
            throw new FileNotFoundException("Cannot find file: " + application.getAbsolutePath());
        }
        if (group.getName() != null) {
            params.put(GROUP_NAME, group.getName());
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, Integer.toString(group.getId()));
        }
        params.put(SERVICE, service.getName());
        params.put(HOST, host.getName());
        params.put(CONTEXT_PATH, contextPath);
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, application, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Group group, String remotePath, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(REMOTE_PATH, new String[] { remotePath });
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Group group, String remotePath, Service service, Host host, String contextPath)
        throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(REMOTE_PATH, new String[] { remotePath });
        params.put(CONTEXT_PATH, new String[] { contextPath });
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Resource resource, File application, Service service, Host host) throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!application.exists()) {
            throw new FileNotFoundException("Cannot find file: " + application.getAbsolutePath());
        }
        if (resource.getName() != null) {
            params.put(SERVER_NAME, resource.getName());
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, Integer.toString(resource.getId()));
        }
        params.put(SERVICE, service.getName());
        params.put(HOST, host.getName());
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, application, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Resource resource, File application, Service service, Host host, String contextPath)
        throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!application.exists()) {
            throw new FileNotFoundException("Cannot find file: " + application.getAbsolutePath());
        }
        if (resource.getName() != null) {
            params.put(SERVER_NAME, resource.getName());
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, Integer.toString(resource.getId()));
        }
        params.put(SERVICE, service.getName());
        params.put(HOST, host.getName());
        params.put(CONTEXT_PATH, contextPath);
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, application, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Resource resource, String remotePath, Service service, Host host) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(REMOTE_PATH, new String[] { remotePath });
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse deployApplication(Resource resource, String remotePath, Service service, Host host, String contextPath)
        throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(REMOTE_PATH, new String[] { remotePath });
        params.put(CONTEXT_PATH, new String[] { contextPath });
        return connection.doPost(BASE_URI + "/deployApplication.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse reloadApplication(Group group, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/reloadApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse reloadApplication(Resource resource, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/reloadApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse startApplication(Group group, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/startApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse startApplication(Resource resource, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/startApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse stopApplication(Group group, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/stopApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse stopApplication(Resource resource, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/stopApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse undeployApplication(Group group, Service service, Host host, Application application) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (group.getName() != null) {
            params.put(GROUP_NAME, new String[] { group.getName() });
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, new String[] { Integer.toString(group.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/undeployApplications.hqu", params, this.responseHandler);
    }

    public ApplicationManagementResponse undeployApplication(Resource resource, Service service, Host host, Application application)
        throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(SERVICE, new String[] { service.getName() });
        params.put(HOST, new String[] { host.getName() });
        params.put(APPLICATIONS, new String[] { application.getName() });
        params.put(VERSION, new String[] { application.getVersion() });
        return connection.doPost(BASE_URI + "/undeployApplications.hqu", params, this.responseHandler);
    }

}
