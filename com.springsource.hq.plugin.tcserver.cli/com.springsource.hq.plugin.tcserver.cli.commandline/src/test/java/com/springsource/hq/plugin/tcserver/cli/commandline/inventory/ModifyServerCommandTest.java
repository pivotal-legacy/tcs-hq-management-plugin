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

package com.springsource.hq.plugin.tcserver.cli.commandline.inventory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import joptsimple.OptionParser;

import org.easymock.EasyMock;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.ResourceRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.HelpMessages;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;
import com.springsource.hq.plugin.tcserver.cli.commandline.inventory.ModifyServerCommand;

/**
 * Unit test of the {@link ModifyServerCommand}
 */
public class ModifyServerCommandTest {

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private ModifyServerCommand modifyServerCommand;

    private OptionParser optionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    private ResourceRepository resourceRepository;

    private String newline = System.getProperty("line.separator");

    /**
     * Sets up the tests
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.resourceRepository = EasyMock.createMock(ResourceRepository.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.modifyServerCommand = new ModifyServerCommand(resourceRepository, optionParser, outWriter, errorWriter);
        this.modifyServerCommand.afterPropertiesSet();
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
     * Verifies the help command output is correct
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServerHelp() throws IOException {
        final int exitCode = modifyServerCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(0, exitCode);
        assertEquals("modify-server: " + modifyServerCommand.getDescription() + newline + HelpMessages.HEADER + newline
            + "--description                           Description to set on specified tc     " + newline
            + "                                          Server.  Optional if --name is       " + newline
            + "                                          specified.                           " + newline + HelpMessages.HELP + newline
            + HelpMessages.HOST + newline + "--name                                  Name to set on specified tc Server.    " + newline
            + "                                          Optional if --description is         " + newline
            + "                                          specified.                           " + newline + HelpMessages.PASSWORD + newline
            + HelpMessages.PORT + newline + HelpMessages.SECURE + newline
            + "--serverid <Integer>                    Id of a single tc Server.              " + newline + HelpMessages.USER, output);
    }

    /**
     * Verifies correct execution of modifyServer
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServer() throws IOException {
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Resource resource = new Resource();
        resource.setDescription("A description");
        resource.setId(1234);
        resource.setName("Server1");
        EasyMock.expect(resourceRepository.modifyServer(resource)).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = modifyServerCommand.execute(new String[] { "--serverid=1234", "--description=A description", "--name=Server1" });
        EasyMock.verify(resourceRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command modify-server executed successfully", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that a failure to execute modifyServer command is reported correctly
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServerFailure() throws IOException {
        final Resource resource = new Resource();
        resource.setId(1234);
        resource.setName("newName");
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something went wrong");
        response.setError(error);
        EasyMock.expect(resourceRepository.modifyServer(resource)).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = modifyServerCommand.execute(new String[] { "--serverid=1234", "--name=newName" });
        EasyMock.verify(resourceRepository);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command modify-server.  Reason: Something went wrong", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies an error is printed if missing server id
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServerMissingNameAndDescription() throws IOException {
        final int exitCode = modifyServerCommand.execute(new String[] { "--serverid=1234" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(1, exitCode);
        assertEquals("Either --name or --description should be specified", output);
    }

    /**
     * Verifies an error is printed if missing server id
     * 
     * @throws IOException
     */
    @Test
    public void testModifyServerMissingServerId() throws IOException {
        final int exitCode = modifyServerCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(1, exitCode);
        assertEquals("Missing required argument --serverid", output);
    }

}
