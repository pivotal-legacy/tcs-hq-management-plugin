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

import com.springsource.hq.plugin.tcserver.cli.client.schema.ControlStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * Invokes control operations against a tc Server or group of tc Servers
 */
public interface ControlOperationInvoker {

    /**
     * Restarts a Compatible Group/Cluster of tc Servers
     * 
     * @param group The group of tc Servers to restart
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse restart(Group group) throws IOException;

    /**
     * Restarts a tc Server
     * 
     * @param resource The tc Server to restart
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse restart(Resource resource) throws IOException;

    /**
     * Starts a Compatible Group/Cluster of tc Servers
     * 
     * @param group The group of tc Servers to start
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse start(Group group) throws IOException;

    /**
     * Starts a tc Server
     * 
     * @param resource The tc Server to start
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse start(Resource resource) throws IOException;

    /**
     * Stops a Compatible Group/Cluster of tc Servers
     * 
     * @param group The group of tc Servers to stop
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse stop(Group group) throws IOException;

    /**
     * Stops a tc Server
     * 
     * @param resource The tc Server to stop
     * @return An {@link ControlStatusResponse} object indicating Success/Failure of the operation
     * @throws IOException If error connecting to server
     */
    ControlStatusResponse stop(Resource resource) throws IOException;

}
