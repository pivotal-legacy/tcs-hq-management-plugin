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

import java.util.Set;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Logging;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * Provides easy access to get and save elements in the configuration.
 * 
 * @since 2.0
 */
public interface SettingsService {

    /**
     * Load new settings with the provided connection information. The settings are cached locally for future use.
     * 
     * @param eid the tc Runtime instance id
     * @param sessionId the authenticated users session id
     * @param baseUrl the base url for HQ
     * @return the settings for the tc Runtime instance
     * @throws SettingsLoaderException if unable to load settings for any reason
     */
    public Settings loadSettings(String eid, String sessionId, String csrfNonce, String baseUrl, boolean readOnly) throws SettingsLoaderException;

    /**
     * Push settings to tc Runtime.
     * 
     * @param eid the tc Runtime instance id
     * @throws SettingsLoaderException if unable to push settings for any reason
     */
    public void saveSettings(String eid) throws SettingsLoaderException;

    /**
     * Ignore local changes in the configuration reloading settings from a remote tc Runtime instance.
     * 
     * @param eid the tc Runtime instance id
     * @throws SettingsLoaderException if unable to reload settings for any reason
     */
    public void reloadSettings(String eid) throws SettingsLoaderException;

    /**
     * Push a user defined configuration file to tc Runtime.
     * 
     * @param eid the tc Runtime instance id
     * @param fileName the location of the file relative to catalina.base
     * @param fileText the text of the file
     * @throws SettingsLoaderException if unable to push settings for any reason
     */
    public void uploadConfigurationFile(String eid, String fileName, String fileText) throws SettingsLoaderException;

    /**
     * Restart tc Runtime.
     * 
     * @param settingsId the tc Runtime instance id
     * @throws SettingsLoaderException if unable to restart Tomcat for any reason
     */
    public void restartServer(String settingsId) throws SettingsLoaderException;

    /**
     * Revert to the latest backup configuration files.
     * 
     * @param eid The tc Runtime instance id
     * @throws SettingsLoaderException
     */
    void revertToPreviousConfiguration(String eid) throws SettingsLoaderException;

    /**
     * Load locally cached settings.
     * 
     * @param eid the tc Runtime instance id
     * @return settings
     */
    public Settings loadSettings(String eid);

    /**
     * Update existing local settings
     * 
     * @param settings
     */
    public void updateLocalSettings(Settings settings);

    /**
     * Load local general configuration settings
     * 
     * @param eid the tc Runtime instance id
     * @return local general configuration
     */
    public GeneralConfig loadGeneralConfig(String eid);

    /**
     * Save general configuration locally
     * 
     * @param eid the tc Runtime instance id
     * @param generalConfig general configuration
     */
    public void saveGeneralConfig(String eid, GeneralConfig generalConfig);

    /**
     * Load local environmental options
     * 
     * @param eid the tc Runtime instance id
     * @return local environmental options
     */
    public Environment loadEnvironment(String eid);

    /**
     * Save environmental options locally
     * 
     * @param eid the tc Runtime instance id
     * @param environment environmental options
     */
    public void saveEnvironment(String eid, Environment environment);

    /**
     * Load local context container
     * 
     * @param eid the tc Runtime instance id
     * @return local context container
     */
    public ContextContainer loadContextContainer(String eid);

    /**
     * Save context container locally
     * 
     * @param eid the tc Runtime instance id
     * @param contextContainer context container
     */
    public void saveContextContainer(String eid, ContextContainer contextContainer);

    /**
     * Load local JSP defaults
     * 
     * @param eid the tc Runtime instance id
     * @return local JSP defaults
     */
    public JspDefaults loadJspDefaults(String eid);

    /**
     * Save JSP defaults locally
     * 
     * @param eid the tc Runtime instance id
     * @param jspDefaults JSP defaults
     */
    public void saveJspDefaults(String eid, JspDefaults jspDefaults);

    /**
     * Load local static defaults
     * 
     * @param eid the tc Runtime instance id
     * @return local static defaults
     */
    public StaticDefaults loadStaticDefaults(String eid);

    /**
     * Save static defaults locally
     * 
     * @param eid the tc Runtime instance id
     * @param staticDefaults static defaults
     */
    public void saveStaticDefaults(String eid, StaticDefaults staticDefaults);

    /**
     * Load local data sources
     * 
     * @param eid the tc Runtime instance id
     * @return set of all local data sources
     */
    public Set<DataSource> loadDataSources(String eid);

    /**
     * Load local DBCP data sources
     * 
     * @param eid the tc Runtime instance id
     * @return set of local DBCP data sources
     */
    public Set<DbcpDataSource> loadDbcpDataSources(String eid);

    /**
     * Load local Tomcat data sources
     * 
     * @param eid the tc Runtime instance id
     * @return set of local Tomcat data sources
     */
    public Set<TomcatDataSource> loadTomcatDataSources(String eid);

    /**
     * Load local data source
     * 
     * @param eid the tc Runtime instance id
     * @param dataSourceName human id of data source
     * @return local data source
     */
    public DataSource loadDataSource(String eid, String dataSourceName);

    /**
     * Load local DBCP data source
     * 
     * @param eid the tc Runtime instance id
     * @param dataSourceName human id of data source
     * @return local DBCP data source
     */
    public DbcpDataSource loadDbcpDataSource(String eid, String dataSourceName);

    /**
     * Load local Tomcat data source
     * 
     * @param eid the tc Runtime instance id
     * @param dataSourceName human id of data source
     * @return local Tomcat data source
     */
    public TomcatDataSource loadTomcatDataSource(String eid, String dataSourceName);

    /**
     * Add new data source locally
     * 
     * @param eid the tc Runtime instance id
     * @param dataSource the data source
     */
    public void addDataSource(String eid, DataSource dataSource);

    /**
     * Replace an existing local data source
     * 
     * @param eid the tc Runtime instance id
     * @param oldDataSourceName the old data source's human id
     * @param dataSource the new data source
     */
    public void saveDataSource(String eid, String oldDataSourceName, DataSource dataSource);

    /**
     * Remove a data source locally
     * 
     * @param eid the tc Runtime instance id
     * @param dataSourceName the data source human id
     */
    public void deleteDataSource(String eid, String dataSourceName);

    /**
     * Load local services
     * 
     * @param eid the tc Runtime instance id
     * @return set of local services
     */
    public Set<Service> loadServices(String eid);

    /**
     * Load local service
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service human id
     * @return local service
     */
    public Service loadService(String eid, String serviceName);

    /**
     * Add new service locally
     * 
     * @param eid the tc Runtime instance id
     * @param service the service
     */
    public void addService(String eid, Service service);

    /**
     * Replace an existing local service
     * 
     * @param eid the tc Runtime instance id
     * @param oldServiceName the old service's human id
     * @param service the new service
     */
    public void saveService(String eid, String oldServiceName, Service service);

    /**
     * Remove a service locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     */
    public void deleteService(String eid, String serviceName);

    /**
     * Load local connectors
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return set of local connectors
     */
    public Set<Connector> loadConnectors(String eid, String serviceName);

    /**
     * Load local AJP connectors
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return set of local AJP connectors
     */
    public Set<AjpConnector> loadAjpConnectors(String eid, String serviceName);

    /**
     * Load local HTTP connectors
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return set of HTTP connectors
     */
    public Set<HttpConnector> loadHttpConnectors(String eid, String serviceName);

    /**
     * Load local connector
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param connectorName the connector's human id
     * @return the local connector
     */
    public Connector loadConnector(String eid, String serviceName, String connectorName);

    /**
     * Add a new connector locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param connector connector
     */
    public void addConnector(String eid, String serviceName, Connector connector);

    /**
     * Replace an existing local connector
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param oldConnectorName the old connector's human id
     * @param connector connector
     */
    public void saveConnector(String eid, String serviceName, String oldConnectorName, Connector connector);

    /**
     * Remove a connector locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param connectorName the connector's human id
     */
    public void deleteConnector(String eid, String serviceName, String connectorName);

    /**
     * Load local engine
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return local engine
     */
    public Engine loadEngine(String eid, String serviceName);

    /**
     * Replace an existing local engine
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param engine the engine
     */
    public void saveEngine(String eid, String serviceName, Engine engine);

    /**
     * Load local hosts
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return set of local hosts
     */
    public Set<Host> loadHosts(String eid, String serviceName);

    /**
     * Load local host
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param hostName the host's human id
     * @return the local host
     */
    public Host loadHost(String eid, String serviceName, String hostName);

    /**
     * Add new host locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param host the host
     */
    public void addHost(String eid, String serviceName, Host host);

    /**
     * Replace host locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param oldHostName the old host's human id
     * @param host the host
     */
    public void saveHost(String eid, String serviceName, String oldHostName, Host host);

    /**
     * Remove host locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param hostName the host's human id
     */
    public void deleteHost(String eid, String serviceName, String hostName);

    /**
     * Load local logging
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @return local logging
     */
    public Logging<Engine> loadLogging(String eid, String serviceName);

    /**
     * Replace logging locally
     * 
     * @param eid the tc Runtime instance id
     * @param serviceName the service's human id
     * @param logging the logging
     */
    public void saveLogging(String eid, String serviceName, Logging<Engine> logging);

    /**
     * @param eid the tc Runtime instance id
     * @return true if the local settings have changed
     */
    public boolean isChangePending(String eid);

    /**
     * @param eid the tc Runtime instance id
     * @return true if the server needs to restart to pick up configuration changes
     */
    public boolean isRestartPending(String eid);

    /**
     * This is not a reliable authorization check. This value can easily be faked out by the end-user. It is only
     * intended to be as an indicator if the user should recieve the read-only vs read-write interface. The true auth
     * check must occur in the HQU controller.
     * 
     * @param eid the tc Runtime instance id
     * @return true if the settings are read only changes
     */
    public boolean isReadOnly(String eid);

    public boolean isJmxListenerChanged(String settingsId);

    public void setJmxListenerChanged(String settingsId, boolean changed);
}
