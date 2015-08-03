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

package com.springsource.hq.plugin.tcserver.cli.commandline;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import joptsimple.OptionParser;

/**
 * Default implementation of {@link CommandDispatcher}
 */
public class DefaultCommandDispatcher implements CommandDispatcher {

    private static final String HELP_COMMAND = "help";

    private final Map<String, Command> commands;

    private PrintWriter errorWriter;

    private final OptionParser optionParser;

    private PrintWriter outWriter;

    /**
     * 
     * @param commands An {@link List} of {@link Command}s to dispatch
     * @param outWriter The {@link PrintWriter} with which to print standard output messages
     * @param errorWriter The {@link PrintWriter} with which to print error messages
     */
    public DefaultCommandDispatcher(List<Command> commands, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.commands = new LinkedHashMap<String, Command>(commands.size(), 1);
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
    }

    DefaultCommandDispatcher(Map<String, Command> commands, OptionParser optionParser, PrintWriter outWriter, PrintWriter errorWriter) {
        this.commands = commands;
        this.errorWriter = errorWriter;
        this.optionParser = optionParser;
        this.outWriter = outWriter;
    }

    public int dispatch(String[] args) {
        if (args.length < 1) {
            printUsage(errorWriter);
            return 1;
        }
        if (args[0].equals(HELP_COMMAND)) {
            printHelp();
            return 0;
        }
        final Command command = commands.get(args[0]);
        if (command == null) {
            printUsage(errorWriter);
            return 1;
        }
        final String[] commandArgs = getCommandArguments(args);
        try {
            return command.execute(commandArgs);
        } catch (IOException e) {
            if (e instanceof UnknownHostException) {
                errorWriter.println("Failed to execute command " + args[0] + ". Unknown host: " + e.getMessage());
            } else if (e instanceof SSLHandshakeException) {
                errorWriter.println("Failed to execute command " + args[0]
                    + ".  The server's SSL certificate is not trusted.  Check the documentation for details.");
            } else {
                final StringBuilder errorMessage = new StringBuilder();
                if (e.getMessage() != null) {
                    errorMessage.append("  Reason: ").append(e.getMessage()).append(".");
                }
                if (e.getCause() != null && e.getCause().getMessage() != null) {
                    errorMessage.append("  Cause: ").append(e.getCause().getMessage()).append(".");
                }
                errorWriter.println("Failed to execute command " + args[0] + "." + errorMessage.toString());
            }
            return 1;
        }
    }

    private String[] getCommandArguments(String[] args) {
        final String[] cmdArgs = new String[args.length - 1];
        System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
        return cmdArgs;
    }

    private void printHelp() {
        // TODO more documentation here?
        printUsage(outWriter);
    }

    private void printUsage(PrintWriter writer) {
        writer.println("Usage: tcsadmin <command name> [options]");
        writer.println();
        writer.println("Available Commands:");
        for (Map.Entry<String, Command> commandEntry : commands.entrySet()) {
            writer.println("   " + commandEntry.getKey());
        }
        writer.println();
        writer.println("For specific command information, run 'tcsadmin <command name> --help'");
        try {
            // do not print anything if there is an exception generating common option help
            StringWriter commonOptsBuf = new StringWriter();
            optionParser.printHelpOn(commonOptsBuf);
            writer.println();
            writer.println("Common Options:");
            writer.println(commonOptsBuf.toString());
        } catch (IOException e) {
            // ignore
        }
    }
}
