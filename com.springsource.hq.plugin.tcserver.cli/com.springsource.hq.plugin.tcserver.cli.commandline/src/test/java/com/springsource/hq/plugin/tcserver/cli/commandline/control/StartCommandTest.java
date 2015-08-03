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

package com.springsource.hq.plugin.tcserver.cli.commandline.control;

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

import com.springsource.hq.plugin.tcserver.cli.client.control.ControlOperationInvoker;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ControlStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of the {@link StartCommand}
 */
public class StartCommandTest {

    private ControlOperationInvoker controlOperationInvoker;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private StartCommand startCommand;

    private ControlStatusResponse getExpectedResponse() {
        final ControlStatusResponse response = new ControlStatusResponse();
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
        this.controlOperationInvoker = EasyMock.createMock(ControlOperationInvoker.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.startCommand = new StartCommand(controlOperationInvoker, optionParser, outWriter, errorWriter);
        this.startCommand.afterPropertiesSet();
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
    public void testStartBothServerAndGroupParams() throws IOException {
        int exitCode = startCommand.execute(new String[] { "--groupname=Group1", "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testStartWithServerNameAndServerId() throws IOException {
        int exitCode = startCommand.execute(new String[] { "--servername=test-server", "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies successful start of a group
     * 
     * @throws IOException
     */
    @Test
    public void testStartGroup() throws IOException {
        Group group = new Group();
        group.setId(123);
        EasyMock.expect(controlOperationInvoker.start(group)).andReturn(getExpectedResponse());
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--groupid=123" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs starting group
     * 
     * @throws IOException
     */
    @Test
    public void testStartGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");
        final ControlStatusResponse response = new ControlStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(controlOperationInvoker.start(group)).andReturn(response);
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--groupname=group1" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command start.  Reason: Something bad happened", output);
    }

    /**
     * Verifies correct output if failure occurs starting group
     * 
     * @throws IOException
     */
    @Test
    public void testStartGroupSpecificFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");
        final ControlStatusResponse response = new ControlStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        EasyMock.expect(controlOperationInvoker.start(group)).andReturn(response);
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--groupname=group1" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to start server1.  Reason: Something bad happened", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testStartHelpOption() throws IOException {
        int exitCode = startCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w/out server or group params
     * 
     * @throws IOException
     */
    @Test
    public void testStartNoServerOrGroupParams() throws IOException {
        int exitCode = startCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies successful start of a single server
     * 
     * @throws IOException
     */
    @Test
    public void testStartServer() throws IOException {
        Resource server = new Resource();
        server.setId(123);
        EasyMock.expect(controlOperationInvoker.start(server)).andReturn(getExpectedResponse());
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--serverid=123" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command start executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs starting server
     * 
     * @throws IOException
     */
    @Test
    public void testStartServerGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        final ControlStatusResponse response = new ControlStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(controlOperationInvoker.start(server)).andReturn(response);
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--servername=server1" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command start.  Reason: Something bad happened", output);
    }

    /**
     * Verifies correct output if failure occurs starting server
     * 
     * @throws IOException
     */
    @Test
    public void testStartServerSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        final ControlStatusResponse response = new ControlStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(controlOperationInvoker.start(server)).andReturn(response);
        EasyMock.replay(controlOperationInvoker);
        int exitCode = startCommand.execute(new String[] { "--servername=server1" });
        EasyMock.verify(controlOperationInvoker);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to start server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(StartCommand.OPT_SERVER_NAME, StartCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(StartCommand.OPT_SERVER_ID, StartCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
            testOptionParser.accepts(StartCommand.OPT_GROUP_NAME, StartCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(StartCommand.OPT_GROUP_ID, StartCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(Integer.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(startCommand.getName() + ": " + startCommand.getDescription());

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
