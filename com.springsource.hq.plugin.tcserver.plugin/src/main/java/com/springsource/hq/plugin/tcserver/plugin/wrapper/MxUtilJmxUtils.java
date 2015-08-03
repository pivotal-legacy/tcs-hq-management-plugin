/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

package com.springsource.hq.plugin.tcserver.plugin.wrapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.jmx.MxUtil;
import org.hyperic.util.config.ConfigResponse;

public final class MxUtilJmxUtils implements JmxUtils {

    private final Log log = LogFactory.getLog(MxUtilJmxUtils.class);

    public MBeanServerConnection getMBeanServer(Properties configProperties) throws MalformedURLException, IOException {
        return MxUtil.getMBeanServer(configProperties);
    }

    public Object getValue(Properties configProperties, String appObjectName, String string) throws MalformedObjectNameException,
        AttributeNotFoundException, InstanceNotFoundException, MalformedURLException, MBeanException, ReflectionException, PluginException,
        IOException {
        return MxUtil.getValue(configProperties, appObjectName, string);
    }

    public Object invoke(Properties configProperties, String objectName, String string, Object[] objects, String[] strings)
        throws MetricUnreachableException, MetricNotFoundException, PluginException {
        return MxUtil.invoke(configProperties, objectName, string, objects, strings);
    }

    public String getJmxUrlProperty() {
        return MxUtil.PROP_JMX_URL;
    }

    public boolean checkConnection(ConfigResponse config) {
        JMXConnector jmxConnector = null;

        try {
            jmxConnector = MxUtil.getMBeanConnector(config.toProperties());
            return true;
        } catch (IOException ioe) {
            log.warn("Connection check failed", ioe);
            return false;
        } finally {
            if (jmxConnector != null) {
                try {
                    jmxConnector.close();
                } catch (IOException ioe) {
                    log.warn("Failed to close connection following check", ioe);
                }
            }
        }
    }
}
