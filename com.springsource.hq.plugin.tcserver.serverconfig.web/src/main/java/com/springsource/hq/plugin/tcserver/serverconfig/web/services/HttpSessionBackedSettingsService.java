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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
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
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.HttpServletContextHolder;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.NotFoundException;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.RemoteSettings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * {@link SettingsService} implementation backed by the HttpSession to locally cache settings. The session is held by
 * the {@link HttpServletContextHolder}.
 * 
 * @since 2.0
 */
@org.springframework.stereotype.Service
public class HttpSessionBackedSettingsService implements SettingsService {

    private HttpServletContextHolder context;

    private SettingsLoader settingsLoader;

    private boolean devMode = false;

    @Autowired
    public HttpSessionBackedSettingsService(HttpServletContextHolder context, SettingsLoader settingsLoader, Boolean devMode) {
        this.context = context;
        this.settingsLoader = settingsLoader;
        this.devMode = devMode;
    }

    public Settings loadSettings(String eid, String sessionId, String csrfNonce, String basePath, boolean readOnly) throws SettingsLoaderException {
        Settings settings = null;
        RemoteSettings remoteSettings = null;
        if (!devMode) {
            // attempt to lookup settings locally before heading off to HQ
            try {
                return loadSettings(eid, csrfNonce);
            } catch (NotFoundException e) {
                settings = null;
            }
        }
        if (settings == null) {
            try {
                remoteSettings = settingsLoader.getConfiguration(eid, sessionId, basePath, csrfNonce);
                remoteSettings.setReadOnly(readOnly);
                settings = remoteSettings.getSettings();
            } catch (SettingsLoaderException e) {
                throw e;
            }
        }
        if (settings != null) {
            settings.setEid(eid);
            settings.applyParentToChildren();
            if (devMode) {
                remoteSettings = new RemoteSettings();
                remoteSettings.setSettings(settings);
            }
            remoteSettings.setSessionId(sessionId);
            remoteSettings.setBasePath(basePath);
            remoteSettings.setReadOnly(readOnly);
            saveRemoteSettingsToSession(remoteSettings);
        }
        return settings;
    }

    public void saveSettings(String eid) throws SettingsLoaderException {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(eid);
        settingsLoader.saveConfiguration(remoteSettings);
        remoteSettings.setChangePending(false);
        remoteSettings.setRestartPending(true);
    }

    public void reloadSettings(String eid) throws SettingsLoaderException {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(eid);
        boolean restartPending = remoteSettings.isRestartPending();
        String sessionId = remoteSettings.getSessionId();
        String basePath = remoteSettings.getBasePath();
        String csrfNonce = remoteSettings.getCsrfNonce();
        Settings settings;
        try {
            remoteSettings = settingsLoader.getConfiguration(eid, sessionId, basePath, csrfNonce);
            settings = remoteSettings.getSettings();
        } catch (SettingsLoaderException e) {
            if (!devMode) {
                throw e;
            } else {
                remoteSettings = createMockSettings(eid);
                settings = remoteSettings.getSettings();
            }
        }
        settings.applyParentToChildren();
        settings.setEid(eid);
        remoteSettings.setSessionId(sessionId);
        remoteSettings.setBasePath(basePath);
        remoteSettings.setRestartPending(restartPending);
        saveRemoteSettingsToSession(remoteSettings);
    }

    public void revertToPreviousConfiguration(String eid) throws SettingsLoaderException {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(eid);
        settingsLoader.revertToPreviousConfiguration(remoteSettings);
        reloadSettings(eid);
        loadRemoteSettingsFromSession(eid).setRestartPending(true);
    }

    public void uploadConfigurationFile(String eid, String fileName, String fileText) throws SettingsLoaderException {
        settingsLoader.saveConfigurationFile(fileName, fileText, loadRemoteSettingsFromSession(eid));
        reloadSettings(eid);
        loadRemoteSettingsFromSession(eid).setRestartPending(true);
    }

    public void restartServer(String eid) throws SettingsLoaderException {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(eid);
        settingsLoader.restartServer(remoteSettings);
        remoteSettings.setRestartPending(false);
        remoteSettings.setJmxListenerChanged(false);
    }

    private Settings loadSettings(String eid, String csrfNonce) {
        RemoteSettings remoteSettings = loadRemoteSettings(eid);
        remoteSettings.setCsrfNonce(csrfNonce);
        return remoteSettings.getSettings();
    }

    public Settings loadSettings(String eid) {
        return loadRemoteSettings(eid).getSettings();
    }

    private RemoteSettings loadRemoteSettings(String eid) {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(eid);
        if (remoteSettings == null) {
            if (!devMode) {
                throw new NotFoundException();
            } else {
                remoteSettings = createMockSettings(eid);
                saveRemoteSettingsToSession(remoteSettings);
            }
        }
        return remoteSettings;
    }

    public void updateLocalSettings(Settings settings) {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(settings.getEid());
        if (remoteSettings == null) {
            throw new NotFoundException();
        }
        remoteSettings.setSettings(settings);
        remoteSettings.setChangePending(true);
    }

    public GeneralConfig loadGeneralConfig(String eid) {
        return loadSettings(eid).getConfiguration().getGeneralConfig();
    }

    public void saveGeneralConfig(String eid, GeneralConfig generalConfig) {
        Settings settings = loadSettings(eid);
        settings.getConfiguration().setGeneralConfig(generalConfig);
        changePending(eid);
    }

    public Environment loadEnvironment(String eid) {
        Environment environment = loadSettings(eid).getConfiguration().getEnvironment();
        if (environment == null) {
            throw new NotFoundException();
        }
        return environment;
    }

    public void saveEnvironment(String eid, Environment environment) {
        Settings settings = loadSettings(eid);
        settings.getConfiguration().setEnvironment(environment);
        changePending(eid);
    }

    public ContextContainer loadContextContainer(String eid) {
        return loadSettings(eid).getConfiguration().getContextContainer();
    }

    public void saveContextContainer(String eid, ContextContainer contextContainer) {
        Settings settings = loadSettings(eid);
        settings.getConfiguration().setContextContainer(contextContainer);
        changePending(eid);
    }

    public JspDefaults loadJspDefaults(String eid) {
        return loadSettings(eid).getConfiguration().getServerDefaults().getJspDefaults();
    }

    public void saveJspDefaults(String eid, JspDefaults jspDefaults) {
        Settings settings = loadSettings(eid);
        settings.getConfiguration().getServerDefaults().setJspDefaults(jspDefaults);
        changePending(eid);
    }

    public StaticDefaults loadStaticDefaults(String eid) {
        return loadSettings(eid).getConfiguration().getServerDefaults().getStaticDefaults();
    }

    public void saveStaticDefaults(String eid, StaticDefaults staticDefaults) {
        Settings settings = loadSettings(eid);
        settings.getConfiguration().getServerDefaults().setStaticDefaults(staticDefaults);
        changePending(eid);
    }

    public Set<DataSource> loadDataSources(String eid) {
        return loadSettings(eid).getDataSources();
    }

    public Set<DbcpDataSource> loadDbcpDataSources(String eid) {
        Set<DbcpDataSource> dataSources = new LinkedHashSet<DbcpDataSource>();
        for (DataSource dataSource : loadDataSources(eid)) {
            if (dataSource instanceof DbcpDataSource) {
                dataSources.add((DbcpDataSource) dataSource);
            }
        }
        return dataSources;
    }

    public Set<TomcatDataSource> loadTomcatDataSources(String eid) {
        Set<TomcatDataSource> dataSources = new LinkedHashSet<TomcatDataSource>();
        for (DataSource dataSource : loadDataSources(eid)) {
            if (dataSource instanceof TomcatDataSource) {
                dataSources.add((TomcatDataSource) dataSource);
            }
        }
        return dataSources;
    }

    public DataSource loadDataSource(String eid, String dataSourceName) {
        DataSource dataSource = findByHumanId(loadDataSources(eid), dataSourceName);
        if (dataSource == null) {
            throw new NotFoundException("DataSource not found for name '" + dataSourceName + "' with eid '" + eid + "'");
        }
        return dataSource;
    }

    public DbcpDataSource loadDbcpDataSource(String eid, String dataSourceName) {
        DataSource dataSource = loadDataSource(eid, dataSourceName);
        if (!(dataSource instanceof DbcpDataSource)) {
            throw new NotFoundException("DbcpDataSource not found for name '" + dataSourceName + "' with eid '" + eid + "'");
        }
        return (DbcpDataSource) dataSource;
    }

    public TomcatDataSource loadTomcatDataSource(String eid, String dataSourceName) {
        DataSource dataSource = loadDataSource(eid, dataSourceName);
        if (!(dataSource instanceof TomcatDataSource)) {
            throw new NotFoundException("TomcatDataSource not found for name '" + dataSourceName + "' with eid '" + eid + "'");
        }
        return (TomcatDataSource) dataSource;
    }

    public void addDataSource(String eid, DataSource dataSource) {
        loadDataSources(eid).add(dataSource);
        reindexDataSources(eid);
        changePending(eid);
    }

    public void saveDataSource(String eid, String oldDataSourceName, DataSource dataSource) {
        Connection oldConnection = loadDataSource(eid, oldDataSourceName).getConnection();
        dataSource.getConnection().bringForwardOldPassword(oldConnection);
        deleteDataSource(eid, oldDataSourceName);
        addDataSource(eid, dataSource);
    }

    public void deleteDataSource(String eid, String dataSourceName) {
        try {
            reindexDataSources(eid);
            loadDataSources(eid).remove(loadDataSource(eid, dataSourceName));
            changePending(eid);
        } catch (NotFoundException e) {
            // ignore
        }
    }

    public Set<Service> loadServices(String eid) {
        return loadSettings(eid).getServices();
    }

    public Service loadService(String eid, String serviceName) {
        Service service = findByHumanId(loadServices(eid), serviceName);
        if (service == null) {
            throw new NotFoundException("Service not found for name '" + serviceName + "' with eid '" + eid + "'");
        }
        return service;
    }

    public void addService(String eid, Service service) {
        loadServices(eid).add(service);
        reindexServices(eid);
        changePending(eid);
    }

    public void saveService(String eid, String oldServiceName, Service service) {
        deleteService(eid, oldServiceName);
        addService(eid, service);
    }

    public void deleteService(String eid, String serviceName) {
        try {
            reindexServices(eid);
            loadServices(eid).remove(loadService(eid, serviceName));
            changePending(eid);
        } catch (NotFoundException e) {
            // ignore
        }
    }

    public Set<Connector> loadConnectors(String eid, String serviceName) {
        return loadService(eid, serviceName).getConnectors();
    }

    public Set<AjpConnector> loadAjpConnectors(String eid, String serviceName) {
        Set<Connector> connectors = loadConnectors(eid, serviceName);
        Set<AjpConnector> ajpConnectors = new LinkedHashSet<AjpConnector>();
        for (Connector connector : connectors) {
            if (connector instanceof AjpConnector) {
                ajpConnectors.add((AjpConnector) connector);
            }
        }
        return ajpConnectors;
    }

    public Set<HttpConnector> loadHttpConnectors(String eid, String serviceName) {
        Set<Connector> connectors = loadConnectors(eid, serviceName);
        Set<HttpConnector> httpConnectors = new LinkedHashSet<HttpConnector>();
        for (Connector connector : connectors) {
            if (connector instanceof HttpConnector) {
                httpConnectors.add((HttpConnector) connector);
            }
        }
        return httpConnectors;
    }

    public Connector loadConnector(String eid, String serviceName, String connectorName) {
        Connector connector = findByHumanId(loadConnectors(eid, serviceName), connectorName);
        if (connector == null) {
            throw new NotFoundException("Connector not found for name '" + connectorName + "' on service '" + serviceName + "' with eid '" + eid
                + "'");
        }
        return connector;
    }

    public void addConnector(String eid, String serviceName, Connector connector) {
        loadConnectors(eid, serviceName).add(connector);
        reindexConnectors(eid, serviceName);
        changePending(eid);
    }

    public void saveConnector(String eid, String serviceName, String oldConnectorName, Connector connector) {
        deleteConnector(eid, serviceName, oldConnectorName);
        addConnector(eid, serviceName, connector);
    }

    public void deleteConnector(String eid, String serviceName, String connectorName) {
        try {
            reindexConnectors(eid, serviceName);
            loadConnectors(eid, serviceName).remove(loadConnector(eid, serviceName, connectorName));
            changePending(eid);
        } catch (NotFoundException e) {
            // ignore
        }
    }

    public Engine loadEngine(String eid, String serviceName) {
        return loadService(eid, serviceName).getEngine();
    }

    public void saveEngine(String eid, String serviceName, Engine engine) {
        loadService(eid, serviceName).setEngine(engine);
        reindexServices(eid);
        changePending(eid);
    }

    public Set<Host> loadHosts(String eid, String serviceName) {
        return loadEngine(eid, serviceName).getHosts();
    }

    public Host loadHost(String eid, String serviceName, String hostName) {
        Host host = findByHumanId(loadHosts(eid, serviceName), hostName);
        if (host == null) {
            throw new NotFoundException("Host not found for name '" + hostName + "' for service '" + serviceName + "' with eid '" + eid + "'");
        }
        return host;
    }

    public void addHost(String eid, String serviceName, Host host) {
        loadHosts(eid, serviceName).add(host);
        reindexHosts(eid, serviceName);
        changePending(eid);
    }

    public void saveHost(String eid, String serviceName, String oldHostName, Host host) {
        deleteHost(eid, serviceName, oldHostName);
        addHost(eid, serviceName, host);
    }

    public void deleteHost(String eid, String serviceName, String hostName) {
        try {
            reindexHosts(eid, serviceName);
            loadHosts(eid, serviceName).remove(loadHost(eid, serviceName, hostName));
            changePending(eid);
        } catch (NotFoundException e) {
            // ignore
        }
    }

    public Logging<Engine> loadLogging(String eid, String serviceName) {
        return loadEngine(eid, serviceName).getLogging();
    }

    public void saveLogging(String eid, String serviceName, Logging<Engine> logging) {
        loadEngine(eid, serviceName).setLogging(logging);
        reindexServices(eid);
        changePending(eid);
    }

    public boolean isChangePending(String eid) {
        return loadRemoteSettingsFromSession(eid).isChangePending();
    }

    public boolean isRestartPending(String eid) {
        return loadRemoteSettingsFromSession(eid).isRestartPending();
    }

    public boolean isReadOnly(String eid) {
        return loadRemoteSettingsFromSession(eid).isReadOnly();
    }

    private void reindexConnectors(String eid, String serviceName) {
        loadService(eid, serviceName).setConnectors(new LinkedHashSet<Connector>(loadConnectors(eid, serviceName)));
        reindexServices(eid);
    }

    private void reindexDataSources(String eid) {
        loadSettings(eid).setDataSources(new LinkedHashSet<DataSource>(loadDataSources(eid)));
    }

    private void reindexHosts(String eid, String serviceName) {
        loadEngine(eid, serviceName).setHosts(new LinkedHashSet<Host>(loadHosts(eid, serviceName)));
        reindexServices(eid);
    }

    private void reindexServices(String eid) {
        loadSettings(eid).setServices(new LinkedHashSet<Service>(loadServices(eid)));
    }

    private void changePending(String eid) {
        loadRemoteSettingsFromSession(eid).setChangePending(true);
    }

    private RemoteSettings loadRemoteSettingsFromSession(String eid) {
        return (RemoteSettings) getHttpSession().getAttribute(getSettingsSessionKey(eid));
    }

    private void saveRemoteSettingsToSession(RemoteSettings remoteSettings) {
        getHttpSession().setAttribute(getSettingsSessionKey(remoteSettings.getSettings().getEid()), remoteSettings);
    }

    private HttpSession getHttpSession() {
        return context.getHttpServletRequest().getSession();
    }

    private String getSettingsSessionKey(String id) {
        return Settings.class.getSimpleName() + "_" + id;
    }

    private <I extends Identity> I findByHumanId(Set<I> identifiedSet, String humanId) {
        for (I identity : identifiedSet) {
            if (humanId.equals(identity.getHumanId())) {
                return identity;
            }
        }
        return null;
    }

    /*
     * Only to be used when in dev mode
     */
    private RemoteSettings createMockSettings(String eid) {
        if (!devMode) {
            throw new UnsupportedOperationException("createMockSettings is only available in development mode");
        }

        Settings settings = new Settings();
        settings.setEid(eid);

        DbcpDataSource dataSource = new DbcpDataSource();
        dataSource.getGeneral().setJndiName("jdni:/employeeDb");
        dataSource.getConnection().setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.getConnection().setUrl("jdbc:hsqldb:mem:test");
        dataSource.getConnection().setUsername("sa");
        dataSource.getConnection().setPassword("");
        dataSource.getConnectionPool().setTestOnBorrow(false);
        settings.getDataSources().add(dataSource);

        // DataSource dataSource2 = new TomcatDataSource();
        // dataSource2.getGeneral().setJndiName("jdni:/employeeDb");
        // dataSource2.getConnection().setDriverClassName("org.hsqldb.jdbcDriver");
        // dataSource2.getConnection().setUrl("jdbc:hsqldb:mem:test");
        // dataSource2.getConnection().setUsername("sa");
        // dataSource2.getConnection().setPassword("");
        // settings.getDataSources().add(dataSource2);

        Service service = new Service();
        service.setName("Catalina");
        Connector httpConnector = new HttpConnector();
        httpConnector.setAddress("localhost");
        httpConnector.setPort(8080l);
        service.getConnectors().add(httpConnector);
        Connector ajpConnector = new AjpConnector();
        ajpConnector.setAddress("localhost");
        ajpConnector.setPort(8009l);
        service.getConnectors().add(ajpConnector);
        Host host = new Host();
        host.setName("localhost");
        host.setAppBase("webapps");
        service.getEngine().getHosts().add(host);
        service.getEngine().setDefaultHost(host.getName());
        service.getEngine().setName("my engine");
        settings.getServices().add(service);

        // Host host2 = new Host();
        // host2.setName("localhost");
        // service.getEngine().getHosts().add(host2);

        // settings.getConfiguration().setJvmOptions(null);

        settings.applyParentToChildren();

        RemoteSettings remoteSettings = new RemoteSettings();
        remoteSettings.setSettings(settings);
        remoteSettings.setSessionId("foo");
        remoteSettings.setBasePath("http://localhost/");
        remoteSettings.setReadOnly(true);

        // remoteSettings.setChangePending(true);
        // remoteSettings.setRestartPending(true);

        return remoteSettings;
    }

    public boolean isJmxListenerChanged(String settingsId) {
        return loadRemoteSettingsFromSession(settingsId).isJmxListenerChanged();
    }

    public void setJmxListenerChanged(String settingsId, boolean changed) {
        RemoteSettings remoteSettings = loadRemoteSettingsFromSession(settingsId);
        remoteSettings.setJmxListenerChanged(changed);
        saveRemoteSettingsToSession(remoteSettings);
    }
}
