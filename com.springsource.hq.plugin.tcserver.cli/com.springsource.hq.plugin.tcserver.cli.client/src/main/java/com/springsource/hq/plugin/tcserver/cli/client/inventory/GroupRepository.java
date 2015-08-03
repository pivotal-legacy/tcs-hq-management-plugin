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
import com.springsource.hq.plugin.tcserver.cli.client.schema.GroupsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;

/**
 * Manages group create/delete/modify/list operations against the server
 */
public interface GroupRepository {

    /**
     * Adds the specified server to the specified group
     * 
     * @param group The group on which to add the server
     * @param server The server to add
     * @return An {@link GroupsResponse} object indicating Success/Failure of add operation
     * @throws IOException If error connecting to server
     */
    GroupsResponse addServerToGroup(Group group, Resource server) throws IOException;

    /**
     * Creates the specified Group, using the name, id, location, and description parameters only
     * 
     * @param group The group to create
     * @param version The version of the group, either 6.0 or 7.0
     * @return An {@link GroupsResponse} object indicating Success/Failure of create operation
     * @throws IOException If error connecting to server
     */
    GroupsResponse createGroup(Group group, String version) throws IOException;

    /**
     * Deletes the specified Group
     * 
     * @param group The group to delete
     * @return An {@link GroupsResponse} object indicating Success/Failure of delete operation
     * @throws IOException If error connecting to server
     */
    GroupsResponse deleteGroup(Group group) throws IOException;

    /**
     * Retrieves all groups of type Compatible Group/Cluster composed of SpringSource tc Servers
     * 
     * @return An {@link GroupsResponse} object indicating Success/Failure of retrieval operation and containing all
     * @{link Group}s
     * @throws IOException If error connecting to server
     */
    GroupsResponse getGroups() throws IOException;

    /**
     * Removes the specified server from the specified group
     * 
     * @param group The group from which to remove the server
     * @param server The server to remove
     * @return An {@link GroupsResponse} object indicating Success/Failure of remove operation
     * @throws IOException If error connecting to server
     */
    GroupsResponse removeServerFromGroup(Group group, Resource server) throws IOException;

}
