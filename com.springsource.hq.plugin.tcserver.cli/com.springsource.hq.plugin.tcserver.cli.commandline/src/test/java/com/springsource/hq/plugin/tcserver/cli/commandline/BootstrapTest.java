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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of {@link Bootstrap}
 */
public class BootstrapTest {

    @Before
    public void clearProperties() {
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "host");
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "password");
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "port");
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "user");
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "secure");
        System.clearProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "portDefaulted");
    }

    /**
     * Verifies that sys props needed by the ProperyPlaceholderConfigurer are set when connection options are specified
     * on command line
     * 
     * @throws Exception
     */
    @Test
    public void testInitConnectionPropertiesAllPresent() throws Exception {
        Bootstrap.initConnectionProperties(new String[] { "--host=myhost", "--port=1234", "--secure", "--user=auser", "--password=apassword",
            "--something=somethingelse" });
        assertEquals("myhost", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "host"));
        assertEquals("1234", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "port"));
        assertEquals("true", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "secure"));
        assertEquals("auser", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "user"));
        assertEquals("apassword", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "password"));
        assertEquals("false", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "portDefaulted"));
    }

    /**
     * Verifies that sys props needed by the ProperyPlaceholderConfigurer are set when connection options are specified
     * on command line using "--optname optvalue"
     * 
     * @throws Exception
     */
    @Test
    public void testInitConnectionPropertiesNoEquals() throws Exception {
        Bootstrap.initConnectionProperties(new String[] { "--host", "myhost", "--port", "1234", "--secure", "--user", "auser", "--password",
            "apassword" });
        assertEquals("myhost", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "host"));
        assertEquals("1234", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "port"));
        assertEquals("true", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "secure"));
        assertEquals("auser", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "user"));
        assertEquals("apassword", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "password"));
        assertEquals("false", System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "portDefaulted"));
    }

    /**
     * Verifies that sys props needed by the ProperyPlaceholderConfigurer are not set when connection options are not
     * specified on command line
     * 
     * @throws Exception
     */
    @Test
    public void testInitConnectionPropertiesNonePresent() throws Exception {
        Bootstrap.initConnectionProperties(new String[0]);
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "host"));
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "port"));
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "secure"));
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "user"));
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "password"));
        assertEquals(null, System.getProperty(OptionParserFactory.SYSTEM_PROP_PREFIX + "portDefaulted"));
    }
}
