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

import java.io.IOException;
import java.io.PrintWriter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.ResourceRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for listing tc Servers
 */
public class ListServersCommand implements Command, InitializingBean {

    private static final String COMMAND_NAME = "list-servers";

    private static final String OPT_GROUP_NAME = "groupname";

    private static final String OPT_PLATFORM_NAME = "platformname";

    private final PrintWriter errorWriter;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    private final ResourceRepository resourceRepository;

    /**
     * 
     * @param resourceRepository The {@link ResourceRepository} to use for retrieving servers
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public ListServersCommand(ResourceRepository resourceRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
        this.resourceRepository = resourceRepository;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_PLATFORM_NAME, "Name of a platform to query for tc Servers.  Defaults to all platforms.").withRequiredArg().ofType(
            String.class);
        optionParser.accepts(OPT_GROUP_NAME, "Name of a Compatible Group/Cluster of tc Servers to query for tc Servers.  Defaults to all groups.").withRequiredArg().ofType(
            String.class);
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        if (options.has(OPT_GROUP_NAME) && options.has(OPT_PLATFORM_NAME)) {
            errorWriter.println("Only one of either groupname or platformname filters should be specified");
            return 1;
        }
        if (options.has(OPT_GROUP_NAME)) {
            final Group group = new Group();
            group.setName(options.valueOf(OPT_GROUP_NAME).toString());
            return handleResponse(resourceRepository.getServersByGroup(group));
        }
        if (options.has(OPT_PLATFORM_NAME)) {
            final Resource platform = new Resource();
            platform.setName(options.valueOf(OPT_PLATFORM_NAME).toString());
            return handleResponse(resourceRepository.getServersByPlatform(platform));
        }
        return handleResponse(resourceRepository.getServers());
    }

    private int handleResponse(final ResourcesResponse response) {
        if (response.getStatus() == ResponseStatus.FAILURE) {
            errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + response.getError().getReasonText());
            return 1;
        }
        for (Resource resource : response.getResource()) {
            final StringBuilder serverListing = new StringBuilder().append(resource.getId()).append(OUTPUT_DELIMITER).append(resource.getName()).append(
                OUTPUT_DELIMITER).append(resource.getDescription()).append(OUTPUT_DELIMITER).append(resource.getStatus());
            outWriter.println(serverListing.toString());
        }
        return 0;
    }

    public String getDescription() {
        return "Retrieves a list of SpringSource tc Servers";
    }

    public String getName() {
        return COMMAND_NAME;
    }

    private void printHelp() throws IOException {
        // TODO more documentation here?
        printUsage(outWriter);
    }

    private void printUsage(PrintWriter writer) throws IOException {
        writer.println(COMMAND_NAME + ": " + getDescription());
        optionParser.printHelpOn(writer);
    }

}
