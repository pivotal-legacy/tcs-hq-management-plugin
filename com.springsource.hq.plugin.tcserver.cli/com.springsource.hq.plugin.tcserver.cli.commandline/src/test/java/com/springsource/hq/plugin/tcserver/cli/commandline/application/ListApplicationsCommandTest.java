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

package com.springsource.hq.plugin.tcserver.cli.commandline.application;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import joptsimple.OptionParser;

import org.easymock.EasyMock;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.application.ApplicationRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Result;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of {@link ListApplicationsCommand}
 */
public class ListApplicationsCommandTest {

    private ApplicationRepository applicationRepository;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private ListApplicationsCommand listApplicationsCommand;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private String newline = System.getProperty("line.separator");

    private ApplicationManagementResponse getExpectedResponse() {
        final Service expectedService = new Service();
        expectedService.setName("Catalina");
        final Host expectedHost = new Host();
        expectedHost.setName("localhost");
        final Application application = new Application();
        application.setName("swf-booking-mvc");
        application.setStatus("Stopped");
        application.setSessions("0");
        expectedHost.getApplication().add(application);
        expectedService.getHost().add(expectedHost);
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setResult(new Result());
        response.getResult().getService().add(expectedService);
        return response;
    }

    /**
     * Sets up the tests
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.applicationRepository = EasyMock.createMock(ApplicationRepository.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.listApplicationsCommand = new ListApplicationsCommand(applicationRepository, optionParser, outWriter, errorWriter);
        this.listApplicationsCommand.afterPropertiesSet();
    }

    /**
     * Tears down the tests
     */
    @After
    public void tearDown() {
        this.errorWriter.close();
        this.outWriter.close();
    }

    /**
     * Verifies an error is printed when both server and group params are used
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsBothServerAndGroupParams() throws IOException {
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupname=Group1", "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testListApplicationsServerIdAndServerName() throws IOException {
        int exitCode = listApplicationsCommand.execute(new String[] { "--servername=server-name", "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that applications are listed when just the group is specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroup() throws IOException {
        Group group = new Group();
        group.setId(123);
        EasyMock.expect(applicationRepository.getApplications(group)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Application application = new Application();
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationRepository.getApplications(group, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group, host, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupApplicationHost() throws IOException {
        Group group = new Group();
        group.setName("group1");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(group, application, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupname=group1", "--tchost=localhost", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies correct output if general failure occurs listing applications
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setId(123);
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("General Failure");
        response.setError(error);
        EasyMock.expect(applicationRepository.getApplications(group)).andReturn(response);
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command list-applications.  Reason: General Failure", output);
    }

    /**
     * Verifies that applications are listed when the group and host are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupHost() throws IOException {
        Group group = new Group();
        group.setId(123);
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(group, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--tchost=localhost" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group and service are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupService() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        EasyMock.expect(applicationRepository.getApplications(group, service)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--tcservice=Catalina" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group, service, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupServiceApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationRepository.getApplications(group, application, service)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--tcservice=Catalina", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group, service, host, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupServiceApplicationHost() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(group, application, service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--tcservice=Catalina", "--tchost=localhost",
            "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the group, service, and host are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupServiceHost() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(group, service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123", "--tcservice=Catalina", "--tchost=localhost" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies correct output if specific failure occurs listing applications
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsGroupSpecificFailures() throws IOException {
        Group group = new Group();
        group.setId(123);
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        StatusResponse statusResponse2 = new StatusResponse();
        statusResponse2.setResourceName("server2");
        statusResponse2.setStatus(ResponseStatus.FAILURE);
        ServiceError error2 = new ServiceError();
        error2.setReasonText("Something else");
        statusResponse2.setError(error2);
        response.getStatusResponse().add(statusResponse2);

        EasyMock.expect(applicationRepository.getApplications(group)).andReturn(response);
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--groupid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(2, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to retrieve applications from server1.  Reason: Something bad happened" + newline
            + "Failed to retrieve applications from server2.  Reason: Something else", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsHelpOption() throws IOException {
        int exitCode = listApplicationsCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that a usage stmt is printed when the command is invoked with no options
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsNoParams() throws IOException {
        int exitCode = listApplicationsCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w/out server or group params
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsNoServerOrGroupParams() throws IOException {
        int exitCode = listApplicationsCommand.execute(new String[] { "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that applications are listed when just the server is specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServer() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        EasyMock.expect(applicationRepository.getApplications(server)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Application application = new Application();
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationRepository.getApplications(server, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server, host, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerApplicationHost() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(server, application, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--servername=server1", "--tchost=localhost", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies correct output if general failure occurs listing applications
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("General Failure");
        response.setError(error);
        EasyMock.expect(applicationRepository.getApplications(server)).andReturn(response);
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command list-applications.  Reason: General Failure", output);
    }

    /**
     * Verifies that applications are listed when the server and host are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerHost() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(server, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--tchost=localhost" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server and service are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerService() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        EasyMock.expect(applicationRepository.getApplications(server, service)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--tcservice=Catalina" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server, service, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerServiceApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationRepository.getApplications(server, application, service)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--tcservice=Catalina", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server, service, host, and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerServiceApplicationHost() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Application application = new Application();
        application.setName("swf-booking-mvc");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(server, application, service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--tcservice=Catalina", "--tchost=localhost",
            "--application=swf-booking-mvc" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies that applications are listed when the server service, and host are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerServiceHost() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationRepository.getApplications(server, service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123", "--tcservice=Catalina", "--tchost=localhost" });
        EasyMock.verify(applicationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Catalina|localhost|swf-booking-mvc|0|Stopped|0", output);
    }

    /**
     * Verifies correct output if specific failure occurs listing applications
     * 
     * @throws IOException
     */
    @Test
    public void testListApplicationsServerSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        statusResponse.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(applicationRepository.getApplications(server)).andReturn(response);
        EasyMock.replay(applicationRepository);
        int exitCode = listApplicationsCommand.execute(new String[] { "--serverid=123" });
        EasyMock.verify(applicationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to retrieve applications from server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(ListApplicationsCommand.OPT_SERVER_NAME, ListApplicationsCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_SERVER_ID, ListApplicationsCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_GROUP_NAME, ListApplicationsCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_GROUP_ID, ListApplicationsCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_SERVICE, ListApplicationsCommand.OPT_SERVICE_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_HOST, ListApplicationsCommand.OPT_HOST_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(ListApplicationsCommand.OPT_APPLICATION, ListApplicationsCommand.OPT_APPLICATION_DESC).withRequiredArg().ofType(
                String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(listApplicationsCommand.getName() + ": " + listApplicationsCommand.getDescription());

            testOptionParser.printHelpOn(pw);

            output = sw.getBuffer().toString().trim();

        } catch (IOException e) {
            // Errors with the testOptionParser.printHelpOn() call
            e.printStackTrace();
        } catch (Exception e) {
            // Errors with the creation of the testOptionParser
            e.printStackTrace();
        }

        return output;
    }

}
