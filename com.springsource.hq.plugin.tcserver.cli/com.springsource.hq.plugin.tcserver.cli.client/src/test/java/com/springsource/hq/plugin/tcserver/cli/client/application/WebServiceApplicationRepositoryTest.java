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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.ParamsEquals;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Result;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;

/**
 * Unit test of the {@link WebServiceApplicationRepository}
 */
public class WebServiceApplicationRepositoryTest {

    @SuppressWarnings("unchecked")
    private final ResponseHandler<ApplicationManagementResponse> responseHandler = createMock(ResponseHandler.class);

    private final Connection connection = EasyMock.createMock(Connection.class);

    private final WebServiceApplicationRepository applicationRepository = new WebServiceApplicationRepository(connection, responseHandler);

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    private ApplicationManagementResponse getExpectedReponse() {
        final ApplicationManagementResponse expected = new ApplicationManagementResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        Service expectedService = new Service();
        expectedService.setName("Catalina");
        Host expectedHost = new Host();
        expectedHost.setName("localhost");
        Application expectedApplication = new Application();
        expectedApplication.setName("swf-booking-mvc");
        expectedApplication.setStatus("Running");
        expectedHost.getApplication().add(expectedApplication);
        expectedService.getHost().add(expectedHost);
        expected.setResult(new Result());
        expected.getResult().getService().add(expectedService);
        return expected;
    }

    /**
     * Verifies that applications for specified group are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroup() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupApplication() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/host/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupApplicationHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/host combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("host", new String[] { "localhost" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/service combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupService() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/service/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupServiceApplication() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/service/host/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupServiceApplicationHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0), expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified group/service/host combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsGroupServiceHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("groupid", new String[] { "10013" });
        expectedParams.put("groupname", new String[] { "group1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Group group = new Group();
        group.setId(10013);
        group.setName("group1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(group,
            expectedResponse.getResult().getService().get(0), expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServer() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource);
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerApplication() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/host/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerApplicationHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/host combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("host", new String[] { "localhost" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/service combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerService() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/service/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerServiceApplication() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/service/host/application combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerServiceApplicationHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        expectedParams.put("application", new String[] { "swf-booking-mvc" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0).getHost().get(0).getApplication().get(0),
            expectedResponse.getResult().getService().get(0), expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

    /**
     * Verifies that applications for specified server/service/host combo are retrieved correctly
     * 
     * @throws IOException
     */
    @Test
    public void testGetApplicationsServerServiceHost() throws IOException {
        final ApplicationManagementResponse expectedResponse = getExpectedReponse();
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "10013" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("service", new String[] { "Catalina" });
        expectedParams.put("host", new String[] { "localhost" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/applicationmanagement/listApplications.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.responseHandler))).andReturn(expectedResponse);
        EasyMock.replay(connection);
        Resource resource = new Resource();
        resource.setId(10013);
        resource.setName("server1");
        ApplicationManagementResponse returnedResponse = applicationRepository.getApplications(resource,
            expectedResponse.getResult().getService().get(0), expectedResponse.getResult().getService().get(0).getHost().get(0));
        EasyMock.verify(connection);
        assertEquals(expectedResponse, returnedResponse);
    }

}
