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

import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResourcesResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.inventory.ResourceRepository;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for modifying the properties of a server
 */
public class ModifyServerCommand implements Command, InitializingBean {

    private static final String COMMAND_NAME = "modify-server";

    private static final String OPT_DESCRIPTION = "description";

    private static final String OPT_NAME = "name";

    private static final String OPT_SERVER_ID = "serverid";

    private final PrintWriter errorWriter;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    private final ResourceRepository resourceRepository;

    /**
     * 
     * @param resourceRepository The {@link ResourceRepository} to use for modifying servers
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public ModifyServerCommand(ResourceRepository resourceRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
        this.resourceRepository = resourceRepository;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_SERVER_ID, "Id of a single tc Server.").withRequiredArg().ofType(Integer.class);
        optionParser.accepts(OPT_NAME, "Name to set on specified tc Server.  Optional if --" + OPT_DESCRIPTION + " is specified.").withRequiredArg().ofType(
            String.class);
        optionParser.accepts(OPT_DESCRIPTION, "Description to set on specified tc Server.  Optional if --" + OPT_NAME + " is specified.").withRequiredArg().ofType(
            String.class);
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        if (!options.has(OPT_SERVER_ID)) {
            errorWriter.println("Missing required argument --" + OPT_SERVER_ID);
            return 1;
        }
        if (!(options.has(OPT_NAME)) && !(options.has(OPT_DESCRIPTION))) {
            errorWriter.println("Either --" + OPT_NAME + " or --" + OPT_DESCRIPTION + " should be specified");
            return 1;
        }
        final Resource server = new Resource();
        server.setId((Integer) options.valueOf(OPT_SERVER_ID));
        if (options.has(OPT_NAME)) {
            server.setName(options.valueOf(OPT_NAME).toString());
        }
        if (options.has(OPT_DESCRIPTION)) {
            server.setDescription(options.valueOf(OPT_DESCRIPTION).toString());
        }
        return handleResponse(resourceRepository.modifyServer(server));
    }

    public String getDescription() {
        return "Modifies the name and/or description of a SpringSource tc Server";
    }

    public String getName() {
        return COMMAND_NAME;
    }

    private int handleResponse(final ResourcesResponse response) {
        if (response.getStatus() == ResponseStatus.FAILURE) {
            errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + response.getError().getReasonText());
            return 1;
        }
        outWriter.println("Command " + COMMAND_NAME + " executed successfully");
        return 0;
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
