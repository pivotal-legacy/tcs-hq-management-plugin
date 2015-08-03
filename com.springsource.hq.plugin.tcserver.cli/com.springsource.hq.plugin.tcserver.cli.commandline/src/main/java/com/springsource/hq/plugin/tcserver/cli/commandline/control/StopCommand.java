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

package com.springsource.hq.plugin.tcserver.cli.commandline.control;

import java.io.IOException;
import java.io.PrintWriter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;
import org.springframework.beans.factory.InitializingBean;

import com.springsource.hq.plugin.tcserver.cli.client.control.ControlOperationInvoker;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ControlStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.StatusResponse;
import com.springsource.hq.plugin.tcserver.cli.commandline.Command;
import com.springsource.hq.plugin.tcserver.cli.commandline.OptionParserFactory;

/**
 * Implementation of {@link Command} responsible for stopping a tc Server or group of tc Servers
 */
public class StopCommand implements Command, InitializingBean {

    protected static final String COMMAND_NAME = "stop";

    protected static final String COMMAND_NAME_DESC = "Stops a SpringSource tc Server or a group of SpringSource tc Servers";

    protected static final String OPT_GROUP_ID = "groupid";

    protected static final String OPT_GROUP_ID_DESC = "Id of a Compatible Group/Cluster of tc Servers. Mutually exclusive with --groupname.";

    protected static final String OPT_GROUP_NAME = "groupname";

    protected static final String OPT_GROUP_NAME_DESC = "Name of a Compatible Group/Cluster of tc Servers. Mutually exclusive with --groupid.";

    protected static final String OPT_SERVER_ID = "serverid";

    protected static final String OPT_SERVER_ID_DESC = "Id of a single tc Server. Mutually exclusive with --servername.";

    protected static final String OPT_SERVER_NAME = "servername";

    protected static final String OPT_SERVER_NAME_DESC = "Name of a single tc Server. Mutually exclusive with --serverid.";

    private final ControlOperationInvoker controlOperationInvoker;

    private final PrintWriter errorWriter;

    private final OptionParser optionParser;

    private final PrintWriter outWriter;

    /**
     * 
     * @param controlOperationInvoker The {@link ControlOperationInvoker} to use for invoking the stop operation
     * @param optionParser The {@link OptionParser} to use for parsing command line options
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public StopCommand(ControlOperationInvoker controlOperationInvoker, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.controlOperationInvoker = controlOperationInvoker;
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
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
        if (!(options.has(OPT_GROUP_NAME)) && !(options.has(OPT_GROUP_ID)) && !(options.has(OPT_SERVER_ID)) && !(options.has(OPT_SERVER_NAME))) {
            errorWriter.println("Either the name or ID of a server or group must be specified");
            return 1;
        }
        if ((options.has(OPT_GROUP_NAME) || options.has(OPT_GROUP_ID)) && (options.has(OPT_SERVER_ID) || options.has(OPT_SERVER_NAME))) {
            errorWriter.println("Only one of either server or group identifiers should be specified");
            return 1;
        }
        if (options.has(OPT_SERVER_ID) && options.has(OPT_SERVER_NAME)) {
            errorWriter.println("Only serverid or servername can be specified, not both.");
            return 1;
        }
        if (options.has(OPT_SERVER_ID) || options.has(OPT_SERVER_NAME)) {
            final Resource server = new Resource();
            if (options.has(OPT_SERVER_ID)) {
                server.setId((Integer) options.valueOf(OPT_SERVER_ID));
            }
            server.setName((String) options.valueOf(OPT_SERVER_NAME));
            final ControlStatusResponse response = controlOperationInvoker.stop(server);
            return handleResponse(response);
        }
        final Group group = new Group();
        if (options.has(OPT_GROUP_ID)) {
            group.setId((Integer) options.valueOf(OPT_GROUP_ID));
        }
        group.setName((String) options.valueOf(OPT_GROUP_NAME));
        final ControlStatusResponse response = controlOperationInvoker.stop(group);
        return handleResponse(response);
    }

    public String getDescription() {
        return COMMAND_NAME_DESC;
    }

    public String getName() {
        return COMMAND_NAME;
    }

    private int handleResponse(final ControlStatusResponse response) {
        if (response.getStatus() == ResponseStatus.FAILURE) {
            if (response.getStatusResponse().isEmpty()) {
                // General Error
                errorWriter.println("Failed to execute command " + COMMAND_NAME + ".  Reason: " + response.getError().getReasonText());
                return 1;
            }
            for (StatusResponse statusResponse : response.getStatusResponse()) {
                if (statusResponse.getStatus() == ResponseStatus.FAILURE) {
                    // Specific Error per server
                    errorWriter.println("Failed to stop " + statusResponse.getResourceName() + ".  Reason: "
                        + statusResponse.getError().getReasonText());
                }
            }
            return response.getStatusResponse().size();
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
