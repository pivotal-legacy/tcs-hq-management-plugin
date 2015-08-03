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

import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;

/**
 * Retrieves applications deployed to tc Servers from the server
 */
public interface ApplicationRepository {

    /**
     * Retrieves applications deployed to a specific group. Will include all applications found on any server in the
     * group (even if not deployed to entire group). Will query all tc Runtime service/host combos found for every
     * server in the group
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group) throws IOException;

    /**
     * Retrieves applications deployed to a specific group and tc Runtime host. Will include all applications found on
     * any server in the group (even if not deployed to entire group). Will query all services.
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param host The tc Runtime host (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Host host) throws IOException;

    /**
     * Retrieves applications deployed to a specific group and tc Runtime service. Will include all applications found
     * on any server in the group (even if not deployed to entire group). Will include applications found on any host in
     * the service
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Service service) throws IOException;

    /**
     * Retrieves applications deployed to a specific group and tc Runtime service/host combo. Will include all
     * applications found on any server in the group (even if not deployed to entire group).
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @param host The tc Runtime host (defined in server.xml) under the specified tc Runtime service to query for
     *        applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Service service, Host host) throws IOException;

    /**
     * Retrieves a specific application by name on a specific group. All tc Runtime service/host combos on the server
     * will be queried.
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param application The application to look for
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found on any servers. status will indicate "Mixed" if found on subset of servers in group or if not in
     *         consistent started/stopped state across group)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Application application) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime host on a specific group. All services will be
     * queried.
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param application The application to look for
     * @param host The tc Runtime host (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found on any servers. status will indicate "Mixed" if found on subset of servers in group or if not in
     *         consistent started/stopped state across group)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Application application, Host host) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime service on a specific group. All hosts of the
     * service will be queried.
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param application The application to look for
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found on any servers. status will indicate "Mixed" if found on subset of servers in group or if not in
     *         consistent started/stopped state across group)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Application application, Service service) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime service/host combo on a specific group.
     * 
     * @param group The Compatible Group/Cluster of tc Servers to query
     * @param application The application to look for
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @param host The tc Runtime host (defined in server.xml) under the specified tc Runtime service to query for
     *        applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found on any servers. status will indicate "Mixed" if found on subset of servers in group or if not in
     *         consistent started/stopped state across group)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Group group, Application application, Service service, Host host) throws IOException;

    /**
     * Retrieves applications deployed to a specific server. Will query all tc Runtime service/host combos
     * 
     * @param resource The server to query
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource) throws IOException;

    /**
     * Retrieves applications deployed to a specific server and tc Runtime host. All services will be queried.
     * 
     * @param resource The server to query
     * @param host The tc Runtime host (defined in server.xml) to query for applications
     * 
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Host host) throws IOException;

    /**
     * Retrieves applications deployed to a specific server and tc Runtime service. All hosts contained by the service
     * will be queried.
     * 
     * @param resource The server to query
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * 
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Service service) throws IOException;

    /**
     * Retrieves applications deployed to a specific server and tc Runtime service/host combo. Both service and host
     * must be specified.
     * 
     * @param resource The server to query
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @param host The tc Runtime host (defined in server.xml) under the specified tc Runtime service to query for
     *        applications
     * 
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing all found Applications
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Service service, Host host) throws IOException;

    /**
     * Retrieves a specific application by name on a specific server. All tc Runtime service/host combos on the server
     * will be queried.
     * 
     * @param resource The server to query
     * @param application The application to look for
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Application application) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime host on a specific server. All services will be
     * queried.
     * 
     * @param resource The server to query
     * @param application The application to look for
     * @param host The tc Runtime host (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Application application, Host host) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime service on a specific server. All hosts of the
     * service will be queried.
     * 
     * @param resource The server to query
     * @param application The application to look for
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Application application, Service service) throws IOException;

    /**
     * Retrieves a specific application by name on a specific tc Runtime service/host combo on a specific server.
     * 
     * @param resource The server to query
     * @param application The application to look for
     * @param service The tc Runtime service (defined in server.xml) to query for applications
     * @param host The tc Runtime host (defined in server.xml) under the specified tc Runtime service to query for
     *        applications
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of retrieval operation and
     *         containing information about the Application (status will indicate "Not Deployed" if application was not
     *         found)
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse getApplications(Resource resource, Application application, Service service, Host host) throws IOException;

}
