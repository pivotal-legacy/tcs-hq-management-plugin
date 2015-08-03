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

import java.io.IOException;
import java.io.PrintWriter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.configuration.ConfigurationRepository;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for listing the JVM options of a single tc Server
 */
public class ListJvmOptionsCommand implements Command, InitializingBean {

    protected static final String COMMAND_NAME = "list-jvm-options";

    protected static final String COMMAND_NAME_DESC = "Lists the JVM options set on a SpringSource tc Server";

    protected static final String OPT_SERVER_ID = "serverid";

    protected static final String OPT_SERVER_ID_DESC = "Id of a single tc Server. Mutually exclusive with --servername.";

    protected static final String OPT_SERVER_NAME = "servername";

    protected static final String OPT_SERVER_NAME_DESC = "Name of a single tc Server. Mutually exclusive with --serverid.";

    private final ConfigurationRepository configurationRepository;

    private final PrintWriter errorWriter;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    /**
     * 
     * @param configurationRepository The {@link ConfigurationRepository} to use for retrieving JVM Options
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public ListJvmOptionsCommand(ConfigurationRepository configurationRepository, OptionParser optionParser, PrintWriter outWriter,
        PrintWriter errorWriter) {
        this.configurationRepository = configurationRepository;
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
    }

    public void afterPropertiesSet() throws Exception {
        optionParser.accepts(OPT_SERVER_NAME, OPT_SERVER_NAME_DESC).withRequiredArg().ofType(String.class);
        optionParser.accepts(OPT_SERVER_ID, OPT_SERVER_ID_DESC).withRequiredArg().ofType(Integer.class);
    }

    public int execute(String[] args) throws IOException {
        final OptionSet options = optionParser.parse(args);
        if (options.has(OptionParserFactory.OPT_HELP_SHORT) || options.has(OptionParserFactory.OPT_HELP_LONG)) {
            printHelp();
            return 0;
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
        final JvmOptionsResponse response = configurationRepository.getJvmOptions(server);
        return handleResponse(response);
    }

    public String getDescription() {
        return COMMAND_NAME_DESC;
    }

    public String getName() {
        return COMMAND_NAME;
    }

    private int handleResponse(final JvmOptionsResponse response) {
        if (response.getStatus() == ResponseStatus.FAILURE) {
            if (response.getStatusResponse().isEmpty()) {
                // General Error
                errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + response.getError().getReasonText());
                return 1;
            }
            final StatusResponse statusResponse = response.getStatusResponse().get(0);
            // Specific Error per server
            errorWriter.println("Failed to retrieve JVM options from " + statusResponse.getResourceName() + ".  Reason: "
                + statusResponse.getError().getReasonText());

            return 1;
        }
        for (String option : response.getJvmOptions().getOption()) {
            outWriter.println(option);
        }
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
