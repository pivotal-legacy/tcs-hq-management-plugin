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

import java.util.Arrays;

import joptsimple.OptionParser;

import org.springframework.beans.factory.FactoryBean;

/**
 * Factory responsible for generating {@link OptionParser}s and initializing with command line options accepted by all
 * commands (i.e. connection parameters and help)
 */
public class OptionParserFactory implements FactoryBean<OptionParser> {

    public static final String OPT_HELP_SHORT = "h";

    public static final String OPT_HELP_LONG = "help";

    public static final String SYSTEM_PROP_PREFIX = "scripting.client.";

    static final String OPT_HOST = "host";

    static final String OPT_PORT = "port";

    static final String OPT_PORT_SSL = "portSSL";

    static final String OPT_PORT_DEFAULTED = "portDefaulted";

    static final String OPT_USER = "user";

    static final String OPT_PASS = "password";

    static final String OPT_SECURE = "secure";

    public OptionParser getObject() throws Exception {
        OptionParser parser = new OptionParser();
        parser.accepts(OPT_HOST, "The server host").withRequiredArg().ofType(String.class);
        parser.accepts(OPT_PORT, "The server port. Defaults to 7080").withRequiredArg().ofType(Integer.class);
        parser.accepts(OPT_USER, "The user to connect as").withRequiredArg().ofType(String.class);
        parser.accepts(OPT_PASS, "The password for the given user").withRequiredArg().ofType(String.class);
        parser.accepts(OPT_SECURE, "Connect using SSL, changes default port to 7443").withOptionalArg().ofType(Boolean.class);
        parser.acceptsAll(Arrays.asList(OPT_HELP_SHORT, OPT_HELP_LONG), "Show this help message");
        return parser;
    }

    public Class<?> getObjectType() {
        return OptionParser.class;
    }

    public boolean isSingleton() {
        return false;
    }

}
