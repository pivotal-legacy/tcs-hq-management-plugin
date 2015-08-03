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
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.GroupRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for removing a server from a group
 */
public class RemoveServerFromGroupCommand implements Command, InitializingBean {

    protected static final String COMMAND_NAME = "remove-server-from-group";

    protected static final String COMMAND_NAME_DESC = "Removes a SpringSource tc Server from a Compatible Group/Cluster of SpringSource tc Servers";

    protected static final String OPT_GROUP_ID = "groupid";

    protected static final String OPT_GROUP_ID_DESC = "Id of a Compatible Group/Cluster of tc Servers. Mutually exclusive with --groupname.";

    protected static final String OPT_GROUP_NAME = "groupname";

    protected static final String OPT_GROUP_NAME_DESC = "Name of a Compatible Group/Cluster of tc Servers. Mutually exclusive with --groupid.";

    protected static final String OPT_SERVER_ID = "serverid";

    protected static final String OPT_SERVER_ID_DESC = "Id of a single tc Server. Mutually exclusive with --servername.";

    protected static final String OPT_SERVER_NAME = "servername";

    protected static final String OPT_SERVER_NAME_DESC = "Name of a single tc Server. Mutually exclusive with --serverid.";

    private final PrintWriter errorWriter;

    private final GroupRepository groupRepository;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    /**
     * 
     * @param groupRepository The {@link GroupRepository} to use for removing a server from a group
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public RemoveServerFromGroupCommand(GroupRepository groupRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.groupRepository = groupRepository;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
        this.errorWriter = errorWriter;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_SERVER_NAME, OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_SERVER_ID, OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
        optionParser.accepts(OPT_GROUP_NAME, OPT_GROUP_NAME_DESC).withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_GROUP_ID, OPT_GROUP_ID_DESC).withRequiredArg().ofType(Integer.class);
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        if (!options.has(OPT_GROUP_NAME) && !options.has(OPT_GROUP_ID)) {
            errorWriter.println("Either the name or ID of a group must be specified");
            return 1;
        }
        if (!(options.has(OPT_SERVER_ID)) && !(options.has(OPT_SERVER_NAME))) {
            errorWriter.println("Either the name or ID of a server must be specified");
            return 1;
        }
        if (options.has(OPT_SERVER_ID) && options.has(OPT_SERVER_NAME)) {
            errorWriter.println("Only serverid or servername can be specified, not both.");
            return 1;
        }
        final Group group = new Group();
        if (options.has(OPT_GROUP_NAME)) {
            group.setName(options.valueOf(OPT_GROUP_NAME).toString());
        }
        if (options.has(OPT_GROUP_ID)) {
            group.setId((Integer) options.valueOf(OPT_GROUP_ID));
        }
        final Resource server = new Resource();
        if (options.has(OPT_SERVER_ID)) {
            server.setId((Integer) options.valueOf(OPT_SERVER_ID));
        }
        server.setName((String) options.valueOf(OPT_SERVER_NAME));

        final GroupsResponse groupResponse = groupRepository.removeServerFromGroup(group, server);
        if (groupResponse.getStatus() == ResponseStatus.FAILURE) {
            errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + groupResponse.getError().getReasonText());
            return 1;
        }
        outWriter.println("Command " + COMMAND_NAME + " executed successfully");
        return 0;
    }

    public String getDescription() {
        return COMMAND_NAME_DESC;
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
