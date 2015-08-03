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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import joptsimple.OptionParser;

import org.easymock.EasyMock;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.configuration.ConfigurationRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptions;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of the {@link ListJvmOptionsCommand}
 * 
 */
public class ListJvmOptionsCommandTest {

    private ConfigurationRepository configurationRepository;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private ListJvmOptionsCommand listJvmOptionsCommand;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private String newline = System.getProperty("line.separator");

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
        this.listJvmOptionsCommand = new ListJvmOptionsCommand(configurationRepository, optionParser, outWriter, errorWriter);
        this.listJvmOptionsCommand.afterPropertiesSet();
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
     * Verifies successful execution of list-jvm-options
     * 
     * @throws IOException
     */
    @Test
    public void testListJvmOptions() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-Xmx512m", "-Xss192k", "-Xms128m", "-server" }));
        response.setJvmOptions(jvmOptions);
        EasyMock.expect(configurationRepository.getJvmOptions(server)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = listJvmOptionsCommand.execute(new String[] { "--serverid=1234" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("-Xmx512m" + newline + "-Xss192k" + newline + "-Xms128m" + newline + "-server", output);
    }

    /**
     * Verifies correct output if failure occurs listing JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testListJvmOptionsGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");
        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        EasyMock.expect(configurationRepository.getJvmOptions(server)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = listJvmOptionsCommand.execute(new String[] { "--servername=server1" });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command list-jvm-options.  Reason: Something bad happened", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testListJvmOptionsHelpOption() throws IOException {
        int exitCode = listJvmOptionsCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w/out server params
     * 
     * @throws IOException
     */
    @Test
    public void testListJvmOptionsNoServerParams() throws IOException {
        int exitCode = listJvmOptionsCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server must be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testListJvmOptionsWithServerNameAndServerId() throws IOException {
        int exitCode = listJvmOptionsCommand.execute(new String[] { "--servername=test-server", "--serverid=123" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs listing JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testListJvmOptionsSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        response.getStatusResponse().add(statusResponse);
        EasyMock.expect(configurationRepository.getJvmOptions(server)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = listJvmOptionsCommand.execute(new String[] { "--servername=server1" });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to retrieve JVM options from server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(ListJvmOptionsCommand.OPT_SERVER_NAME, ListJvmOptionsCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(ListJvmOptionsCommand.OPT_SERVER_ID, ListJvmOptionsCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(listJvmOptionsCommand.getName() + ": " + listJvmOptionsCommand.getDescription());

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
