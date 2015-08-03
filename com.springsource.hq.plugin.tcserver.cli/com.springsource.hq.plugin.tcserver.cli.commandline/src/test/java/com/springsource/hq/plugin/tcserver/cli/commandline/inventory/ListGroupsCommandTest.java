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
import com.springsource.hq.plugin.tcserver.cli.commandline.inventory.ListGroupsCommand;

/**
 * Unit test of {@link ListGroupsCommand}
 */
public class ListGroupsCommandTest {

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private GroupRepository groupRepository;

    private ListGroupsCommand listGroupsCommand;

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
        this.listGroupsCommand = new ListGroupsCommand(groupRepository, optionParser, outWriter, errorWriter);
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
     * Verifies the listGroups command lists all groups
     * 
     * @throws IOException
     */
    @Test
    public void testListGroups() throws IOException {
        GroupsResponse response = new GroupsResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Group group = new Group();
        group.setDescription("A description");
        group.setId(1234);
        group.setName("Group1");
        group.setLocation("Melbourne");
        response.getGroup().add(group);
        EasyMock.expect(groupRepository.getGroups()).andReturn(response);
        EasyMock.replay(groupRepository);
        int exitCode = listGroupsCommand.execute(new String[0]);
        EasyMock.verify(groupRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("1234|Group1|A description|Melbourne", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that a failure to execute listServers command is reported correctly
     * 
     * @throws IOException
     */
    @Test
    public void testListGroupsFailure() throws IOException {
        GroupsResponse response = new GroupsResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something went wrong");
        response.setError(error);
        EasyMock.expect(groupRepository.getGroups()).andReturn(response);
        EasyMock.replay(groupRepository);
        int exitCode = listGroupsCommand.execute(new String[0]);
        EasyMock.verify(groupRepository);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command list-groups.  Reason: Something went wrong", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies the help command output is correct
     * 
     * @throws IOException
     */
    @Test
    public void testListGroupsHelp() throws IOException {
        final int exitCode = listGroupsCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(0, exitCode);
        assertEquals("list-groups: " + listGroupsCommand.getDescription() + newline + HelpMessages.HEADER + newline + HelpMessages.HELP + newline
            + HelpMessages.HOST + newline + HelpMessages.PASSWORD + newline + HelpMessages.PORT + newline + HelpMessages.SECURE + newline
            + HelpMessages.USER, output);
    }

}
