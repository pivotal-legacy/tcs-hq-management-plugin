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

import java.io.File;
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

import com.springsource.hq.plugin.tcserver.cli.client.application.ApplicationManager;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ApplicationManagementResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Host;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Service;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of the {@link DeployApplicationCommand}
 * 
 */
public class DeployApplicationCommandTest {

    private ApplicationManager applicationManager;

    private DeployApplicationCommand deployApplicationCommand;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

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
        this.deployApplicationCommand = new DeployApplicationCommand(applicationManager, optionParser, outWriter, errorWriter);
        this.deployApplicationCommand.afterPropertiesSet();
    }

    /**
     * Tears down the tests
     */
    @After
    public void tearDown() {
        this.errorWriter.close();
        this.outWriter.close();
    }

    @Test
    public void testDeployLocalFileWithNoExtension() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--tchost=myhost", "--localpath=/tmp/foobar" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("localpath must be a file that ends with .war", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testDeployRemoteFileWithNoExtension() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--tchost=myhost", "--remotepath=/tmp/foobar" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("remotepath must be a file that ends with .war", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testDeployLocalFileWithNonWarExtension() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--tchost=myhost", "--localpath=/tmp/foobar.notwar" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("localpath must be a file that ends with .war", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testDeployRemoteFileWithNonWarExtension() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--tchost=myhost", "--remotepath=/tmp/foobar.notwar" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("remotepath must be a file that ends with .war", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies an error is printed when both server and group params are used
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationBothServerAndGroupParams() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupname=Group1", "--serverid=123", "--localpath=/tmp/swf-booking-mvc.war" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testDeployApplicationServerIdAndServerName() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--servername=test-server", "--serverid=123",
            "--localpath=/tmp/swf-booking-mvc.war" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an application is deployed correctly when a group and host are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupHostRemotePath() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        EasyMock.expect(applicationManager.deployApplication(group, "/pathtoremotefile/file.war", service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--remotepath=/pathtoremotefile/file.war", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed when just the group and localpath are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupLocalPath() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(group, new File("/tmp/swf-booking-mvc.war"), service, host)).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--localpath=/tmp/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed when the group, remotepath and contextpath are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupRemotePathContextPath() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(group, "/tmp/swf-booking-mvc.war", service, host, "foo")).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--remotepath=/tmp/swf-booking-mvc.war", "--contextpath=foo" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed correctly when a specific group, service and context path are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationGroupServiceContextPath() throws IOException {
        Group group = new Group();
        group.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(group, new File("/tmp/file.war"), service, host, "swf-booking-mvc")).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupid=123", "--contextpath=swf-booking-mvc", "--tcservice=service2",
            "--localpath=/tmp/file.war" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationHelpOption() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w both localpath and remotepath params
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationLocalAndRemotePath() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--remotepath=swf-booking-mvc.war", "--localpath=/tmp/file.war" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either localpath or remotepath should be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies an error is printed when the --application option is missing
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationMissingPath() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either remotepath or localpath must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that a usage stmt is printed when the command is invoked with no options
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationNoParams() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[0]);
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
    public void testDeployApplicationNoServerOrGroupParams() throws IOException {
        int exitCode = deployApplicationCommand.execute(new String[] { "--remotepath=swf-booking-mvc.war" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an application is deployed correctly when a specific host is specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerHostRemotePath() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("myhost");
        EasyMock.expect(applicationManager.deployApplication(server, "/pathtoremotefile/file.war", service, host)).andReturn(getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=123", "--remotepath=/pathtoremotefile/file.war", "--tchost=myhost" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed when just the server and localpath are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerLocalPath() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(server, new File("/tmp/swf-booking-mvc.war"), service, host)).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=123", "--localpath=/tmp/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed when the server, remotepath and contextpath are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerRemotePathContextPath() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(server, "/tmp/swf-booking-mvc.war", service, host, "foo")).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=123", "--remotepath=/tmp/swf-booking-mvc.war", "--contextpath=foo" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies that an application is deployed correctly when a specific service and context path are specified
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationServerServiceContextPath() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        Service service = new Service();
        service.setName("service2");
        Host host = new Host();
        host.setName("localhost");
        EasyMock.expect(applicationManager.deployApplication(server, new File("/tmp/file.war"), service, host, "swf-booking-mvc")).andReturn(
            getExpectedResponse());
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=123", "--contextpath=swf-booking-mvc", "--tcservice=service2",
            "--localpath=/tmp/file.war" });
        EasyMock.verify(applicationManager);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command deploy-application executed successfully", output);
    }

    /**
     * Verifies correct output if general failure occurs deploying an application to a specific group
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationToGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("Group1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final ServiceError error = new ServiceError();
        error.setReasonText("Authorization failure");
        response.setError(error);

        EasyMock.expect(applicationManager.deployApplication(group, "/mount/apps/swf-booking-mvc.war", service, host)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupname=Group1", "--remotepath=/mount/apps/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command deploy-application.  Reason: Authorization failure", output);
    }

    /**
     * Verifies correct output if failures occur deploying an application to a specific group
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationToGroupSpecificFailures() throws IOException {
        Group group = new Group();
        group.setName("Group1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(ResponseStatus.FAILURE);
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);

        StatusResponse statusResponse2 = new StatusResponse();
        statusResponse2.setResourceName("server2");
        statusResponse2.setStatus(ResponseStatus.FAILURE);
        ServiceError error2 = new ServiceError();
        error2.setReasonText("Another bad thing happened");
        statusResponse2.setError(error2);
        response.getStatusResponse().add(statusResponse2);

        EasyMock.expect(applicationManager.deployApplication(group, "/mount/apps/swf-booking-mvc.war", service, host)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--groupname=Group1", "--remotepath=/mount/apps/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(2, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to deploy application to server1.  Reason: Something bad happened" + newline
            + "Failed to deploy application to server2.  Reason: Another bad thing happened", output);
    }

    /**
     * Verifies correct output if general failure occurs deploying an application to a specific server
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationToServerGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final ServiceError error = new ServiceError();
        error.setReasonText("Authorization failure");
        response.setError(error);
        EasyMock.expect(applicationManager.deployApplication(server, "/mount/apps/swf-booking-mvc.war", service, host)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--servername=server1", "--remotepath=/mount/apps/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command deploy-application.  Reason: Authorization failure", output);
    }

    /**
     * Verifies correct output if specific failure occurs deploying an application to a server
     * 
     * @throws IOException
     */
    @Test
    public void testDeployApplicationToServerSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        Service service = new Service();
        service.setName("Catalina");
        Host host = new Host();
        host.setName("localhost");
        final ApplicationManagementResponse response = new ApplicationManagementResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(ResponseStatus.FAILURE);
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);

        EasyMock.expect(applicationManager.deployApplication(server, "/mount/apps/swf-booking-mvc.war", service, host)).andReturn(response);
        EasyMock.replay(applicationManager);
        int exitCode = deployApplicationCommand.execute(new String[] { "--serverid=1234", "--remotepath=/mount/apps/swf-booking-mvc.war" });
        EasyMock.verify(applicationManager);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to deploy application to server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(DeployApplicationCommand.OPT_SERVER_NAME, DeployApplicationCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_SERVER_ID, DeployApplicationCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_GROUP_NAME, DeployApplicationCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_GROUP_ID, DeployApplicationCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_SERVICE, DeployApplicationCommand.OPT_SERVICE_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_HOST, DeployApplicationCommand.OPT_HOST_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_CONTEXT_PATH, DeployApplicationCommand.OPT_CONTEXT_PATH_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_LOCAL_PATH, DeployApplicationCommand.OPT_LOCAL_PATH_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(DeployApplicationCommand.OPT_REMOTE_PATH, DeployApplicationCommand.OPT_REMOTE_PATH_DESC).withRequiredArg().ofType(
                String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(deployApplicationCommand.getName() + ": " + deployApplicationCommand.getDescription());

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
