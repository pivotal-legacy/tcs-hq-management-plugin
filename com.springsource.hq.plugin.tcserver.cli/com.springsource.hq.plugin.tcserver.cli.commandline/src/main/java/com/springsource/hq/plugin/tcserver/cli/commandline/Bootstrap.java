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

import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstraps the command line scripting application
 */
public class Bootstrap {

    private static final Log LOGGER = LogFactory.getLog(Bootstrap.class);

    static void initConnectionProperties(final String[] args) throws Exception {
        final List<String> connectionArgs = new ArrayList<String>(5);
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.trim().startsWith("--" + OptionParserFactory.OPT_HOST) || arg.trim().startsWith("--" + OptionParserFactory.OPT_PORT)
                || arg.trim().startsWith("--" + OptionParserFactory.OPT_PASS) || arg.trim().startsWith("--" + OptionParserFactory.OPT_USER)
                || arg.trim().startsWith("--" + OptionParserFactory.OPT_SECURE)) {
                connectionArgs.add(arg);
                if (i != args.length - 1 && !(args[i + 1].startsWith("--"))) {
                    connectionArgs.add(args[i + 1]);
                }
            }
        }
        final OptionParser optionParser = (OptionParser) new OptionParserFactory().getObject();
        final OptionSet options = optionParser.parse(connectionArgs.toArray(new String[connectionArgs.size()]));
        if (options.valueOf(OptionParserFactory.OPT_HOST) != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_HOST,
                options.valueOf(OptionParserFactory.OPT_HOST).toString());
        }
        if (options.valueOf(OptionParserFactory.OPT_PORT) != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_PORT,
                options.valueOf(OptionParserFactory.OPT_PORT).toString());
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_PORT_DEFAULTED, "false");
        }
        if (options.valueOf(OptionParserFactory.OPT_USER) != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_USER,
                options.valueOf(OptionParserFactory.OPT_USER).toString());
        }
        if (options.valueOf(OptionParserFactory.OPT_PASS) != null) {
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_PASS,
                options.valueOf(OptionParserFactory.OPT_PASS).toString());
        }
        if (options.has(OptionParserFactory.OPT_SECURE)) {
            String secure = "true";
            if (options.valueOf(OptionParserFactory.OPT_SECURE) != null) {
                secure = options.valueOf(OptionParserFactory.OPT_SECURE).toString();
            }
            System.setProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + OptionParserFactory.OPT_SECURE, secure);
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            initConnectionProperties(args);
        } catch (Exception e) {
            LOGGER.warn("Error parsing command line connection properties.  Will uses connection properties read from ~/ams/client.properties if present or default values.  Error: "
                + e.getMessage());
        }
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
            new String[] { "classpath:META-INF/spring/tcsadmin-context.xml" });
        try {
            final int exitCode = ((CommandDispatcher) applicationContext.getBean("commandDispatcher")).dispatch(args);
            System.exit(exitCode);
        } catch (Exception e) {
            System.err.println("Failed to execute command.  Reason: " + e.getMessage());
            System.exit(1);
        }
    }

}
