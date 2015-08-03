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

package com.springsource.hq.plugin.tcserver.cli.commandline.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import joptsimple.OptionParser;

import org.easymock.EasyMock;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.configuration.ConfigurationRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ConfigurationStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of the {@link GetFileCommand}
 * 
 */
public class GetFileCommandTest {

    private ConfigurationRepository configurationRepository;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private GetFileCommand getFileCommand;

    /**
     * Sets up the tests
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.configurationRepository = EasyMock.createMock(ConfigurationRepository.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.getFileCommand = new GetFileCommand(configurationRepository, optionParser, outWriter, errorWriter);
        this.getFileCommand.afterPropertiesSet();
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
     * Verifies successful execution of get-file
     * 
     * @throws IOException
     */
    @Test
    public void testGetFile() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        EasyMock.expect(configurationRepository.getFile(server, "conf/server.xml", new File("/tmp/my-server.xml"))).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = getFileCommand.execute(new String[] { "--serverid=1234", "--targetfile=/tmp/my-server.xml", "--file=conf/server.xml" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command get-file executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs retrieving file
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        final ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(configurationRepository.getFile(server, "conf/server.xml", new File("/tmp/my-server.xml"))).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = getFileCommand.execute(new String[] { "--servername=server1", "--targetfile=/tmp/my-server.xml", "--file=conf/server.xml" });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to get file from server1.  Reason: Something bad happened", output);
    }

    /**
     * Verifies correct output if failure occurs retrieving file
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        final ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(configurationRepository.getFile(server, "conf/server.xml", new File("/tmp/my-server.xml"))).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = getFileCommand.execute(new String[] { "--servername=server1", "--targetfile=/tmp/my-server.xml", "--file=conf/server.xml" });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command get-file.  Reason: Something bad happened", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileHelpOption() throws IOException {
        int exitCode = getFileCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that an error is printed if called with missing file argument
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileNoFile() throws IOException {
        int exitCode = getFileCommand.execute(new String[] { "--serverid=1234", "--targetfile=/tmp/server.xml" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --file", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that a usage stmt is printed when the command is invoked with no options
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileNoParams() throws IOException {
        int exitCode = getFileCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w/out server params
     * 
     * @throws IOException
     */
    @Test
    public void testGetFileNoServerParams() throws IOException {
        int exitCode = getFileCommand.execute(new String[] { "--targetfile=/tmp/my-server.xml", "--file=conf/server.xml" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server must be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testGetFileWithServerNameAndServerId() throws IOException {
        int exitCode = getFileCommand.execute(new String[] { "--servername=test-server", "--serverid=123", "--targetfile=/tmp/my-server.xml",
            "--file=conf/server.xml" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an error is printed if called with missing targetfile argument
     * 
     * @throws IOException
     */
    @Test
    public void tesGetFileNoTarget() throws IOException {
        int exitCode = getFileCommand.execute(new String[] { "--serverid=1234", "--file=conf/server.xml" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --targetfile", output);
        assertEquals(1, exitCode);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(GetFileCommand.OPT_SERVER_NAME, GetFileCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(GetFileCommand.OPT_SERVER_ID, GetFileCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
            testOptionParser.accepts(GetFileCommand.OPT_FILE, GetFileCommand.OPT_FILE_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(GetFileCommand.OPT_TARGET_FILE, GetFileCommand.OPT_TARGET_FILE_DESC).withRequiredArg().ofType(String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(getFileCommand.getName() + ": " + getFileCommand.getDescription());

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
