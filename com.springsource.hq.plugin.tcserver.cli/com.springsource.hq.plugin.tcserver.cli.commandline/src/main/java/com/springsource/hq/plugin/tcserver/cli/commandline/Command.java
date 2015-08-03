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

/**
 * A command that can be executed through the command line client
 */
public interface Command {

    /**
     * The delimiter used to separate fields in command output
     */
    String OUTPUT_DELIMITER = "|";

    /**
     * Executes the command
     * 
     * @param args Command arguments. Currently expected in the form --name=value
     * @return An exit code indicating success or failure. Generally, 0 should indicate success. 1 or greater indicates
     *         failure
     * @throws IOException If unable to connect to server
     */
    int execute(String[] args) throws IOException;

    /**
     * 
     * @return A description of the command
     */
    String getDescription();

    /**
     * 
     * @return The name of the command
     */
    String getName();

}
