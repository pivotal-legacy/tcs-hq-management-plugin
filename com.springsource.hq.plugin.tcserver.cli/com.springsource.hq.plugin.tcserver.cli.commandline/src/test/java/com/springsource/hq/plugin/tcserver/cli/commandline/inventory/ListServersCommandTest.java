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
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.ResourceRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.HelpMessages;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Unit test of {@link ListServersCommand}
 */
public class ListServersCommandTest {

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private ListServersCommand listServersCommand;

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
        this.listServersCommand = new ListServersCommand(resourceRepository, optionParser, outWriter, errorWriter);
        this.listServersCommand.afterPropertiesSet();
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
     * Verifies the listServers command with no options lists all tc Servers
     * 
     * @throws IOException
     */
    @Test
    public void testListServers() throws IOException {
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Resource resource = new Resource();
        resource.setDescription("A description");
        resource.setId(1234);
        resource.setName("Server1");
        resource.setStatus("Stopped");
        response.getResource().add(resource);
        EasyMock.expect(resourceRepository.getServers()).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = listServersCommand.execute(new String[0]);
        EasyMock.verify(resourceRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("1234|Server1|A description|Stopped", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies an error is printed if both platformname and groupname are specified
     * 
     * @throws IOException
     */
    @Test
    public void testListServersBothPlatformAndGroup() throws IOException {
        final int exitCode = listServersCommand.execute(new String[] { "--platformname=mymachine", "--groupname=group1" });
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(1, exitCode);
        assertEquals("Only one of either groupname or platformname filters should be specified", output);
    }

    /**
     * Verifies the listServers command with groupname option lists all servers by group
     * 
     * @throws IOException
     */
    @Test
    public void testListServersByGroup() throws IOException {
        final Group group = new Group();
        group.setName("group1");
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Resource resource = new Resource();
        resource.setDescription("A description");
        resource.setId(1234);
        resource.setName("Server1");
        resource.setStatus("Running");
        response.getResource().add(resource);
        EasyMock.expect(resourceRepository.getServersByGroup(group)).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = listServersCommand.execute(new String[] { "--groupname=group1" });
        EasyMock.verify(resourceRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("1234|Server1|A description|Running", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies the listServers command with platformname option lists all servers by platform
     * 
     * @throws IOException
     */
    @Test
    public void testListServersByPlatform() throws IOException {
        final Resource platform = new Resource();
        platform.setName("mymachine");
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        final Resource resource = new Resource();
        resource.setDescription("A description");
        resource.setId(1234);
        resource.setName("Server1");
        resource.setStatus("Unknown");
        response.getResource().add(resource);
        EasyMock.expect(resourceRepository.getServersByPlatform(platform)).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = listServersCommand.execute(new String[] { "--platformname=mymachine" });
        EasyMock.verify(resourceRepository);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals("1234|Server1|A description|Unknown", output);
        assertEquals(0, exitCode);
    }

    /**
     * Verifies that a failure to execute listServers command is reported correctly
     * 
     * @throws IOException
     */
    @Test
    public void testListServersFailure() throws IOException {
        ResourcesResponse response = new ResourcesResponse();
        response.setStatus(ResponseStatus.FAILURE);
        ServiceError error = new ServiceError();
        error.setReasonText("Something went wrong");
        response.setError(error);
        EasyMock.expect(resourceRepository.getServers()).andReturn(response);
        EasyMock.replay(resourceRepository);
        int exitCode = listServersCommand.execute(new String[0]);
        EasyMock.verify(resourceRepository);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command list-servers.  Reason: Something went wrong", output);
        assertEquals(1, exitCode);
    }

    /**
     * Verifies the help command output is correct
     * 
     * @throws IOException
     */
    @Test
    public void testListServersHelp() throws IOException {
        final int exitCode = listServersCommand.execute(new String[] { "--help" });
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(0, exitCode);
        assertEquals("list-servers: " + listServersCommand.getDescription() + newline + HelpMessages.HEADER + newline
            + "--groupname                             Name of a Compatible Group/Cluster of  " + newline
            + "                                          tc Servers to query for tc Servers.  " + newline
            + "                                          Defaults to all groups.              " + newline + HelpMessages.HELP + newline
            + HelpMessages.HOST + newline + HelpMessages.PASSWORD + newline
            + "--platformname                          Name of a platform to query for tc     " + newline
            + "                                          Servers.  Defaults to all platforms. " + newline + HelpMessages.PORT + newline
            + HelpMessages.SECURE + newline + HelpMessages.USER, output);
    }

}
