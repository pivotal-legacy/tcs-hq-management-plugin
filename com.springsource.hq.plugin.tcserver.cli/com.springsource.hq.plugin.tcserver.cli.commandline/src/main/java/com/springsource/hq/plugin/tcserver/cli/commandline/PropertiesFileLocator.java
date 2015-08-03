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

import java.io.File;

import org.springframework.beans.factory.FactoryBean;

/**
 * Class responsible for returning the name of a properties file from a predefined location in order to override
 * connection properties (to avoid specifying host, port, etc for every command). Note this file does not have to be
 * present, this class simply returns the name of the file relative to user.home
 */
public class PropertiesFileLocator implements FactoryBean<String> {

    public String getObject() throws Exception {
        final String home = System.getProperty("user.home");
        final File hq = new File(home, ".hq");
        final File clientProperties = new File(hq, "client.properties");
        return "file:" + clientProperties.getAbsolutePath();
    }

    public Class<?> getObjectType() {
        return String.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
