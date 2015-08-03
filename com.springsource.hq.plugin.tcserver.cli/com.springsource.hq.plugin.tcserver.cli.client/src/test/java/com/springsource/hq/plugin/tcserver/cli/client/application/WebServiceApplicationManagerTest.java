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

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.junit.After;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.ParamsEquals;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;

public class WebServiceApplicationManagerTest {

    private final File applicationFile = new File(System.getProperty("user.dir") + "/applicationManagerTest.war");

    @SuppressWarnings("unchecked")
    private ResponseHandler<ApplicationManagementResponse> responseHandler = createMock(ResponseHandler.class);

    private Connection connection = createMock(Connection.class);

    private final WebServiceApplicationManager applicationManager = new WebServiceApplicationManager(connection, responseHandler);

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    private ApplicationManagementResponse getExpectedReponse() {
        final ApplicationManagementResponse expected = new ApplicationManagementResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        return expected;
    }

    /**
     * Tears down the tests
     */
    @After
    public void tearDown() {
        applicationFile.delete();
    }

    /**
     * Verifies deployment of local application file to specified group/service/host combo
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupServiceHost() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationFile.createNewFile();
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("groupid", "10408");
        expectedParams.put("groupname", "Group1");
        expectedParams.put("service", "Catalina");
        expectedParams.put("host", "localhost");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu", expectedParams, applicationFile,
                this.responseHandler)).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(group, applicationFile, service, host);
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies deployment of local application file to specified group/service/host combo with specified context path
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupServiceHostContextPath() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationFile.createNewFile();
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("groupid", "10408");
        expectedParams.put("groupname", "Group1");
        expectedParams.put("service", "Catalina");
        expectedParams.put("host", "localhost");
        expectedParams.put("contextpath", "swf-booking-mvc");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu", expectedParams, applicationFile,
                this.responseHandler)).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(group, applicationFile, service, host, "swf-booking-mvc");
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies Exception is thrown attempting to deploy a non-existent local application file to specified
     * group/service/host/context path combo
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDeployApplicationGroupServiceHostContextPathInvalidFile() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationManager.deployApplication(group, applicationFile, service, host, "swf-booking-mvc");
    }

    /**
     * Verifies Exception is thrown attempting to deploy a non-existent local application file to specified
     * group/service/host combo
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDeployApplicationGroupServiceHostInvalidFile() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationManager.deployApplication(group, applicationFile, service, host);
    }

    /**
     * Verifies deployment of remote file to specified group/service/host combo
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupServiceHostRemotePath() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10408" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("remotepath", new String[] { "/tmp/swf-booking-mvc.war" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(group, "/tmp/swf-booking-mvc.war", service, host);
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies deployment of remote file to specified group/service/host combo with specified context path
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupServiceHostRemotePathContextPath() throws IOException {
        final Group group = new Group();
        group.setId(10408);
        group.setName("Group1");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10408" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("remotepath", new String[] { "/tmp/swf-booking-mvc.war" });
        expectedParams.put("contextpath", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(group, "/tmp/swf-booking-mvc.war", service, host,
            "swf-booking-mvc");
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies deployment of local application file to specified server/service/host combo
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerServiceHost() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationFile.createNewFile();
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("serverid", "10408");
        expectedParams.put("servername", "server2");
        expectedParams.put("service", "Catalina");
        expectedParams.put("host", "localhost");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu", expectedParams, applicationFile,
                this.responseHandler)).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(server, applicationFile, service, host);
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies deployment of local application file to specified server/service/host combo with specified context path
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerServiceHostContextPath() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationFile.createNewFile();
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("serverid", "10408");
        expectedParams.put("servername", "server2");
        expectedParams.put("service", "Catalina");
        expectedParams.put("host", "localhost");
        expectedParams.put("contextpath", "swf-booking-mvc");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu", expectedParams, applicationFile,
                this.responseHandler)).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(server, applicationFile, service, host, "swf-booking-mvc");
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies Exception is thrown attempting to deploy a non-existent local application file to specified
     * server/service/host/context path combo
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDeployApplicationServerServiceHostContextPathInvalidFile() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationManager.deployApplication(server, applicationFile, service, host, "swf-booking-mvc");
    }

    /**
     * Verifies Exception is thrown attempting to deploy a non-existent local application file to specified
     * server/service/host combo
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDeployApplicationServerServiceHostInvalidFile() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        applicationManager.deployApplication(server, applicationFile, service, host);
    }

    /**
     * Verifies deployment of remote file to specified server/service/host combo
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerServiceHostRemotePath() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10408" });
        expectedParams.put("servername", new String[] { "server2" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("remotepath", new String[] { "/tmp/swf-booking-mvc.war" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(server, "/tmp/swf-booking-mvc.war", service, host);
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies deployment of remote file to specified server/service/host combo with specified context path
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerServiceHostRemotePathContextPath() throws IOException {
        final Resource server = new Resource();
        server.setId(10408);
        server.setName("server2");
        final Service service = new Service();
        service.setName("Catalina");
        final Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10408" });
        expectedParams.put("servername", new String[] { "server2" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("remotepath", new String[] { "/tmp/swf-booking-mvc.war" });
        expectedParams.put("contextpath", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/deployApplication.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        final ApplicationManagementResponse response = applicationManager.deployApplication(server, "/tmp/swf-booking-mvc.war", service, host,
            "swf-booking-mvc");
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies that the proper web service call is made to reload an application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroup() throws IOException {
        final Group group = new Group();
        group.setId(10013);
        group.setName("Group1");
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/reloadApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);

        ApplicationManagementResponse returnedResponse = applicationManager.reloadApplication(group, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to reload an application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationServer() throws IOException {
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/reloadApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationManager.reloadApplication(resource, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to start an application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroup() throws IOException {
        final Group group = new Group();
        group.setId(10013);
        group.setName("Group1");
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/startApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);

        ApplicationManagementResponse returnedResponse = applicationManager.startApplication(group, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to start an application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationServer() throws IOException {
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/startApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationManager.startApplication(resource, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to stop an application
     * 
     * @throws IOException
     */
    @Test
    public void testStopApplicationGroup() throws IOException {
        final Group group = new Group();
        group.setId(10013);
        group.setName("Group1");
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/stopApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);

        ApplicationManagementResponse returnedResponse = applicationManager.stopApplication(group, expectedService, expectedHost, expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to stop an application
     * 
     * @throws IOException
     */
    @Test
    public void testStopApplicationServer() throws IOException {
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/stopApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationManager.stopApplication(resource, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to undeploy an application
     * 
     * @throws IOException
     */
    @Test
    public void testUndeployApplicationGroup() throws IOException {
        final Group group = new Group();
        group.setId(10013);
        group.setName("Group1");
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "Group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/undeployApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);

        ApplicationManagementResponse returnedResponse = applicationManager.undeployApplication(group, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that the proper web service call is made to undeploy an application
     * 
     * @throws IOException
     */
    @Test
    public void testUndeployApplicationServer() throws IOException {
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setVersion("1");

        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("applications", new String[] { "swf-booking-mvc" });
        expectedParams.put("version", new String[] { "1" });
        EasyMock.expect(
            connection.doPost(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/undeployApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationManager.undeployApplication(resource, expectedService, expectedHost,
            expectedApplication);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

}
