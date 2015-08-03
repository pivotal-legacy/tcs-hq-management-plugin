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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public interface EnvironmentRepository {

    /**
     * The will write out the environment file from scratch. This should be used when the file does not exist already.
     * 
     * @param config The configuration response collection of properties
     * @param environment The environment object.
     * @throws PluginException
     */
    void save(ConfigResponse config, Environment environment) throws PluginException;

    /**
     * This is called if the backup file exists. This will preserve the other options the user has added to the file.
     * 
     * @param backupFileName The backup file to read in the file data
     * @param config The configuration response collection of properties
     * @param environment The environment object.
     * @throws PluginException
     */
    void save(final String backupFileName, ConfigResponse config, Environment environment) throws PluginException;
}
