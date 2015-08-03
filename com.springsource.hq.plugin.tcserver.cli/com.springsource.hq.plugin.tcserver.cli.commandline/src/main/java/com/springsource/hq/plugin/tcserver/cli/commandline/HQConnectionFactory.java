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

import java.util.Properties;
import java.util.Set;

import org.hyperic.hq.hqapi1.HQConnection;
import org.springframework.beans.factory.FactoryBean;

/**
 * Bean factory for HQConnection. This factory will determine the correct port to use. The SSL port will be used when
 * the request is secure and the port is otherwise unspecified.
 */
public class HQConnectionFactory implements FactoryBean<HQConnection> {

    private static final String PROPERTY_PASSWORD = "password";

    private static final String PROPERTY_ENCRYPTED_PASSWORD = "encryptedPassword";

    private static final String PROPERTY_SECURE = "secure";

    private static final String PROPERTY_PORT = "port";

    private static final String DEFAULT_SECURE_PORT = "7443";

    private static final String DEFAULT_PASSWORD = "hqadmin";

    private final Properties properties;

    public HQConnectionFactory(Properties properties) {
        this.properties = applyDefaults(properties);
    }

    public HQConnection getObject() throws Exception {
        return new HQConnection(properties);
    }

    public Class<?> getObjectType() {
        return HQConnection.class;
    }

    public boolean isSingleton() {
        return true;
    }

    Properties getProperties() {
        return this.properties;
    }

    private Properties applyDefaults(Properties originalProperties) {
        Properties properties = new Properties(originalProperties);

        addRelevantSystemProperties(properties);

        setPropertyIfNotNull(properties, PROPERTY_PORT, determinePort(properties));
        setPropertyIfNotNull(properties, PROPERTY_PASSWORD, determinePassword(properties));

        return properties;
    }

    private void addRelevantSystemProperties(Properties properties) {
        Properties systemProperties = System.getProperties();
        Set<Object> keySet = systemProperties.keySet();
        for (Object keyObj : keySet) {
            if (keyObj instanceof String) {
                String key = (String) keyObj;
                if (key.startsWith(OptionParserFactory.SYSTEM_PROP_PREFIX)) {
                    properties.put(((String) keyObj).substring(OptionParserFactory.SYSTEM_PROP_PREFIX.length()), systemProperties.getProperty(key));
                }
            }
        }
    }

    private String determinePort(Properties properties) {
        if (isSecure(properties)) {
            String port = getPort(properties);
            if (port == null) {
                return DEFAULT_SECURE_PORT;
            }
        }
        return null;
    }

    private String determinePassword(Properties properties) {
        if ((properties.getProperty(PROPERTY_PASSWORD) == null) && (properties.getProperty(PROPERTY_ENCRYPTED_PASSWORD) == null)) {
            return DEFAULT_PASSWORD;
        }
        return null;
    }

    private boolean isSecure(Properties properties) {
        return Boolean.valueOf(properties.getProperty(PROPERTY_SECURE));
    }

    private String getPort(Properties properties) {
        return properties.getProperty(PROPERTY_PORT);
    }

    private void setPropertyIfNotNull(Properties properties, String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        }
    }
}
