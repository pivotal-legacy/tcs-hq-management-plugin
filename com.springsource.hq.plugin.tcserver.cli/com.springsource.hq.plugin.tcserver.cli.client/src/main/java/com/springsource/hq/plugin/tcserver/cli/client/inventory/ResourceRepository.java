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

import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;

/**
 * Performs list resource operations against the server
 */
public interface ResourceRepository {

    /**
     * Returns a list of servers of type SpringSource tc Server
     * 
     * @return An {@link ResourcesResponse} object indicating Success/Failure of retrieval operation and containing all
     *         found {@link Resource}s
     * @throws IOException If error connecting to server
     */
    ResourcesResponse getServers() throws IOException;

    /**
     * Returns a list of servers of type SpringSource tc Server in the specified Group
     * 
     * @param group The group to filter server listings from - id of group will be used to query if present, else name
     *        will be used
     * @return An {@link ResourcesResponse} object indicating Success/Failure of retrieval operation and containing all
     *         found {@link Resource}s
     * @throws IOException If error connecting to server
     */
    ResourcesResponse getServersByGroup(Group group) throws IOException;

    /**
     * Returns a list of servers of type SpringSource tc Server on the specified Platform
     * 
     * @param platform The platform to filter server listings from - id of platform will be used to query if present,
     *        else name will be used
     * @return An {@link ResourcesResponse} object indicating Success/Failure of retrieval operation and containing all
     *         found {@link Resource}s
     * @throws IOException If error connecting to server
     */
    ResourcesResponse getServersByPlatform(Resource platform) throws IOException;

    /**
     * Modifies the properties of a Resource - currently just name and description
     * 
     * @param resource The resource to modify
     * @return An {@link ResourcesResponse} indicating Success/Failure of modify operation
     * @throws IOException
     */
    ResourcesResponse modifyServer(Resource resource) throws IOException;

}
