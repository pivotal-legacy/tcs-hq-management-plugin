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

package com.springsource.hq.plugin.tcserver.serverconfig.web.services;

import com.springsource.hq.plugin.tcserver.serverconfig.web.support.RemoteSettings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * Load and retrieve remote configuration settings from HQ.
 * 
 * @since 2.0
 */
public interface SettingsLoader {

    /**
     * Load configuration settings for tc Runtime
     * 
     * @param eid the tc Runtime instance identifier
     * @param sessionId the authentication users sessionId
     * @param baseUrl the base url of the HQ service
     * @return the settings for the tc Runtime instance
     * @throws SettingsLoaderException if unable to load settings for any reason
     */
    RemoteSettings getConfiguration(String eid, String sessionId, String baseUrl, String csrfNonce) throws SettingsLoaderException;

    /**
     * Restart tc Runtime
     * 
     * @param remoteSettings the connection information
     * @param csrfNonce The nonce to include in the request that's required by the CSRF prevention filter
     * @throws SettingsLoaderException if unable to restart tc Runtime for any reason
     */
    void restartServer(RemoteSettings remoteSettings) throws SettingsLoaderException;

    /**
     * Reverts to the previous configuration of latest backed up files. This will also reflect the restored data in the
     * UI.
     * 
     * @param remoteSettings
     * @param csrfNonce The nonce to include in the request that's required by the CSRF prevention filter
     * @throws SettingsLoaderException
     */
    void revertToPreviousConfiguration(RemoteSettings remoteSettings) throws SettingsLoaderException;

    /**
     * Push configuration settings to tc Runtime
     * 
     * @param remoteSettings the new configuration settings
     * @param csrfNonce The nonce to include in the request that's required by the CSRF prevention filter
     * @throws SettingsLoaderException if unable to push settings for any reason
     */
    void saveConfiguration(RemoteSettings remoteSettings) throws SettingsLoaderException;

    /**
     * Push a user defined configuration file to tc Runtime
     * 
     * @param fileName the file name to push relative to catalina.base
     * @param config the text of the configuration file to push out
     * @param remoteSettings the connection information
     * @param csrfNonce The nonce to include in the request that's required by the CSRF prevention filter
     * @throws SettingsLoaderException if unable to push settings for any reason
     */
    void saveConfigurationFile(String fileName, String config, RemoteSettings remoteSettings) throws SettingsLoaderException;

}
