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

package com.springsource.hq.plugin.tcserver.serverconfig.web.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.util.UriUtils;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;

public abstract class AbstractServicesControllerTests {

    private final SettingsService settingsService = createMock(SettingsService.class);

    private final ServicesController controller = new ServicesController(settingsService);

    private final ExtendedModelMap model = new ExtendedModelMap();

    private final BeanPropertyBindingResult binder = new BeanPropertyBindingResult(new Object(), "foo");

    private final Settings settings = new Settings();

    private final String settingsId;

    private final String encodedSettingsId;

    private final String serviceId;

    private final String encodedServiceId;

    private final String connectorId;

    private final String hostId;

    public AbstractServicesControllerTests(String settingsId, String serviceId, String connectorId, String hostId) {
        this.settingsId = settingsId;
        this.serviceId = serviceId;
        this.connectorId = connectorId;
        this.hostId = hostId;
        try {
            this.encodedSettingsId = UriUtils.encodePathSegment(settingsId, "UTF-8");
            this.encodedServiceId = UriUtils.encodePathSegment(serviceId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testServices_get() throws UnsupportedEncodingException {
        Set<Service> services = new HashSet<Service>();

        expect(settingsService.loadServices(settingsId)).andReturn(services);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.services(model, settingsId);
        verify(settingsService);

        assertEquals("services/services", view);
        assertEquals(services, model.get("services"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewService_get() throws UnsupportedEncodingException {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newService(model, settingsId);
        verify(settingsService);

        assertEquals("services/newService", view);
        assertTrue(model.get("newService") instanceof Service);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewService_post() throws UnsupportedEncodingException {
        NewService service = new NewService();
        service.setName("foo");
        service.getEngine().setName("bar");
        ((NewEngine) service.getEngine()).getNewHost().setName("localhost");
        ((NewEngine) service.getEngine()).getNewHost().setAppBase("webapps");

        HttpConnector httpConnector = new HttpConnector();
        httpConnector.setPort(8080L);
        AjpConnector ajpConnector = new AjpConnector();
        ajpConnector.setPort(8009L);

        settingsService.addService(settingsId, service);
        settingsService.addConnector(settingsId, service.getHumanId(), httpConnector);
        settingsService.addConnector(settingsId, service.getHumanId(), ajpConnector);

        replay(settingsService);
        String view = controller.newService(model, settingsId, service, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void testService_get() throws UnsupportedEncodingException {
        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/engine/",
            controller.service(model, settingsId, encodedServiceId));
    }

    @Test
    public void testDeleteService_get() throws UnsupportedEncodingException {
        Service service = new Service();
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.deleteService(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/deleteService", view);
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testDeleteServiceConfirm_delete() throws UnsupportedEncodingException {
        settingsService.deleteService(settingsId, serviceId);

        replay(settingsService);
        String view = controller.deleteServiceConfirm(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/", view);
        assertSame("deleted", model.get("message"));
    }

    @Test
    public void testConnectors_get() throws UnsupportedEncodingException {
        Service service = new Service();
        Set<HttpConnector> httpConnectors = new HashSet<HttpConnector>();
        Set<AjpConnector> ajpConnectors = new HashSet<AjpConnector>();

        expect(settingsService.loadHttpConnectors(settingsId, serviceId)).andReturn(httpConnectors);
        expect(settingsService.loadAjpConnectors(settingsId, serviceId)).andReturn(ajpConnectors);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.connectors(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/connectors", view);
        assertSame(httpConnectors, model.get("httpConnectors"));
        assertSame(ajpConnectors, model.get("ajpConnectors"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewAjpConnector_get() throws UnsupportedEncodingException {
        Service service = new Service();
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newAjpConnector(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/newAjpConnector", view);
        assertTrue(model.get("ajpConnector") instanceof AjpConnector);
        assertSame(settings, model.get("settings"));
        assertEquals(service, model.get("service"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewAjpConnector_post() throws UnsupportedEncodingException {
        AjpConnector ajpConnector = new AjpConnector();
        ajpConnector.setPort(8009l);

        settingsService.addConnector(settingsId, serviceId, ajpConnector);

        replay(settingsService);
        String view = controller.newAjpConnector(model, settingsId, encodedServiceId, ajpConnector, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void testAjpConnector_get() throws UnsupportedEncodingException {
        Service service = new Service();
        AjpConnector ajpConnector = new AjpConnector();

        expect(settingsService.loadConnector(settingsId, serviceId, connectorId)).andReturn(ajpConnector);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.ajpConnector(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("services/ajpConnector", view);
        assertSame(ajpConnector, model.get("ajpConnector"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testAjpConnector_put() throws UnsupportedEncodingException {
        AjpConnector ajpConnector = new AjpConnector();
        ajpConnector.setPort(8009l);

        settingsService.saveConnector(settingsId, serviceId, connectorId, ajpConnector);

        replay(settingsService);
        String view = controller.ajpConnector(model, settingsId, encodedServiceId, connectorId, ajpConnector, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testAjpConnectorDelete_get() throws UnsupportedEncodingException {
        Service service = new Service();
        AjpConnector ajpConnector = new AjpConnector();

        expect(settingsService.loadConnector(settingsId, serviceId, connectorId)).andReturn(ajpConnector);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.ajpConnectorDelete(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("services/deleteConnector", view);
        assertSame(ajpConnector, model.get("connector"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testAjpConnectorDeleteConfirm_delete() throws UnsupportedEncodingException {
        settingsService.deleteConnector(settingsId, serviceId, connectorId);

        replay(settingsService);
        String view = controller.ajpConnectorDeleteConfirm(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertSame("deleted", model.get("message"));
    }

    @Test
    public void testNewHttpConnector_get() throws UnsupportedEncodingException {
        Service service = new Service();
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newHttpConnector(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/newHttpConnector", view);
        assertTrue(model.get("httpConnector") instanceof HttpConnector);
        assertSame(settings, model.get("settings"));
        assertEquals(service, model.get("service"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewHttpConnector_post() throws UnsupportedEncodingException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.setPort(8080l);

        settingsService.addConnector(settingsId, serviceId, httpConnector);

        replay(settingsService);
        String view = controller.newHttpConnector(model, settingsId, encodedServiceId, httpConnector, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void testHttpConnector_get() throws UnsupportedEncodingException {
        Service service = new Service();
        HttpConnector httpConnector = new HttpConnector();

        expect(settingsService.loadConnector(settingsId, serviceId, connectorId)).andReturn(httpConnector);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.httpConnector(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("services/httpConnector", view);
        assertSame(httpConnector, model.get("httpConnector"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testHttpConnector_put() throws UnsupportedEncodingException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.setPort(8080l);

        settingsService.saveConnector(settingsId, serviceId, connectorId, httpConnector);

        replay(settingsService);
        String view = controller.httpConnector(model, settingsId, encodedServiceId, connectorId, httpConnector, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testHttpConnectorDelete_get() throws UnsupportedEncodingException {
        Service service = new Service();
        HttpConnector httpConnector = new HttpConnector();

        expect(settingsService.loadConnector(settingsId, serviceId, connectorId)).andReturn(httpConnector);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.httpConnectorDelete(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("services/deleteConnector", view);
        assertSame(httpConnector, model.get("connector"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testHttpConnectorDeleteConfirm_delete() throws UnsupportedEncodingException {
        settingsService.deleteConnector(settingsId, serviceId, connectorId);

        replay(settingsService);
        String view = controller.httpConnectorDeleteConfirm(model, settingsId, encodedServiceId, connectorId);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/connectors/", view);
        assertSame("deleted", model.get("message"));
    }

    @Test
    public void testEngine_get() throws UnsupportedEncodingException {
        Service service = new Service();
        Engine engine = new Engine();
        Set<Host> hosts = new HashSet<Host>();

        expect(settingsService.loadEngine(settingsId, serviceId)).andReturn(engine);
        expect(settingsService.loadHosts(settingsId, serviceId)).andReturn(hosts);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.engine(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/engine", view);
        assertSame(engine, model.get("engine"));
        assertSame(hosts, model.get("hosts"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testEngine_put() throws UnsupportedEncodingException {
        Engine engine = new Engine();
        engine.setName("foo");
        engine.setDefaultHost("localhost");
        Host host = new Host();
        host.setName("localhost");
        engine.getHosts().add(host);

        Engine newEngine = new Engine();
        newEngine.setName("bar");
        newEngine.setDefaultHost("localhost");

        expect(settingsService.loadEngine(settingsId, serviceId)).andReturn(engine);
        settingsService.saveEngine(settingsId, serviceId, engine);

        replay(settingsService);
        String view = controller.engine(model, settingsId, encodedServiceId, newEngine, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/engine/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testHosts_get() throws UnsupportedEncodingException {
        Service service = new Service();
        Set<Host> hosts = new HashSet<Host>();

        expect(settingsService.loadHosts(settingsId, serviceId)).andReturn(hosts);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.hosts(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/hosts", view);
        assertSame(hosts, model.get("hosts"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewHost_get() throws UnsupportedEncodingException {
        Service service = new Service();
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newHost(model, settingsId, encodedServiceId);
        verify(settingsService);

        assertEquals("services/newHost", view);
        assertTrue(model.get("host") instanceof Host);
        assertSame(settings, model.get("settings"));
        assertEquals(service, model.get("service"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewHost_post() throws UnsupportedEncodingException {
        Host host = new Host();
        host.setName("localhost");
        host.setAppBase("webapps");

        settingsService.addHost(settingsId, serviceId, host);

        replay(settingsService);
        String view = controller.newHost(model, settingsId, encodedServiceId, host, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/engine/hosts/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void host_get() throws UnsupportedEncodingException {
        Service service = new Service();
        Host host = new Host();

        expect(settingsService.loadHost(settingsId, serviceId, hostId)).andReturn(host);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.host(model, settingsId, encodedServiceId, hostId);
        verify(settingsService);

        assertEquals("services/host", view);
        assertSame(host, model.get("host"));
        assertSame(settings, model.get("settings"));
        assertEquals(service, model.get("service"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void host_put() throws UnsupportedEncodingException {
        Host host = new Host();
        host.setName("localhost");
        host.setAppBase("webapps");

        settingsService.saveHost(settingsId, serviceId, hostId, host);

        replay(settingsService);
        String view = controller.host(model, settingsId, encodedServiceId, hostId, host, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/engine/hosts/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testHostDelete_get() throws UnsupportedEncodingException {
        Service service = new Service();
        Host host = new Host();

        expect(settingsService.loadHost(settingsId, serviceId, hostId)).andReturn(host);
        expect(settingsService.loadService(settingsId, serviceId)).andReturn(service);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.hostDelete(model, settingsId, encodedServiceId, hostId);
        verify(settingsService);

        assertEquals("services/deleteHost", view);
        assertSame(host, model.get("host"));
        assertEquals(service, model.get("service"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testHostDeleteConfirm_delete() throws UnsupportedEncodingException {
        settingsService.deleteHost(settingsId, serviceId, hostId);

        replay(settingsService);
        String view = controller.hostDeleteConfirm(model, settingsId, encodedServiceId, hostId);
        verify(settingsService);

        assertEquals("redirect:/app/" + encodedSettingsId + "/services/" + encodedServiceId + "/engine/hosts/", view);
        assertSame("deleted", model.get("message"));
    }
}
