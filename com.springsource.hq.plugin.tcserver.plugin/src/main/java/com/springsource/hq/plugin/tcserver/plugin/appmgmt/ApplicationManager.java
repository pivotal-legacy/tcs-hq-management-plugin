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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;

/**
 * Handles all functions related to management of web applications in tc Runtime
 * 
 */
public interface ApplicationManager {

    Object deploy(ConfigResponse config) throws PluginException;

    Map<String, List<String>> getServiceHostMappings(ConfigResponse config) throws PluginException;

    Set<Application> list(ConfigResponse config) throws PluginException;

    Map<String, Object> reload(ConfigResponse config) throws PluginException;

    Map<String, Object> start(ConfigResponse config) throws PluginException;

    Map<String, Object> stop(ConfigResponse config) throws PluginException;

    Map<String, Object> undeploy(ConfigResponse config) throws PluginException;

    void removeTemporaryWarFile(ConfigResponse config);

    String getAppBase(ConfigResponse config) throws PluginException;
}
