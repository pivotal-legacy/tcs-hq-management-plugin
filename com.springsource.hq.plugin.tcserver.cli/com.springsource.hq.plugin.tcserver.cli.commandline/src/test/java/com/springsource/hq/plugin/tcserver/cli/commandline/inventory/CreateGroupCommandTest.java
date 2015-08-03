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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.GroupsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.GroupRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.HelpMessages;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of {@link CreateGroupCommand}
 */
public class CreateGroupCommandTest {

    private CreateGroupCommand createGroupCommand;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private GroupRepository groupRepository;

    private OptionParser optionParser;

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
        this.groupRepository = EasyMock.createMock(GroupRepository.class);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.outWriter = new PrintWriter(outputStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.createGroupCommand = new CreateGroupCommand(groupRepository, optionParser, outWriter, errorWriter);
        this.createGroupCommand.afterPropertiesSet();
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
     * Verifies the createGroup command creates a group
     * 
     * @throws IOException
     */
    @Test
    public void testCreateGroup() throws IOException {
        GroupsResponse response = new GroupsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Group group = new Group();
        group.setDescription("A description");
        group.setName("Group1");
        group.setLocation("Melbourne");
        EasyMock.expect(groupRepository.createGroup(group, "7.0")).andReturn(response);
        EasyMock.replay(groupRepository);
        int exitCode = createGroupCommand.execute(new String[] { "--name=Group1", "--description=A description", "--location=Melbourne",
            "--version=7.0" });
        EasyMock.verify(groupRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("Command create-group executed successfully", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that a failure to execute createGroup command is reported correctly
     * 
     * @throws IOException
     */
    @Test
    public void testCreateGroupFailure() throws IOException {
        final Group group = new Group();
        group.setName("Group1");
        GroupsResponse response = new GroupsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something went wrong");
        response.setError(error);
        EasyMock.expect(groupRepository.createGroup(group, null)).andReturn(response);
        EasyMock.replay(groupRepository);
        int exitCode = createGroupCommand.execute(new String[] { "--name=Group1" });
        EasyMock.verify(groupRepository);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command create-group.  Reason: Something went wrong", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies the help command output is correct
     * 
     * @throws IOException
     */
    @Test
    public void testCreateGroupHelp() throws IOException {
        final int exitCode = createGroupCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(0, exitCode);
        assertEquals(//
            "create-group: " + createGroupCommand.getDescription() + newline //
                + HelpMessages.HEADER + newline //
                + "--description                           Description of the group.  Optional.   " + newline //
                + HelpMessages.HELP + newline //
                + HelpMessages.HOST + newline //
                + "--location                              Location of the group.  Optional.      " + newline //
                + "--name                                  Name of the group to create.           " + newline //
                + HelpMessages.PASSWORD + newline //
                + HelpMessages.PORT + newline //
                + HelpMessages.SECURE + newline //
                + HelpMessages.USER + "                 " + newline //
                + "--version                               Version of the group. Optional.        " + newline //
                + "                                          Defaults to 6.0", output);
    }

    /**
     * Verifies an error is printed when name argument is missing
     * 
     * @throws IOException
     */
    @Test
    public void testCreateGroupMissingName() throws IOException {
        final int exitCode = createGroupCommand.execute(new String[0]);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(1, exitCode);
        assertEquals("Missing required argument --name", output);
    }

}
