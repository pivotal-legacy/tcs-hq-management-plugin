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

/**
 * Help messages for commonly used command arguments
 */
public final class HelpMessages {

    private static final String newline = System.getProperty("line.separator");

    public static final String HEADER = "Option                                  Description                            " + newline
        + "------                                  -----------                            ";

    public static final String HOST = "--host                                  The server host                        ";

    public static final String PORT = "--port <Integer>                        The server port. Defaults to 7080      ";

    public static final String USER = "--user                                  The user to connect as";

    public static final String PASSWORD = "--password                              The password for the given user        ";

    public static final String SECURE = "--secure [Boolean]                      Connect using SSL, changes default     " + newline
        + "                                          port to 7443                         ";

    public static final String HELP = "-h, --help                              Show this help message                 ";

    public static final String GROUP_ID = "--groupid <Integer>                     Id of a Compatible Group/Cluster of tc " + newline
        + "                                          Servers.  Mutually exclusive with " + newline
        + "                                          --groupname.           ";

    public static final String GROUP_NAME = "--groupname                             Name of a Compatible Group/Cluster of  " + newline
        + "                                          tc Servers.  Mutually exclusive with " + newline
        + "                                          --groupid.           ";

    public static final String SERVER_ID = "--serverid <Integer>                    Id of a single tc Server.  Mutually " + newline
        + "                                          exclusive with --servername.           ";

    public static final String SERVER_NAME = "--servername                            Name of a single tc Server.  Mutually " + newline
        + "                                          exclusive with --serverid.          ";

    public static final String NO_BACKUP = "--nobackupfile                          Turns off the creation of a backup     " + newline
        + "                                          file.                                ";

}
