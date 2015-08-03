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

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;

/**
 * Unit tests for {@ConfigurationController}
 * 
 */
public class ConfigurationControllerWithPercentsTests {

    private ConfigurationController controller;

    private SettingsService settingsService;

    private ExtendedModelMap model;

    private BeanPropertyBindingResult binder;

    private String settingsIdNotEscaped;

    private String settingsIdEscaped;

    private Settings settings;

    @Before
    public void setup() {
        settingsService = createMock(SettingsService.class);
        controller = new ConfigurationController(settingsService);
        model = new ExtendedModelMap();
        binder = new BeanPropertyBindingResult(new Object(), "foo");
        settingsIdNotEscaped = "eid%with%percents";
        settingsIdEscaped = "eid%25with%25percents";
        settings = new Settings();
    }

    @Test
    public void general_get() {
        GeneralConfig generalConfig = new GeneralConfig();

        expect(settingsService.loadGeneralConfig(settingsIdNotEscaped)).andReturn(generalConfig);
        expect(settingsService.loadSettings(settingsIdNotEscaped)).andReturn(settings);
        expect(settingsService.isChangePending(settingsIdNotEscaped)).andReturn(false);
        expect(settingsService.isRestartPending(settingsIdNotEscaped)).andReturn(false);

        replay(settingsService);
        String view = controller.general(model, settingsIdNotEscaped);
        verify(settingsService);

        assertEquals("configuration/general", view);
        assertSame(generalConfig, model.get("generalConfig"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void general_put() throws UnsupportedEncodingException {
        GeneralConfig generalConfig = new GeneralConfig();

        expect(settingsService.loadGeneralConfig(settingsIdNotEscaped)).andReturn(generalConfig);
        settingsService.setJmxListenerChanged(settingsIdNotEscaped, false);
        settingsService.saveGeneralConfig(settingsIdNotEscaped, generalConfig);

        replay(settingsService);
        String view = controller.general(model, settingsIdNotEscaped, generalConfig, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + settingsIdEscaped + "/configuration/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void startup_get() {
        Environment environment = new Environment();

        expect(settingsService.loadEnvironment(settingsIdNotEscaped)).andReturn(environment);
        expect(settingsService.loadSettings(settingsIdNotEscaped)).andReturn(settings);
        expect(settingsService.isChangePending(settingsIdNotEscaped)).andReturn(false);
        expect(settingsService.isRestartPending(settingsIdNotEscaped)).andReturn(false);

        replay(settingsService);
        String view = controller.startup(model, settingsIdNotEscaped);
        verify(settingsService);

        assertEquals("configuration/startup", view);
        assertSame(environment, model.get("environment"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testStartup_put() throws UnsupportedEncodingException {
        Environment environment = new Environment();

        settingsService.saveEnvironment(settingsIdNotEscaped, environment);

        replay(settingsService);
        String view = controller.startup(model, settingsIdNotEscaped, environment, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + settingsIdEscaped + "/configuration/startup/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testContainer_get() {
        ContextContainer contextContainer = new ContextContainer();

        expect(settingsService.loadContextContainer(settingsIdNotEscaped)).andReturn(contextContainer);
        expect(settingsService.loadSettings(settingsIdNotEscaped)).andReturn(settings);
        expect(settingsService.isChangePending(settingsIdNotEscaped)).andReturn(false);
        expect(settingsService.isRestartPending(settingsIdNotEscaped)).andReturn(false);

        replay(settingsService);
        String view = controller.container(model, settingsIdNotEscaped);
        verify(settingsService);

        assertEquals("configuration/container", view);
        assertSame(contextContainer, model.get("contextContainer"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testContainer_put() throws UnsupportedEncodingException {
        ContextContainer contextContainer = new ContextContainer();

        settingsService.saveContextContainer(settingsIdNotEscaped, contextContainer);

        replay(settingsService);
        String view = controller.container(model, settingsIdNotEscaped, contextContainer, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + settingsIdEscaped + "/configuration/container/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testJspDefaults_get() {
        JspDefaults jspDefaults = new JspDefaults();

        expect(settingsService.loadJspDefaults(settingsIdNotEscaped)).andReturn(jspDefaults);
        expect(settingsService.loadSettings(settingsIdNotEscaped)).andReturn(settings);
        expect(settingsService.isChangePending(settingsIdNotEscaped)).andReturn(false);
        expect(settingsService.isRestartPending(settingsIdNotEscaped)).andReturn(false);

        replay(settingsService);
        String view = controller.jspDefaults(model, settingsIdNotEscaped);
        verify(settingsService);

        assertEquals("configuration/defaultsJsp", view);
        assertSame(jspDefaults, model.get("jspDefaults"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testJspDefaults_put() throws UnsupportedEncodingException {
        JspDefaults jspDefaults = new JspDefaults();

        settingsService.saveJspDefaults(settingsIdNotEscaped, jspDefaults);

        replay(settingsService);
        String view = controller.jspDefaults(model, settingsIdNotEscaped, jspDefaults, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + settingsIdEscaped + "/configuration/defaults-jsp/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testStaticDefaults_get() {
        StaticDefaults staticDefaults = new StaticDefaults();

        expect(settingsService.loadStaticDefaults(settingsIdNotEscaped)).andReturn(staticDefaults);
        expect(settingsService.loadSettings(settingsIdNotEscaped)).andReturn(settings);
        expect(settingsService.isChangePending(settingsIdNotEscaped)).andReturn(false);
        expect(settingsService.isRestartPending(settingsIdNotEscaped)).andReturn(false);

        replay(settingsService);
        String view = controller.staticDefaults(model, settingsIdNotEscaped);
        verify(settingsService);

        assertEquals("configuration/defaultsStatic", view);
        assertSame(staticDefaults, model.get("staticDefaults"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testStaticDefaults_put() throws UnsupportedEncodingException {
        StaticDefaults staticDefaults = new StaticDefaults();

        settingsService.saveStaticDefaults(settingsIdNotEscaped, staticDefaults);

        replay(settingsService);
        String view = controller.staticDefaults(model, settingsIdNotEscaped, staticDefaults, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + settingsIdEscaped + "/configuration/defaults-static/", view);
        assertEquals("saved", model.get("message"));
    }

}
