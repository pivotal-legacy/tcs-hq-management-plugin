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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.configuration.ConfigurationRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ConfigurationStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of @{link Command} responsible for retrieving files from a tc Server
 */
public class GetFileCommand implements Command, InitializingBean {

    protected static final String COMMAND_NAME = "get-file";

    protected static final String COMMAND_NAME_DESC = "Retrieves a file from a SpringSource tc Server";

    protected static final String OPT_FILE = "file";

    protected static final String OPT_FILE_DESC = "Path of file to retrieve from the tc Server. This path is resolved relative to the catalina.base directory of the tc Server.";

    protected static final String OPT_SERVER_ID = "serverid";

    protected static final String OPT_SERVER_ID_DESC = "Id of a single tc Server. Mutually exclusive with --servername.";

    protected static final String OPT_SERVER_NAME = "servername";

    protected static final String OPT_SERVER_NAME_DESC = "Name of a single tc Server. Mutually exclusive with --serverid.";

    protected static final String OPT_TARGET_FILE = "targetfile";

    protected static final String OPT_TARGET_FILE_DESC = "Local path to which the retrieved file should be saved.";

    private final ConfigurationRepository configurationRepository;

    private final PrintWriter errorWriter;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    /**
     * 
     * @param configurationRepository The {@link ConfigurationRepository} to use for retrieving files
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public GetFileCommand(ConfigurationRepository configurationRepository, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.configurationRepository = configurationRepository;
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_SERVER_NAME, OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_SERVER_ID, OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
        optionParser.accepts(OPT_FILE, OPT_FILE_DESC).withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_TARGET_FILE, OPT_TARGET_FILE_DESC).withRequiredArg().ofType(String.class);
    }

    public int execute(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage(errorWriter);
            return 1;
        }
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
        }
        if (!options.has(OPT_FILE)) {
            errorWriter.println("Missing required argument --" + OPT_FILE);
            return 1;
        }
        if (!options.has(OPT_TARGET_FILE)) {
            errorWriter.println("Missing required argument --" + OPT_TARGET_FILE);
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
        final Resource server = new Resource();
        if (options.has(OPT_SERVER_ID)) {
            server.setId((Integer) options.valueOf(OPT_SERVER_ID));
        }
        server.setName((String) options.valueOf(OPT_SERVER_NAME));
        final ConfigurationStatusResponse response = configurationRepository.getFile(server, options.valueOf(OPT_FILE).toString(), new File(
            options.valueOf(OPT_TARGET_FILE).toString()));
        return handleResponse(response);
    }

    public String getDescription() {
        return COMMAND_NAME_DESC;
    }

    public String getName() {
        return COMMAND_NAME;
    }

    private int handleResponse(final ConfigurationStatusResponse response) {
        if (response.getStatus() == ResponseStatus.FAILURE) {
            if (response.getStatusResponse().isEmpty()) {
                // General Error
                errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + response.getError().getReasonText());
                return 1;
            }
            final StatusResponse statusResponse = response.getStatusResponse().get(0);
            // Specific Error per server
            errorWriter.println("Failed to get file from " + statusResponse.getResourceName() + ".  Reason: "
                + statusResponse.getError().getReasonText());

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
        writer.println(getName() + ": " + getDescription());
        optionParser.printHelpOn(writer);
    }
}
