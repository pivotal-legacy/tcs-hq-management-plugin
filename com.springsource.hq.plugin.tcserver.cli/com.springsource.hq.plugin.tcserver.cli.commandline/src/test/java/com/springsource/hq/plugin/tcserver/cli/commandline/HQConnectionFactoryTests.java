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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.hyperic.hq.hqapi1.HQConnection;
import org.junit.Test;

/**
 * Unit test of {@link HQConnectionFactory}
 */
public class HQConnectionFactoryTests {

    @Test
    public void testObjectType() {
        HQConnectionFactory factory = new HQConnectionFactory(new Properties());
        assertEquals(HQConnection.class, factory.getObjectType());
    }

    @Test
    public void testObject() throws Exception {
        HQConnectionFactory factory = new HQConnectionFactory(new Properties());
        assertTrue(factory.getObject() instanceof HQConnection);
    }

    @Test
    public void testDefaultConnectionPort() throws Exception {
        HQConnectionFactory factory = new HQConnectionFactory(new Properties());
        assertEquals(null, factory.getProperties().getProperty("port"));
    }

    @Test
    public void testDefaultSSLConnectionPort() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secure", "true");
        HQConnectionFactory factory = new HQConnectionFactory(properties);
        assertEquals("7443", factory.getProperties().getProperty("port"));
    }

    @Test
    public void testExplicitConnectionPort() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("port", "7777");
        HQConnectionFactory factory = new HQConnectionFactory(properties);
        assertEquals("7777", factory.getProperties().getProperty("port"));
    }

    @Test
    public void testExplicitSSLConnectionPort() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secure", "true");
        properties.setProperty("port", "7777");

        HQConnectionFactory factory = new HQConnectionFactory(properties);
        assertEquals("7777", factory.getProperties().getProperty("port"));
    }

    @Test
    public void defaultPassword() throws Exception {
        HQConnectionFactory factory = new HQConnectionFactory(new Properties());
        assertEquals("hqadmin", factory.getProperties().getProperty("password"));
    }

    @Test
    public void explicitPassword() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("password", "secret");
        HQConnectionFactory factory = new HQConnectionFactory(properties);
        assertEquals("secret", factory.getProperties().getProperty("password"));
    }

    @Test
    public void copyingOfRelevantSystemProperties() throws Exception {
        Properties properties = new Properties();
        HQConnectionFactory factory;
        try {
            System.setProperty("irrelevant", "alpha");
            System.setProperty("scripting.client.relevant", "bravo");

            factory = new HQConnectionFactory(properties);
        } finally {
            System.clearProperty("irrelevant");
            System.clearProperty("scripting.client.relevant");
        }

        assertNull(factory.getProperties().getProperty("irrelevant"));
        assertEquals("bravo", factory.getProperties().getProperty("relevant"));
    }
}
