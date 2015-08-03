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

package com.springsource.hq.plugin.tcserver.cli.client.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.hyperic.hq.hqapi1.XmlResponseHandler;

import com.springsource.hq.plugin.tcserver.cli.client.schema.ConfigurationStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptions;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsGroupRequest;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsRequest;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * Implementation of {@link ConfigurationRepository} that modifies/retrieves configuration from the server through Web
 * Service calls
 */
public class WebServiceConfigurationRepository implements ConfigurationRepository {

    private static final String BASE_URI = "/hqu/tcserverclient/serverconfig";

    private static final String FILE = "file";

    private static final String GROUP_ID = "groupid";

    private static final String GROUP_NAME = "groupname";

    private static final String SERVER_ID = "serverid";

    private static final String SERVER_NAME = "servername";

    private static final String TARGET_FILE = "targetfile";

    private static final String NO_BACKUP_FILE = "nobackupfile";

    private final Connection connection;

    private final ResponseHandler<ConfigurationStatusResponse> configurationResponseHandler;

    private final ResponseHandler<JvmOptionsResponse> jvmOptionsResponseHandler;

    /**
     * 
     * @param connection The {@link Connection} to the server
     */
    public WebServiceConfigurationRepository(Connection connection) {
        this(connection, new XmlResponseHandler<ConfigurationStatusResponse>(ConfigurationStatusResponse.class),
            new XmlResponseHandler<JvmOptionsResponse>(JvmOptionsResponse.class));
    }

    WebServiceConfigurationRepository(Connection connection, ResponseHandler<ConfigurationStatusResponse> configurationResponseHandler,
        ResponseHandler<JvmOptionsResponse> jvmOptionsResponseHandler) {
        this.connection = connection;
        this.configurationResponseHandler = configurationResponseHandler;
        this.jvmOptionsResponseHandler = jvmOptionsResponseHandler;
    }

    public ConfigurationStatusResponse getFile(Resource resource, String file, File targetFile) throws IOException {
        // Ensure we can create this file if not already existing before we call
        // the server
        try {
            targetFile.createNewFile();
        } catch (Exception e) {
            throw new IOException("Unable to create target file " + targetFile.getAbsolutePath() + ".  Cause: " + e.getMessage());
        }
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        params.put(FILE, new String[] { file });
        return connection.doGet(BASE_URI + "/getFile.hqu", params, targetFile, new FileResponseHandler<ConfigurationStatusResponse>(targetFile,
            ConfigurationStatusResponse.class));
    }

    public JvmOptionsResponse getJvmOptions(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doGet(BASE_URI + "/getJvmOptions.hqu", params, this.jvmOptionsResponseHandler);
    }

    public ConfigurationStatusResponse putFile(Group group, File file, String targetFile, boolean noBackup) throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find file: " + file.getAbsolutePath());
        }
        if (group.getName() != null) {
            params.put(GROUP_NAME, group.getName());
        }
        if (group.getId() != null) {
            params.put(GROUP_ID, Integer.toString(group.getId()));
        }
        params.put(NO_BACKUP_FILE, String.valueOf(noBackup));
        params.put(TARGET_FILE, targetFile);
        return connection.doPost(BASE_URI + "/putFile.hqu", params, file, this.configurationResponseHandler);
    }

    public ConfigurationStatusResponse putFile(Resource resource, File file, String targetFile, boolean noBackup) throws IOException {
        final Map<String, String> params = new HashMap<String, String>();
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find file: " + file.getAbsolutePath());
        }
        if (resource.getName() != null) {
            params.put(SERVER_NAME, resource.getName());
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, Integer.toString(resource.getId()));
        }
        params.put(NO_BACKUP_FILE, String.valueOf(noBackup));
        params.put(TARGET_FILE, targetFile);
        return connection.doPost(BASE_URI + "/putFile.hqu", params, file, this.configurationResponseHandler);
    }

    public ConfigurationStatusResponse revertToPreviousConfiguration(Resource resource) throws IOException {
        final Map<String, String[]> params = new HashMap<String, String[]>();
        if (resource.getName() != null) {
            params.put(SERVER_NAME, new String[] { resource.getName() });
        }
        if (resource.getId() != 0) {
            params.put(SERVER_ID, new String[] { Integer.toString(resource.getId()) });
        }
        return connection.doGet(BASE_URI + "/revertToPreviousConfiguration.hqu", params, this.configurationResponseHandler);
    }

    public JvmOptionsResponse setJvmOptions(Group group, JvmOptions jvmOptions) throws IOException {
        final JvmOptionsGroupRequest request = new JvmOptionsGroupRequest();
        request.setJvmOptions(jvmOptions);
        request.setGroup(group);
        return connection.doPost(BASE_URI + "/putJvmOptions.hqu", request, this.jvmOptionsResponseHandler);
    }

    public JvmOptionsResponse setJvmOptions(Resource server, JvmOptions jvmOptions) throws IOException {
        final JvmOptionsRequest request = new JvmOptionsRequest();
        request.setJvmOptions(jvmOptions);
        request.setResource(server);
        return connection.doPost(BASE_URI + "/putJvmOptions.hqu", request, this.jvmOptionsResponseHandler);
    }
}
