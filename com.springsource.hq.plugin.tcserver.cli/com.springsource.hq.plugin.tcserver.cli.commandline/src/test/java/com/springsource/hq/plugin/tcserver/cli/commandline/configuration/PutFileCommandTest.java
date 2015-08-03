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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
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
 * Unit test of the {@link PutFileCommand}
 * 
 */
public class PutFileCommandTest {

    private ConfigurationRepository configurationRepository;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private OptionParser testOptionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private PutFileCommand putFileCommand;

    private String separator = File.separator;

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
        this.putFileCommand = new PutFileCommand(configurationRepository, optionParser, outWriter, errorWriter);
        this.putFileCommand.afterPropertiesSet();
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
     * Verifies successful execution of put-file
     * 
     * @throws IOException
     */
    @Test
    public void testPutFile() throws IOException {
        Resource server = new Resource();
        server.setId(1234);

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.SUCCESS);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";

        EasyMock.expect(configurationRepository.putFile(server, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String serverId = "--serverid=1234";
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFileConfServerXml = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { serverId, fileTmpMyServerXml, targetFileConfServerXml });

        EasyMock.verify(configurationRepository);

        assertEquals(0, exitCode);

        String output = outputStringWriter.getBuffer().toString().trim();

        assertEquals("Command put-file executed successfully", output);
    }

    /**
     * Verifies an error is printed when both server and group params are used
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileBothServerAndGroupParams() throws IOException {
        String groupName = "--groupname=Group1";
        String serverId = "--serverid=123";
        String fileTmpServerXml = "--file=" + separator + "tmp" + separator + "server.xml";
        String targetFileConfServerXml = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { groupName, serverId, fileTmpServerXml, targetFileConfServerXml });
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only one of either server or group identifiers should be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs sending file
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileGeneralFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";

        EasyMock.expect(configurationRepository.putFile(server, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String serverName = "--servername=server1";
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFile = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { serverName, fileTmpMyServerXml, targetFile });

        EasyMock.verify(configurationRepository);

        assertEquals(1, exitCode);

        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command put-file.  Reason: Something bad happened", output);
    }

    /**
     * Verifies successful execution of put-file
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileGroup() throws IOException {
        Group group = new Group();
        group.setId(1234);

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.SUCCESS);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";
        EasyMock.expect(configurationRepository.putFile(group, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String groupId = "--groupid=1234";
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFile = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { groupId, fileTmpMyServerXml, targetFile });
        EasyMock.verify(configurationRepository);
        assertEquals(0, exitCode);
        String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command put-file executed successfully", output);
    }

    /**
     * Verifies correct output if failure occurs sending file
     * 
     * @throws IOException
     */
    public void testPutFileGroupGeneralFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        response.setError(error);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";

        EasyMock.expect(configurationRepository.putFile(group, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String groupName = "--groupname=group1";
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFile = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { groupName, fileTmpMyServerXml, targetFile });
        EasyMock.verify(configurationRepository);

        assertEquals(1, exitCode);

        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command put-file.  Reason: Something bad happened", output);
    }

    /**
     * Verifies correct output if failure occurs sending file
     * 
     * @throws IOException
     */
    public void testPutFileGroupSpecificFailure() throws IOException {
        Group group = new Group();
        group.setName("group1");

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";

        EasyMock.expect(configurationRepository.putFile(group, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String groupName = "--groupname=group1";
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFile = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { groupName, fileTmpMyServerXml, targetFile });
        EasyMock.verify(configurationRepository);

        assertEquals(1, exitCode);

        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to put file on server1.  Reason: Something bad happened", output);
    }

    /**
     * Verifies the output of the command when using the help option
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileHelpOption() throws IOException {
        int exitCode = putFileCommand.execute(new String[] { "--help" });
        String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that an error is printed if called with missing file argument
     * 
     * @throws IOException
     */
    public void testPutFileNoFile() throws IOException {

        String serverId = "--serverid=1234";
        String targetFile = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { serverId, targetFile });
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --file", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that a usage stmt is printed when the command is invoked with no options
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileNoParams() throws IOException {
        int exitCode = putFileCommand.execute(new String[0]);
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(getHelpOutput(), output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an error is printed when the command is called w/out server or group params
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileNoServerOrGroupParams() throws IOException {
        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFile = "--targetfile=conf" + separator + "server.xml";
        int exitCode = putFileCommand.execute(new String[] { fileTmpMyServerXml, targetFile });
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Either the name or ID of a server or group must be specified", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies that an error is printed if called with missing targetfile argument
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileNoTarget() throws IOException {
        String fileConfServerXml = "--file=conf/server.xml";
        int exitCode = putFileCommand.execute(new String[] { "--serverid=1234", fileConfServerXml });
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Missing required argument --targetfile", output);
        assertEquals(1, exitCode);
    }

    @Test
    public void testPutFileWithServerNameAndServerId() throws IOException {
        int exitCode = putFileCommand.execute(new String[] { "--servername=test-server", "--serverid=1234", "--file=conf/server.xml",
            "--targetfile=foobar.txt" });
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Only serverid or servername can be specified, not both.", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies correct output if failure occurs sending file
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileSpecificFailure() throws IOException {
        Resource server = new Resource();
        server.setName("server1");

        ConfigurationStatusResponse response = new ConfigurationStatusResponse();
        response.setStatus(ResponseStatus.FAILURE);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setResourceName("server1");
        ServiceError error = new ServiceError();
        error.setReasonText("Something bad happened");
        statusResponse.setError(error);
        statusResponse.setStatus(ResponseStatus.FAILURE);
        response.getStatusResponse().add(statusResponse);

        String tmpMyServerXml = separator + "tmp" + separator + "my-server.xml";
        String confServerXml = "conf" + separator + "server.xml";

        EasyMock.expect(configurationRepository.putFile(server, new File(tmpMyServerXml), confServerXml, false)).andReturn(response);
        EasyMock.replay(configurationRepository);

        String fileTmpMyServerXml = "--file=" + separator + "tmp" + separator + "my-server.xml";
        String targetFileConfServerXml = "--targetfile=conf" + separator + "server.xml";

        int exitCode = putFileCommand.execute(new String[] { "--servername=server1", fileTmpMyServerXml, targetFileConfServerXml });
        EasyMock.verify(configurationRepository);

        assertEquals(1, exitCode);
        String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to put file on server1.  Reason: Something bad happened", output);
    }

    private String getHelpOutput() {
        String output = null;

        try {
            testOptionParser = (OptionParser) new OptionParserFactory().getObject();
            testOptionParser.accepts(PutFileCommand.OPT_SERVER_NAME, PutFileCommand.OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(PutFileCommand.OPT_SERVER_ID, PutFileCommand.OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
            testOptionParser.accepts(PutFileCommand.OPT_GROUP_NAME, PutFileCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(PutFileCommand.OPT_GROUP_ID, PutFileCommand.OPT_GROUP_ID_DESC).withRequiredArg().ofType(Integer.class);
            testOptionParser.accepts(PutFileCommand.OPT_FILE, PutFileCommand.OPT_FILE_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(PutFileCommand.OPT_TARGET_FILE, PutFileCommand.OPT_TARGET_FILE_DESC).withRequiredArg().ofType(String.class);
            testOptionParser.accepts(PutFileCommand.OPT_NO_BACKUP_FILE, PutFileCommand.OPT_NO_BACKUP_FILE_DESC).withOptionalArg();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(putFileCommand.getName() + ": " + putFileCommand.getDescription());

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
