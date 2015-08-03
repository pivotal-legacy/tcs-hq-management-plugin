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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.application.ApplicationManager;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Application;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of {@link StartApplicationCommand}
 */
public class StartApplicationCommandTest {

    private ApplicationManager applicationManager;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private StartApplicationCommand startApplicationCommand;

    private String newline = System.getProperty("line.separator");

    private ApplicationManagementResponse getExpectedResponse() {
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }

    /**
     * Sets up the tests
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.applicationManager = EasyMock.createMock(ApplicationManager.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.startApplicationCommand = new StartApplicationCommand(applicationManager, optionParser, outWriter, errorWriter);
        this.startApplicationCommand.afterPropertiesSet();
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
    public void testStartApplicationBothServerAndGroupParams() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[] { "--groupname=Group1", "--serverid=123", "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testStartApplicationServerNameAndServerId() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[] { "--servername=test-server", "--serverid=123", "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs starting application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(applicationManager.startApplication(server, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--servername=server1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command start-application.  Reason: Something bad happened", output);
    }

    /**
     * Verifies that an application is started when just the group and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroupApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs starting application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(applicationManager.startApplication(group, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--groupname=group1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command start-application.  Reason: Something bad happened", output);
    }

    /**
     * Verifies that an application is started correctly when a specific host is specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroupHostApplication() throws IOException {
        final Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies that an application is started correctly when a specific service is specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroupServiceApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc", "--tcservice=service2" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs starting application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationGroupSpecificFailures() throws IOException {
        Group group = new Group();
        group.setName("group1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        statusResponse.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);

        StatusResponse statusResponse2 = new StatusResponse();
        statusResponse2.setResourceName("server2");
        ServiceError error2 = new ServiceError();
        error2.setReasonText("Disaster");
        statusResponse2.setError(error2);
        statusResponse2.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse2);
        EasyMock.expect(applicationManager.startApplication(group, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--groupname=group1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(2, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to start application on server1.  Reason: Something bad happened" + newline
            + "Failed to start application on server2.  Reason: Disaster", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationHelpOption() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies an error is printed when the --application option is missing
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationMissingApplicationOption() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[] { "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --application", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that a usage stmt is printed when the command is invoked with no options
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationNoParams() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[0]);
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
    public void testStartApplicationNoServerOrGroupParams() throws IOException {
        int exitCode = startApplicationCommand.execute(new String[] { "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an application is started when just the server and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationServerApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies that an application is started correctly when a specific host is specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationServerHostApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies that an application is started correctly when a specific service is specified
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationServerServiceApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.startApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc", "--tcservice=service2" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs starting application
     * 
     * @throws IOException
     */
    @Test
    public void testStartApplicationSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(ResponseStatus.FAILURE);
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(applicationManager.startApplication(server, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = startApplicationCommand.execute(new String[] { "--servername=server1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to start application on server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(StartApplicationCommand.OPT_SERVER_NAME, StartApplicationCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_SERVER_ID, StartApplicationCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_GROUP_NAME, StartApplicationCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_GROUP_ID, StartApplicationCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_SERVICE, StartApplicationCommand.OPT_SERVICE_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_HOST, StartApplicationCommand.OPT_HOST_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_APPLICATION, StartApplicationCommand.OPT_APPLICATION_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(StartApplicationCommand.OPT_REVISION, StartApplicationCommand.OPT_REVISION_DESC).withRequiredArg().ofType(
                String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(startApplicationCommand.getName() + ": " + startApplicationCommand.getDescription());

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
