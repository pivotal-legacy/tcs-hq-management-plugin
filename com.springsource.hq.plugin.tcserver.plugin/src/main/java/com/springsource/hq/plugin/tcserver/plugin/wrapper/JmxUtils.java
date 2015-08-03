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

import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

public interface JmxUtils {

    MBeanServerConnection getMBeanServer(Properties configProperties) throws MalformedURLException, IOException;

    Object getValue(Properties configProperties, String appObjectName, String string) throws MalformedObjectNameException,
        AttributeNotFoundException, InstanceNotFoundException, MalformedURLException, MBeanException, ReflectionException, PluginException,
        IOException;

    Object invoke(Properties configProperties, String objectName, String string, Object[] objects, String[] strings)
        throws MetricUnreachableException, MetricNotFoundException, PluginException;

    String getJmxUrlProperty();

    boolean checkConnection(ConfigResponse config);

}
