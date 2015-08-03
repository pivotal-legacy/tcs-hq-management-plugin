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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
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
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.HttpServletContextHolder;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.RemoteSettings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * Unit tests for {@link HttpSessionBackedSettingsService}
 */
public class HttpSessionBackedSettingsServiceTests {

    private HttpSessionBackedSettingsService service;

    private HttpServletContextHolder context;

    private SettingsLoader settingsLoader;

    private HttpSession session;

    private String eid;

    private RemoteSettings remoteSettings;

    private Settings settings;

    @Before
    public void setup() {
        context = new HttpServletContextHolder();
        context.setHttpServletRequest(new MockHttpServletRequest());
        context.setHttpServletResponse(new MockHttpServletResponse());
        session = context.getHttpServletRequest().getSession();
        settingsLoader = createMock(SettingsLoader.class);
        service = new HttpSessionBackedSettingsService(context, settingsLoader, Boolean.FALSE);
        eid = "eid";
        remoteSettings = createMockSettings(eid);
        settings = remoteSettings.getSettings();
        session.setAttribute(getSettingsSessionKey(eid), remoteSettings);
    }

    @Test
    public void testLoadSettings_fromHQ() throws SettingsLoaderException {
        session.setAttribute(getSettingsSessionKey(eid), null);
        String sessionId = "session";
        String basePath = "basePath";
        RemoteSettings remoteSettings = new RemoteSettings();
        Settings settings = new Settings();
        remoteSettings.setSettings(settings);

        assertFalse(remoteSettings.isReadOnly());

        expect(settingsLoader.getConfiguration(eid, sessionId, basePath, "nonce")).andReturn(remoteSettings);

        replay(settingsLoader);
        Settings s = service.loadSettings(eid, sessionId, "nonce", basePath, true);
        verify(settingsLoader);

        assertSame(settings, s);
        assertSame(remoteSettings, session.getAttribute(getSettingsSessionKey(eid)));
        assertEquals(eid, settings.getEid());
        assertEquals(sessionId, remoteSettings.getSessionId());
        assertEquals(basePath, remoteSettings.getBasePath());
        assertTrue(remoteSettings.isReadOnly());
    }

    @Test
    public void testSaveSettings() throws SettingsLoaderException {
        remoteSettings.setChangePending(true);

        settingsLoader.saveConfiguration(remoteSettings);

        replay(settingsLoader);
        service.saveSettings(eid);
        verify(settingsLoader);

        assertFalse(remoteSettings.isChangePending());
    }

    @Test
    public void testReloadSettings() throws SettingsLoaderException {
        remoteSettings.setChangePending(true);
        RemoteSettings newRemoteSettings = createMockSettings(eid);

        expect(settingsLoader.getConfiguration(eid, remoteSettings.getSessionId(), remoteSettings.getBasePath(), "nonce")).andReturn(
            newRemoteSettings);

        replay(settingsLoader);
        service.reloadSettings(eid);
        verify(settingsLoader);

        assertFalse(service.isChangePending(eid));
        assertSame(newRemoteSettings, session.getAttribute(getSettingsSessionKey(eid)));
        assertEquals(eid, newRemoteSettings.getSettings().getEid());
        assertEquals(remoteSettings.getSessionId(), newRemoteSettings.getSessionId());
        assertEquals(remoteSettings.getBasePath(), newRemoteSettings.getBasePath());
    }

    @Test
    public void testUploadConfigurationFile() throws SettingsLoaderException {
        String fileName = "/conf/server.xml";
        remoteSettings.setChangePending(true);
        RemoteSettings newRemoteSettings = createMockSettings(eid);

        settingsLoader.saveConfigurationFile(fileName, "", remoteSettings);
        expect(settingsLoader.getConfiguration(eid, remoteSettings.getSessionId(), remoteSettings.getBasePath(), "nonce")).andReturn(
            newRemoteSettings);

        replay(settingsLoader);
        service.uploadConfigurationFile(eid, fileName, "");
        verify(settingsLoader);

        assertFalse(service.isChangePending(eid));
        assertTrue(service.isRestartPending(eid));
        assertSame(newRemoteSettings, session.getAttribute(getSettingsSessionKey(eid)));
        assertEquals(eid, newRemoteSettings.getSettings().getEid());
        assertEquals(remoteSettings.getSessionId(), newRemoteSettings.getSessionId());
        assertEquals(remoteSettings.getBasePath(), newRemoteSettings.getBasePath());
    }

    @Test
    public void testRestartServer() throws SettingsLoaderException {
        remoteSettings.setChangePending(true);
        remoteSettings.setRestartPending(true);

        settingsLoader.restartServer(remoteSettings);

        replay(settingsLoader);
        service.restartServer(eid);
        verify(settingsLoader);

        assertTrue(remoteSettings.isChangePending());
        assertFalse(remoteSettings.isRestartPending());
    }

    @Test
    public void testLoadSettings_localCache() {
        assertSame(settings, service.loadSettings(eid));
    }

    @Test
    public void testLoadGeneralConfig() {
        assertSame(settings.getConfiguration().getGeneralConfig(), service.loadGeneralConfig(eid));
    }

    @Test
    public void testSaveGeneralConfig() {
        GeneralConfig generalConfig = new GeneralConfig();
        assertFalse(service.isChangePending(eid));
        service.saveGeneralConfig(eid, generalConfig);
        assertSame(generalConfig, settings.getConfiguration().getGeneralConfig());
        assertTrue(remoteSettings.isChangePending());
    }

    @Test
    public void testLoadEnvironment() {
        assertSame(settings.getConfiguration().getEnvironment(), service.loadEnvironment(eid));
    }

    @Test
    public void testSaveEnvironment() {
        Environment environment = new Environment();
        assertFalse(service.isChangePending(eid));
        service.saveEnvironment(eid, environment);
        assertSame(environment, settings.getConfiguration().getEnvironment());
        assertTrue(service.isChangePending(eid));
    }

    @Test
    public void testLoadContextContainer() {
        assertSame(settings.getConfiguration().getContextContainer(), service.loadContextContainer(eid));
    }

    @Test
    public void testSaveContextContainer() {
        ContextContainer contextContainer = new ContextContainer();
        assertFalse(service.isChangePending(eid));
        service.saveContextContainer(eid, contextContainer);
        assertSame(contextContainer, settings.getConfiguration().getContextContainer());
        assertTrue(service.isChangePending(eid));
    }

    @Test
    public void testLoadJspDefaults() {
        assertSame(settings.getConfiguration().getServerDefaults().getJspDefaults(), service.loadJspDefaults(eid));
    }

    @Test
    public void testSaveJspDefaults() {
        JspDefaults jspDefaults = new JspDefaults();
        assertFalse(service.isChangePending(eid));
        service.saveJspDefaults(eid, jspDefaults);
        assertSame(jspDefaults, settings.getConfiguration().getServerDefaults().getJspDefaults());
        assertTrue(service.isChangePending(eid));
    }

    @Test
    public void testLoadStaticDefaults() {
        assertSame(settings.getConfiguration().getServerDefaults().getStaticDefaults(), service.loadStaticDefaults(eid));
    }

    @Test
    public void testSaveStaticDefaults() {
        StaticDefaults staticDefaults = new StaticDefaults();
        assertFalse(service.isChangePending(eid));
        service.saveStaticDefaults(eid, staticDefaults);
        assertSame(staticDefaults, settings.getConfiguration().getServerDefaults().getStaticDefaults());
        assertTrue(service.isChangePending(eid));
    }

    @Test
    public void testLoadDataSources() {
        assertEquals(new LinkedHashSet<DataSource>(settings.getDataSources()), new LinkedHashSet<DataSource>(service.loadDataSources(eid)));
    }

    @Test
    public void testLoadDbcpDataSources() {
        assertArrayEquals(new Object[] { findByHumanId(settings.getDataSources(), "jndi:/dbcpDb") }, service.loadDbcpDataSources(eid).toArray());
    }

    @Test
    public void testLoadTomcatDataSources() {
        assertArrayEquals(new Object[] { findByHumanId(settings.getDataSources(), "jndi:/tomcatDb") }, service.loadTomcatDataSources(eid).toArray());
    }

    @Test
    public void testLoadDataSource() {
        assertSame(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"), service.loadDataSource(eid, "jndi:/tomcatDb"));
    }

    @Test
    public void testLoadDbcpDataSource() {
        assertSame(findByHumanId(settings.getDataSources(), "jndi:/dbcpDb"), service.loadDbcpDataSource(eid, "jndi:/dbcpDb"));
    }

    @Test
    public void testLoadTomcatDataSource() {
        assertSame(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"), service.loadTomcatDataSource(eid, "jndi:/tomcatDb"));
    }

    @Test
    public void testLoadDataSourcesPreserveOrdering() {
        Set<DataSource> dataSources = settings.getDataSources();
        assertEquals("There should only be two data sources, but there are " + dataSources.size(), dataSources.size(), 2);

        int i = 0;
        dataSources.toArray();
        for (Iterator<DataSource> iter = dataSources.iterator(); iter.hasNext();) {
            DataSource ds = iter.next();
            if (i == 1) {
                assertTrue("The Tomcat DB datasource should be second in the array", "jndi:/tomcatDb".equals(ds.getGeneral().getJndiName()));
            } else if (i == 0) {
                assertTrue("The DBCP datasource should be first in the array", "jndi:/dbcpDb".equals(ds.getGeneral().getJndiName()));
            }
            ++i;
        }
    }

    @Test
    public void testAddDataSource() {
        DataSource dataSource = new TomcatDataSource();
        dataSource.getGeneral().setJndiName("foo");

        assertFalse(settings.getDataSources().contains(dataSource));
        assertNull(findByHumanId(settings.getDataSources(), "foo"));
        assertFalse(remoteSettings.isChangePending());

        service.addDataSource(eid, dataSource);

        assertTrue(settings.getDataSources().contains(dataSource));
        assertSame(dataSource, findByHumanId(settings.getDataSources(), "foo"));
        assertTrue(remoteSettings.isChangePending());
    }

    @Test
    public void testSaveDataSource() {
        DataSource dataSource = new TomcatDataSource();
        dataSource.getGeneral().setJndiName("jndi:/tomcat");

        assertFalse(remoteSettings.isChangePending());
        assertNotNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));

        service.saveDataSource(eid, "jndi:/tomcatDb", dataSource);

        assertSame(dataSource, findByHumanId(settings.getDataSources(), "jndi:/tomcat"));
        assertTrue(remoteSettings.isChangePending());
        assertNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));
    }

    @Test
    public void testDeleteDataSource() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));
        service.deleteDataSource(eid, "jndi:/tomcatDb");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));
    }

    @Test
    public void testDeleteDataSource_Modified() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));
        DataSource ds = service.loadDataSource(eid, "jndi:/tomcatDb");
        ds.getConnection().setUrl("db:url");
        service.deleteDataSource(eid, "jndi:/tomcatDb");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(settings.getDataSources(), "jndi:/tomcatDb"));
    }

    @Test
    public void testLoadServices() {
        assertSame(settings.getServices(), service.loadServices(eid));
    }

    @Test
    public void testLoadService() {
        assertSame(findByHumanId(settings.getServices(), "Catalina"), service.loadService(eid, "Catalina"));
    }

    @Test
    public void testAddService() {
        Service s = new Service();
        s.setName("foo");

        assertFalse(settings.getServices().contains(s));
        assertNull(findByHumanId(settings.getServices(), "foo"));
        assertFalse(remoteSettings.isChangePending());

        service.addService(eid, s);

        assertTrue(settings.getServices().contains(s));
        assertSame(s, findByHumanId(settings.getServices(), "foo"));
        assertTrue(remoteSettings.isChangePending());
    }

    @Test
    public void testSaveService() {
        Service s = new Service();
        s.setName("foo");

        assertFalse(remoteSettings.isChangePending());
        assertNotNull(findByHumanId(settings.getServices(), "Catalina"));

        service.saveService(eid, "Catalina", s);

        assertSame(s, findByHumanId(settings.getServices(), "foo"));
        assertTrue(remoteSettings.isChangePending());
        assertNull(findByHumanId(settings.getServices(), "Catalina"));
    }

    @Test
    public void testDeleteService() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(settings.getServices(), "Catalina"));
        service.deleteService(eid, "Catalina");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(settings.getServices(), "Catalina"));
    }

    @Test
    public void testDeleteService_Modified() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(settings.getServices(), "Catalina"));
        Service catalina = service.loadService(eid, "Catalina");
        catalina.getEngine().setJvmRoute("fooJvmRotue");
        service.deleteService(eid, "Catalina");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(settings.getServices(), "Catalina"));
    }

    @Test
    public void testLoadConnectors() {
        assertSame(findByHumanId(settings.getServices(), "Catalina").getConnectors(), service.loadConnectors(eid, "Catalina"));
    }

    @Test
    public void testLoadAjpConnectors() {
        Set<Connector> connectors = new LinkedHashSet<Connector>();
        connectors.add(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8009"));
        assertEquals(connectors, service.loadAjpConnectors(eid, "Catalina"));
    }

    @Test
    public void testLoadHttpConnectors() {
        Set<Connector> connectors = new LinkedHashSet<Connector>();
        connectors.add(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
        assertEquals(connectors, service.loadHttpConnectors(eid, "Catalina"));
    }

    @Test
    public void testLoadConnector() {
        assertSame(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"),
            service.loadConnector(eid, "Catalina", "localhost:8080"));
    }

    @Test
    public void testAddConnector() {
        Connector connector = new HttpConnector();
        connector.setPort(8080l);

        assertFalse(findByHumanId(settings.getServices(), "Catalina").getConnectors().contains(connector));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), ":8080"));
        assertFalse(remoteSettings.isChangePending());

        service.addConnector(eid, "Catalina", connector);

        assertTrue(findByHumanId(settings.getServices(), "Catalina").getConnectors().contains(connector));
        assertSame(connector, findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), ":8080"));
        assertTrue(remoteSettings.isChangePending());
    }

    @Test
    public void testSaveConnector() {
        Connector connector = new HttpConnector();
        connector.setPort(8080l);

        assertFalse(remoteSettings.isChangePending());
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));

        service.saveConnector(eid, "Catalina", "localhost:8080", connector);

        assertSame(connector, findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), ":8080"));
        assertTrue(remoteSettings.isChangePending());
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
    }

    @Test
    public void testDeleteConnector() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
        service.deleteConnector(eid, "Catalina", "localhost:8080");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
    }

    @Test
    public void testDeleteConnector_Modified() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
        Connector connector = service.loadConnector(eid, "Catalina", "localhost:8080");
        connector.setScheme("https");
        service.deleteConnector(eid, "Catalina", "localhost:8080");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getConnectors(), "localhost:8080"));
    }

    @Test
    public void testLoadEngine() {
        assertSame(findByHumanId(settings.getServices(), "Catalina").getEngine(), service.loadEngine(eid, "Catalina"));
    }

    @Test
    public void testSaveEngine() {
        Engine engine = new Engine();
        assertFalse(service.isChangePending(eid));
        service.saveEngine(eid, "Catalina", engine);
        assertSame(engine, findByHumanId(settings.getServices(), "Catalina").getEngine());
        assertTrue(service.isChangePending(eid));
    }

    @Test
    public void testLoadHosts() {
        assertSame(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), service.loadHosts(eid, "Catalina"));
    }

    @Test
    public void testLoadHost() {
        assertSame(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"),
            service.loadHost(eid, "Catalina", "localhost"));
    }

    @Test
    public void testAddHost() {
        Host host = new Host();
        host.setName("www.springsource.com");

        assertFalse(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts().contains(host));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "www.springsource.com"));
        assertFalse(remoteSettings.isChangePending());

        service.addHost(eid, "Catalina", host);

        assertTrue(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts().contains(host));
        assertSame(host, findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "www.springsource.com"));
        assertTrue(remoteSettings.isChangePending());
    }

    @Test
    public void testSaveHost() {
        Host host = new Host();
        host.setName("www.springsource.com");

        assertFalse(remoteSettings.isChangePending());
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));

        service.saveHost(eid, "Catalina", "localhost", host);

        assertSame(host, findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "www.springsource.com"));
        assertTrue(remoteSettings.isChangePending());
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));
    }

    @Test
    public void testDeleteHost() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));
        service.deleteHost(eid, "Catalina", "localhost");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));
    }

    @Test
    public void testDeleteHost_Modified() {
        assertFalse(service.isChangePending(eid));
        assertNotNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));
        Host host = service.loadHost(eid, "Catalina", "localhost");
        host.setAppBase("fake/file/path");
        service.deleteHost(eid, "Catalina", "localhost");
        assertTrue(service.isChangePending(eid));
        assertNull(findByHumanId(findByHumanId(settings.getServices(), "Catalina").getEngine().getHosts(), "localhost"));
    }

    @Test
    public void testLoadLogging() {
        assertSame(findByHumanId(settings.getServices(), "Catalina").getEngine().getLogging(), service.loadLogging(eid, "Catalina"));
    }

    @Test
    public void testSaveLogging() {
        Logging<Engine> logging = new Logging<Engine>();
        assertFalse(service.isChangePending(eid));
        service.saveLogging(eid, "Catalina", logging);
        assertSame(logging, findByHumanId(settings.getServices(), "Catalina").getEngine().getLogging());
        assertTrue(service.isChangePending(eid));
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

    private RemoteSettings createMockSettings(String eid) {
        Settings settings = new Settings();
        settings.setEid(eid);

        DataSource dataSource = new DbcpDataSource();
        dataSource.getGeneral().setJndiName("jndi:/dbcpDb");
        dataSource.getConnection().setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.getConnection().setUrl("jdbc:hsqldb:mem:test");
        dataSource.getConnection().setUsername("sa");
        dataSource.getConnection().setPassword("");
        settings.getDataSources().add(dataSource);

        DataSource dataSource2 = new TomcatDataSource();
        dataSource2.getGeneral().setJndiName("jndi:/tomcatDb");
        dataSource2.getConnection().setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource2.getConnection().setUrl("jdbc:hsqldb:mem:test");
        dataSource2.getConnection().setUsername("sa");
        dataSource2.getConnection().setPassword("");
        settings.getDataSources().add(dataSource2);

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
        remoteSettings.setCsrfNonce("nonce");

        // remoteSettings.setChangePending(true);
        // remoteSettings.setRestartPending(true);

        return remoteSettings;
    }

}
