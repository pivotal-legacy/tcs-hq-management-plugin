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
import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.GroupRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for creating groups
 */
public class CreateGroupCommand implements Command, InitializingBean {

    private static final String COMMAND_NAME = "create-group";

    private static final String OPT_DESCRIPTION = "description";

    private static final String OPT_LOCATION = "location";

    private static final String OPT_NAME = "name";

    private static final String OPT_VERSION = "version";

    private final PrintWriter errorWriter;

    private final GroupRepository groupRepository;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    /**
     * 
     * @param groupRepository The {@link GroupRepository} to use for creating groups
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public CreateGroupCommand(GroupRepository groupRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.groupRepository = groupRepository;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
        this.errorWriter = errorWriter;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_NAME, "Name of the group to create.").withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_DESCRIPTION, "Description of the group.  Optional.").withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_LOCATION, "Location of the group.  Optional.").withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_VERSION, "Version of the group. Optional. Defaults to 6.0").withRequiredArg().ofType(String.class);
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        if (!options.has(OPT_NAME)) {
            errorWriter.println("Missing required argument --" + OPT_NAME);
            return 1;
        }
        final Group group = new Group();
        group.setName(options.valueOf(OPT_NAME).toString());
        if (options.has(OPT_DESCRIPTION)) {
            group.setDescription(options.valueOf(OPT_DESCRIPTION).toString());
        }
        if (options.has(OPT_LOCATION)) {
            group.setLocation(options.valueOf(OPT_LOCATION).toString());
        }
        final GroupsResponse groupResponse = groupRepository.createGroup(group, (String) options.valueOf(OPT_VERSION));
        if (groupResponse.getStatus() == ResponseStatus.FAILURE) {
            errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + groupResponse.getError().getReasonText());
            return 1;
        }
        outWriter.println("Command " + COMMAND_NAME + " executed successfully");
        return 0;
    }

    public String getDescription() {
        return "Creates a new Compatible Group/Cluster composed of SpringSource tc Servers";
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
