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

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.ApplicationStatus;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Service;

public interface ScriptingApplicationManager {

    List<ApplicationStatus> deploy(ConfigResponse config) throws PluginException;

    Map<String, List<String>> getServiceHostMappings(ConfigResponse config) throws PluginException;

    List<Service> list(ConfigResponse config) throws PluginException;

    List<ApplicationStatus> reload(ConfigResponse config) throws PluginException;

    List<ApplicationStatus> start(ConfigResponse config) throws PluginException;

    List<ApplicationStatus> stop(ConfigResponse config) throws PluginException;

    List<ApplicationStatus> undeploy(ConfigResponse config) throws PluginException;
}
