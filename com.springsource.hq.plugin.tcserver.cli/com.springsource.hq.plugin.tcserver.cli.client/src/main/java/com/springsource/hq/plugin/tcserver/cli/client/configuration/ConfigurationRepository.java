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
import java.io.IOException;

import com.springsource.hq.plugin.tcserver.cli.client.schema.ConfigurationStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptions;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * Manages operations for retrieving and setting tc Server Configuration
 */
public interface ConfigurationRepository {

    /**
     * Retrieve a config file from an existing tc Server
     * 
     * @param server The tc Server from which to retrieve the file
     * @param file The path of the file to retrieve, relative to the server's catalina.base directory (installpath)
     * @param targetFile The path to the local file to which the retrieved file should be written
     * @return An {@link ConfigurationStatusResponse} object indicating Success/Failure of the retrieval operation
     * @throws IOException If error connecting to server or if error writing to local target file
     */
    ConfigurationStatusResponse getFile(Resource server, String file, File targetFile) throws IOException;

    /**
     * Retrieves JVM Options from an existing tc Server
     * 
     * @param server The tc Server from which to retrieve the JVM Options
     * @return An {@link JvmOptionsResponse} object indicating Success/Failure of the retrieval operation and containing
     *         the retrieved JvmOptions
     * @throws IOException If error connecting to server
     */
    JvmOptionsResponse getJvmOptions(Resource server) throws IOException;

    /**
     * Writes a config file to a group of tc Servers
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which the file should be written
     * @param file The local file whose contents should be written to the tc Servers
     * @param targetFile The path of the file to write, relative to the tc Servers' catalina.base directory
     *        (installpath)
     * @param noBackup Turns off the backup file creation
     * @return An {@link ConfigurationStatusResponse} object indicating Success/Failure of the write operation
     * @throws IOException If error connecting to server or if error reading from local file
     */
    ConfigurationStatusResponse putFile(Group group, File file, String targetFile, boolean noBackup) throws IOException;

    /**
     * Writes a config file to a single tc Server
     * 
     * @param server The tc Server to which the file should be written
     * @param file The local file whose contents should be written to the tc Server
     * @param targetFile The path of the file to write, relative to the tc Server's catalina.base directory
     *        (installpath)
     * @param noBackup Turns off the backup file creation
     * @return An {@link ConfigurationStatusResponse} object indicating Success/Failure of the write operation
     * @throws IOException If error connecting to server or if error reading from local file
     */
    ConfigurationStatusResponse putFile(Resource server, File file, String targetFile, boolean noBackup) throws IOException;

    /**
     * Reverts configuration files to the last known backup.
     * 
     * @param resource The tc Server instance to revert to the previous configuration.
     * @return The response
     * @throws IOException
     */
    ConfigurationStatusResponse revertToPreviousConfiguration(Resource resource) throws IOException;

    /**
     * Writes JVM Options to a group of tc Servers
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which the JVM Options should be written
     * @param jvmOptions The JVM Options to write to the group of tc Servers
     * @return An {@link JvmOptionsResponse} object indicating Success/Failure of the write operation
     * @throws IOException If error connecting to server
     */
    JvmOptionsResponse setJvmOptions(Group group, JvmOptions jvmOptions) throws IOException;

    /**
     * Writes JVM Options to a single tc Server
     * 
     * @param server The tc Server to which the JVM Options should be written
     * @param jvmOptions The JVM Options to write to the tc Server
     * @return An {@link JvmOptionsResponse} object indicating Success/Failure of the write operation
     * @throws IOException If error connecting to server
     */
    JvmOptionsResponse setJvmOptions(Resource server, JvmOptions jvmOptions) throws IOException;

}
