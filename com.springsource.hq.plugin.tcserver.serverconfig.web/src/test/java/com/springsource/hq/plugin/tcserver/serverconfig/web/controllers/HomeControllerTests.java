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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * Unit tests for {@HomeController}.
 */
public class HomeControllerTests {

    private HomeController controller;

    private SettingsService settingsService;

    private ExtendedModelMap model;

    private String settingsId;

    private Settings settings;

    private String username;

    @Before
    public void setup() {
        settingsService = createMock(SettingsService.class);
        controller = new HomeController(settingsService);
        model = new ExtendedModelMap();
        settingsId = "eid";
        settings = new Settings();
        username = "hqadmin";
    }

    @Test
    public void testEnter() throws UnsupportedEncodingException, SettingsLoaderException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setLocalPort(7443);
        request.setScheme("https");
        request.setLocalName("localhost");
        expect(settingsService.loadSettings(settingsId, "sessionId", "nonce", "https://localhost:7443", true)).andReturn(settings);

        replay(settingsService);
        String view = controller.enter("sessionId", settingsId, "true", username, "nonce", model, request);
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
    }

    @Test
    public void testIndex() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.index(model, settingsId);
        verify(settingsService);

        assertEquals("home/home", view);
        assertNotNull(model.get("org.springframework.validation.BindingResult.settings"));
        assertFalse((Boolean) model.get("hasErrors"));
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testSave() throws UnsupportedEncodingException {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.save(model, settingsId);
        verify(settingsService);

        assertEquals("home/save", view);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testSaveConfirmed() throws SettingsLoaderException, UnsupportedEncodingException {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        settingsService.saveSettings(settingsId);

        replay(settingsService);
        String view = controller.saveConfirmed(model, settingsId);
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
        assertEquals("saved-to-server", model.get("message"));
    }

    @Test
    public void testRevert() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.revert(model, settingsId);
        verify(settingsService);

        assertEquals("home/revert", view);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testRevertConfirmed() throws SettingsLoaderException, UnsupportedEncodingException {
        settingsService.reloadSettings(settingsId);

        replay(settingsService);
        String view = controller.revertConfirmed(model, settingsId);
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
        assertEquals("reloaded", model.get("message"));
    }

    @Test
    public void testRevertToPreviousConfiguration() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.revertToPreviousConfiguration(model, settingsId);
        verify(settingsService);

        assertEquals("home/revertToPreviousConfiguration", view);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testRevertToPreviousConfigurationConfirmed() throws UnsupportedEncodingException, SettingsLoaderException {
        settingsService.revertToPreviousConfiguration(settingsId);

        replay(settingsService);
        String view = controller.revertToPreviousConfigurationConfirmed(model, settingsId);
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
        assertEquals("reverted", model.get("message"));
    }

    @Test
    public void testRestart() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.restart(model, settingsId);
        verify(settingsService);

        assertEquals("home/restart", view);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testRestartConfirmed() throws SettingsLoaderException, UnsupportedEncodingException {
        settingsService.restartServer(settingsId);

        replay(settingsService);
        String view = controller.restartConfirmed(model, settingsId);
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
        assertEquals("restarted", model.get("message"));
    }

    @Test
    public void testUploadServerXml() throws IOException, SettingsLoaderException {
        String fileName = "/conf/server.xml";
        settingsService.uploadConfigurationFile(settingsId, fileName, "");

        replay(settingsService);
        String view = controller.uploadConfigurationFile(model, settingsId, fileName, new MockMultipartFile("file", "".getBytes()));
        verify(settingsService);

        assertEquals("redirect:/app/eid/", view);
        assertEquals("saved-to-server", model.get("message"));
    }

}
