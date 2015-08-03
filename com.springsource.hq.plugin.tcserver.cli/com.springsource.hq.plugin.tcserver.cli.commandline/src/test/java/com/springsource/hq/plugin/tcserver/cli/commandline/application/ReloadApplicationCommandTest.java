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
 * Unit test of the {@link ReloadApplicationCommand}
 * 
 */
public class ReloadApplicationCommandTest {

    private ApplicationManager applicationManager;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private ReloadApplicationCommand reloadApplicationCommand;

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
        this.reloadApplicationCommand = new ReloadApplicationCommand(applicationManager, optionParser, outWriter, errorWriter);
        this.reloadApplicationCommand.afterPropertiesSet();
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
    public void testReloadApplicationBothServerAndGroupParams() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupname=Group1", "--serverid=123", "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testReloadApplicationServerNameAndServerId() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[] { "--servername=test-server", "--serverid=123", "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs reloading application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGeneralFailure() throws IOException {
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
        EasyMock.expect(applicationManager.reloadApplication(server, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--servername=server1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command reload-application.  Reason: Something bad happened", output);
    }

    /**
     * Verifies that an application is reloaded when just the group and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroupApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs reloading application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setName("swf-booking-mvc");
        application.setVersion("");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(applicationManager.reloadApplication(group, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupname=group1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command reload-application.  Reason: Something bad happened", output);
    }

    /**
     * Verifies that an application is reloaded correctly when a specific host is specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroupHostApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies that an application is reloaded correctly when a specific service is specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroupServiceApplication() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(group, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupid=123", "--application=swf-booking-mvc", "--tcservice=service2" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs reloading application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationGroupSpecificFailure() throws IOException {
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
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        StatusResponse statusResponse2 = new StatusResponse();
        statusResponse2.setResourceName("server2");
        statusResponse2.setStatus(ResponseStatus.FAILURE);
        ServiceError error2 = new ServiceError();
        error2.setReasonText("Catastrophe!");
        statusResponse2.setError(error2);
        response.getStatusResponse().add(statusResponse2);

        EasyMock.expect(applicationManager.reloadApplication(group, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--groupname=group1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(2, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to reload application on server1.  Reason: Something bad happened" + newline
            + "Failed to reload application on server2.  Reason: Catastrophe!", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationHelpOption() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[] { "--help" });
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
    public void testReloadApplicationMissingApplicationOption() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[] { "--serverid=123" });
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
    public void testReloadApplicationNoParams() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[0]);
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
    public void testReloadApplicationNoServerOrGroupParams() throws IOException {
        int exitCode = reloadApplicationCommand.execute(new String[] { "--application=swf-booking-mvc" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an application is reloaded when just the server and application are specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationServerApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies that an application is reloaded correctly when a specific host is specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationServerHostApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies that an application is reloaded correctly when a specific service is specified
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationServerServiceApplication() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        final Application application = new Application();
        application.setVersion("");
        application.setName("swf-booking-mvc");
        EasyMock.expect(applicationManager.reloadApplication(server, service, host, application)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--serverid=123", "--application=swf-booking-mvc", "--tcservice=service2" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command reload-application executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs reloading application
     * 
     * @throws IOException
     */
    @Test
    public void testReloadApplicationSpecificFailure() throws IOException {
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
        statusResponse.setResourceName("server1");
        statusResponse.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(applicationManager.reloadApplication(server, service, host, application)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = reloadApplicationCommand.execute(new String[] { "--servername=server1", "--application=swf-booking-mvc" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to reload application on server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(ReloadApplicationCommand.OPT_SERVER_NAME, ReloadApplicationCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_SERVER_ID, ReloadApplicationCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_GROUP_NAME, ReloadApplicationCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_GROUP_ID, ReloadApplicationCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_SERVICE, ReloadApplicationCommand.OPT_SERVICE_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_HOST, ReloadApplicationCommand.OPT_HOST_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_APPLICATION, ReloadApplicationCommand.OPT_APPLICATION_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ReloadApplicationCommand.OPT_REVISION, ReloadApplicationCommand.OPT_REVISION_DESC).withRequiredArg().ofType(
                String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(reloadApplicationCommand.getName() + ": " + reloadApplicationCommand.getDescription());

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
