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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
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

public class SetJvmOptionsCommandTest {

    private ConfigurationRepository configurationRepository;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private SetJvmOptionsCommand setJvmOptionsCommand;

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
        this.setJvmOptionsCommand = new SetJvmOptionsCommand(configurationRepository, optionParser, outWriter, errorWriter);
        this.setJvmOptionsCommand.afterPropertiesSet();
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
     * Verifies successful execution of set-jvm-options
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptions() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-Dprop=value1, value2", "-Xmx512m,foo", "-Xms128m" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=-Dprop=value1\\, value2,-Xmx512m\\,foo,   -Xms128m" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies an error is printed when both server and group params are used
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsBothServerAndGroupParams() throws IOException {
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--groupname=Group1", "--serverid=123", "--options=\"\"" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies successful parsing when options end with a delimiter
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsEndsWithComma() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-server", "-Xmx512m", "-Xms128m" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=-server,,,,-Xmx512m,-Xms128m,,," });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs setting JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-server" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--servername=server1", "--options=,-server," });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command set-jvm-options.  Reason: Something bad happened", output);
    }

    /**
     * Verifies successful execution of set-jvm-options
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsGroup() throws IOException {
        Group group = new Group();
        group.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-Dprop=value1, value2", "-Xmx512m,foo", "-Xms128m" }));
        EasyMock.expect(configurationRepository.setJvmOptions(group, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--groupid=1234", "--options=-Dprop=value1\\, value2,-Xmx512m\\,foo,   -Xms128m" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs setting JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-server" }));
        EasyMock.expect(configurationRepository.setJvmOptions(group, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--groupname=group1", "--options=,-server," });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command set-jvm-options.  Reason: Something bad happened", output);
    }

    /**
     * Verifies correct output if failure occurs setting JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsGroupSpecificFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        final StatusResponse statusResponse2 = new StatusResponse();
        statusResponse2.setResourceName("server2");
        statusResponse2.setStatus(ResponseStatus.FAILURE);
        ServiceError error2 = new ServiceError();
        error2.setReasonText("Someone screwed up");
        statusResponse2.setError(error2);
        response.getStatusResponse().add(statusResponse2);

        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-server" }));
        EasyMock.expect(configurationRepository.setJvmOptions(group, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--groupname=group1", "--options=,-server," });
        EasyMock.verify(configurationRepository);
        assertEquals(2, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to set JVM options on server1.  Reason: Something bad happened" + newline
            + "Failed to set JVM options on server2.  Reason: Someone screwed up", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsHelpOption() throws IOException {
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();

        /*
         * File origFile = new File(System.getProperty("java.io.tmpdir") + "/originalHelp.txt");
         * System.out.println(origFile.getAbsolutePath()); Writer origWriter = new FileWriter(origFile); BufferedWriter
         * origBuffer = new BufferedWriter(origWriter); origBuffer.write(output); origBuffer.flush();
         * 
         * File testFile = new File(System.getProperty("java.io.tmpdir") + "/testHelp.txt");
         * System.out.println(testFile.getAbsolutePath()); Writer testWriter = new FileWriter(testFile); BufferedWriter
         * testBuffer = new BufferedWriter(testWriter); testBuffer.write(getHelpOutput()); testBuffer.flush();
         * 
         * assertTrue(FileUtils.contentEquals(origFile, testFile));
         */

        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies an error is printed when command is called without JVM options
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsMissingOptions() throws IOException {
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --options", output);
        assertEquals(1, exitCode);
    }

    /**
     * Tests a combination of allowable commas as delimiters
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsNoBackslashes() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-Dprop=value1", "value2", "-Xmx512m", "foo", "-Xms128m" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=   -Dprop=value1, value2,-Xmx512m  ,foo,   -Xms128m" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies an error is printed when command is called with no params
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsNoParams() throws Exception {
        int exitCode = setJvmOptionsCommand.execute(new String[0]);
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
    public void testSetJvmOptionsNoServerOrGroupParams() throws IOException {
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--options=-server" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testSetJvmOptionsWithServerNameAndServerId() throws IOException {
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--servername=test-server", "--serverid=123", "--options=-server" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs setting JVM opts
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-server" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--servername=server1", "--options=,-server," });
        EasyMock.verify(configurationRepository);
        assertEquals(1, exitCode);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to set JVM options on server1.  Reason: Something bad happened", output);
    }

    /**
     * Verifies successful parsing when options start with a delimiter
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsStartsWithComma() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        jvmOptions.getOption().addAll(Arrays.asList(new String[] { "-Dprop=value1", "-Xmx512m", ", -Xms128m" }));
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=,   -Dprop=value1,-Xmx512m  ,  \\, -Xms128m" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies successful set of JVM options to empty string
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsToNothing() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=\"\"" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    /**
     * Verifies successful set of JVM options if value is left off (i.e. --options=)
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsToNothingNotSpecified() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        final JvmOptionsResponse response = new JvmOptionsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions jvmOptions = new JvmOptions();
        EasyMock.expect(configurationRepository.setJvmOptions(server, jvmOptions)).andReturn(response);
        EasyMock.replay(configurationRepository);
        int exitCode = setJvmOptionsCommand.execute(new String[] { "--serverid=1234", "--options=" });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command set-jvm-options executed successfully", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(SetJvmOptionsCommand.OPT_SERVER_NAME, SetJvmOptionsCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(SetJvmOptionsCommand.OPT_SERVER_ID, SetJvmOptionsCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(SetJvmOptionsCommand.OPT_GROUP_NAME, SetJvmOptionsCommand.OPT_GROUP_NAME_DESC).withRequiredArg().ofType(
                String.class);
            testOptionParser.accepts(SetJvmOptionsCommand.OPT_GROUP_ID, SetJvmOptionsCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(
                Integer.class);
            testOptionParser.accepts(SetJvmOptionsCommand.OPT_OPTIONS, SetJvmOptionsCommand.OPT_OPTIONS_DESC).withOptionalArg().ofType(String.class);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(setJvmOptionsCommand.getName() + ": " + setJvmOptionsCommand.getDescription());

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
