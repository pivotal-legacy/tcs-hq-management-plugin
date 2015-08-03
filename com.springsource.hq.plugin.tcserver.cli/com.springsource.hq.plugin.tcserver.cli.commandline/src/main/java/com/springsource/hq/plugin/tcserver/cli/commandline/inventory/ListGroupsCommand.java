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
import com.springsource.hq.plugin.tcserver.cli.client.schema.GroupsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.GroupRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for listing groups
 */
public class ListGroupsCommand implements Command {

    private final GroupRepository groupRepository;

    private final OptionParser optionParser;

    private static final String COMMAND_NAME = "list-groups";

    private final PrintWriter errorWriter;

    private final PrintWriter outWriter;

    /**
     * 
     * @param groupRepository The {@link GroupRepository} to use for lookup of groups
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public ListGroupsCommand(GroupRepository groupRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.groupRepository = groupRepository;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
        this.errorWriter = errorWriter;
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        final GroupsResponse groupResponse = groupRepository.getGroups();
        if (groupResponse.getStatus() == ResponseStatus.FAILURE) {
            errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + groupResponse.getError().getReasonText());
            return 1;
        }
        for (Group group : groupResponse.getGroup()) {
            final StringBuilder groupListing = new StringBuilder().append(group.getId()).append("|").append(group.getName()).append("|").append(
                group.getDescription()).append("|").append(group.getLocation());
            outWriter.println(groupListing.toString());
        }
        return 0;
    }

    public String getDescription() {
        return "Retrieves a list of Compatible Group/Clusters composed of SpringSource tc Servers";
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
