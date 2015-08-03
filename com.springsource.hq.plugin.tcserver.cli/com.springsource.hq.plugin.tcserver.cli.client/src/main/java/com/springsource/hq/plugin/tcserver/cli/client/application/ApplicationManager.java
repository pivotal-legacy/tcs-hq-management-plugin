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
import java.io.IOException;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;

/**
 * Component responsible for managing applications on the tc Server (deploy, start, stop reload, undeploy)
 */
public interface ApplicationManager {

    /**
     * Deploys the specified application by uploading the contents of a local WAR file
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which to deploy the application
     * @param application A path to a local WAR file to deploy
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException If error connecting to server or if error reading the local WAR file
     */
    ApplicationManagementResponse deployApplication(Group group, File application, Service service, Host host) throws IOException;

    /**
     * Deploys the specified application by uploading the contents of a local WAR file
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which to deploy the application
     * @param application A path to a local WAR file to deploy
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @param contextPath The context path to give the deployed application
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse deployApplication(Group group, File application, Service service, Host host, String contextPath) throws IOException;

    /**
     * Deploys a WAR file that is resident to the machine on which the tc Runtime server is running
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which to deploy the application
     * @param remotePath Full path to WAR file resolvable from machine the tc Runtime server is running on
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse deployApplication(Group group, String remotePath, Service service, Host host) throws IOException;

    /**
     * Deploys a WAR file that is resident to the machine on which the tc Runtime server is running
     * 
     * @param group The Compatible Group/Cluster of tc Servers to which to deploy the application
     * @param remotePath Full path to WAR file resolvable from machine the tc Runtime server is running on
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @param contextPath The context path to give the deployed application
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException
     */
    ApplicationManagementResponse deployApplication(Group group, String remotePath, Service service, Host host, String contextPath)
        throws IOException;

    /**
     * Deploys the specified application by uploading the contents of a local WAR file
     * 
     * @param resource The tc Runtime server to which to deploy the application
     * @param application A path to a local WAR file to deploy
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException If error connecting to server
     */
    ApplicationManagementResponse deployApplication(Resource resource, File application, Service service, Host host) throws IOException;

    /**
     * Deploys the specified application by uploading the contents of a local WAR file
     * 
     * @param resource The tc Runtime server to which to deploy the application
     * @param application A path to a local WAR file to deploy
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @param contextPath The context path to give the deployed application
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException
     */
    ApplicationManagementResponse deployApplication(Resource resource, File application, Service service, Host host, String contextPath)
        throws IOException;

    /**
     * Deploys a WAR file that is resident to the machine on which the tc Runtime server is running
     * 
     * @param resource The tc Runtime server to which to deploy the application
     * @param remotePath Full path to WAR file resolvable from machine the tc Runtime server is running on
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException
     */
    ApplicationManagementResponse deployApplication(Resource resource, String remotePath, Service service, Host host) throws IOException;

    /**
     * Deploys a WAR file that is resident to the machine on which the tc Runtime server is running
     * 
     * @param resource The tc Runtime server to which to deploy the application
     * @param remotePath Full path to WAR file resolvable from machine the tc Runtime server is running on
     * @param service The tc Runtime service (defined in server.xml) to which the application should be deployed
     * @param host The tc Runtime host (defined in server.xml) to which the application should be deployed
     * @param contextPath The context path to give the deployed application
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the deploy operation
     * @throws IOException
     */
    ApplicationManagementResponse deployApplication(Resource resource, String remotePath, Service service, Host host, String contextPath)
        throws IOException;

    /**
     * Reloads the specified application
     * 
     * @param group The Compatible Group/Cluster of tc Runtime servers on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to reload
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the reload operation
     * @throws IOException
     */
    ApplicationManagementResponse reloadApplication(Group group, Service service, Host host, Application application) throws IOException;

    /**
     * Reloads the specified application
     * 
     * @param resource The tc Runtime Server on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to reload
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the reload operation
     * @throws IOException
     */
    ApplicationManagementResponse reloadApplication(Resource resource, Service service, Host host, Application application) throws IOException;

    /**
     * Starts the specified application
     * 
     * @param group The Compatible Group/Cluster of tc Runtime servers on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to start
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the start operation
     * @throws IOException
     */
    ApplicationManagementResponse startApplication(Group group, Service service, Host host, Application application) throws IOException;

    /**
     * Starts the specified application
     * 
     * @param resource The tc Runtime Server on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to start
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the start operation
     * @throws IOException
     */
    ApplicationManagementResponse startApplication(Resource resource, Service service, Host host, Application application) throws IOException;

    /**
     * Stops the specified application
     * 
     * @param group The Compatible Group/Cluster of tc Runtime servers on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to stop
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the stop operation
     * @throws IOException
     */
    ApplicationManagementResponse stopApplication(Group group, Service service, Host host, Application application) throws IOException;

    /**
     * Stops the specified application
     * 
     * @param resource The tc Runtime Server on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to stop
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the stop operation
     * @throws IOException
     */
    ApplicationManagementResponse stopApplication(Resource resource, Service service, Host host, Application application) throws IOException;

    /**
     * Undeploys the specified application
     * 
     * @param group The Compatible Group/Cluster of tc Runtime servers on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to undeploy
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the undeploy operation
     * @throws IOException
     */
    ApplicationManagementResponse undeployApplication(Group group, Service service, Host host, Application application) throws IOException;

    /**
     * Undeploys the specified application
     * 
     * @param resource The tc Runtime Server on which the application is deployed
     * @param service The tc Runtime service (defined in server.xml) on which the application is deployed
     * @param host The tc Runtime host (defined in server.xml) on which the application is deployed
     * @param application The application to undeploy
     * @return An {@link ApplicationManagementResponse} object indicating Success/Failure of the undeploy operation
     * @throws IOException
     */
    ApplicationManagementResponse undeployApplication(Resource resource, Service service, Host host, Application application) throws IOException;

}
