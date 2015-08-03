/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;

/**
 * Unit tests for {@JdbcResourceController}
 */
public abstract class AbstractJdbcResourceControllerTests {

    private final SettingsService settingsService = createMock(SettingsService.class);

    private final JdbcResourceController controller = new JdbcResourceController(settingsService);

    private final ExtendedModelMap model = new ExtendedModelMap();

    private final BeanPropertyBindingResult binder = new BeanPropertyBindingResult(new Object(), "foo");

    private final Settings settings = new Settings();

    private final String settingsId;

    private final String encodedSettingsId;

    private final String dataSourceId;

    private final String encodedDataSourceId;

    public AbstractJdbcResourceControllerTests(String settingsId, String dataSourceId) {
        this.settingsId = settingsId;
        this.dataSourceId = dataSourceId;
        try {
            this.encodedSettingsId = UriUtils.encodePathSegment(settingsId, "UTF-8");
            this.encodedDataSourceId = UriUtils.encodePathSegment(dataSourceId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testListDataSources_get() {
        Set<DbcpDataSource> dbcpDataSources = new HashSet<DbcpDataSource>();
        Set<TomcatDataSource> tomcatDataSources = new HashSet<TomcatDataSource>();

        expect(settingsService.loadDbcpDataSources(settingsId)).andReturn(dbcpDataSources);
        expect(settingsService.loadTomcatDataSources(settingsId)).andReturn(tomcatDataSources);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.listDataSources(model, settingsId);
        verify(settingsService);

        assertEquals("resources/dataSources", view);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewTomcatDataSource_get() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newTomcatDataSource(model, settingsId);
        verify(settingsService);

        assertEquals("resources/newTomcatDataSource", view);
        assertTrue(model.get("tomcatDataSource") instanceof TomcatDataSource);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewTomcatDataSource_post() throws UnsupportedEncodingException {
        TomcatDataSource dataSource = new TomcatDataSource();
        dataSource.getGeneral().setJndiName("name");
        dataSource.getConnection().setDriverClassName("driver");
        dataSource.getConnection().setUrl("url");
        dataSource.getConnection().setUsername("");
        dataSource.getConnection().setPassword("");

        settingsService.addDataSource(settingsId, dataSource);

        replay(settingsService);
        String view = controller.newTomcatDataSource(model, settingsId, dataSource, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + this.encodedSettingsId + "/resources/jdbc/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void testNewDbcpDataSource_get() {
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.newDbcpDataSource(model, settingsId);
        verify(settingsService);

        assertEquals("resources/newDbcpDataSource", view);
        assertTrue(model.get("dbcpDataSource") instanceof DbcpDataSource);
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testNewDbcpDataSource_post() throws UnsupportedEncodingException {
        DbcpDataSource dataSource = new DbcpDataSource();
        dataSource.getGeneral().setJndiName("name");
        dataSource.getConnection().setDriverClassName("driver");
        dataSource.getConnection().setUrl("url");
        dataSource.getConnection().setUsername("");
        dataSource.getConnection().setPassword("");
        dataSource.getConnectionPool().setTestOnBorrow(false);

        settingsService.addDataSource(settingsId, dataSource);

        replay(settingsService);
        String view = controller.newDbcpDataSource(model, settingsId, dataSource, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + this.encodedSettingsId + "/resources/jdbc/", view);
        assertEquals("added", model.get("message"));
    }

    @Test
    public void testViewTomcatDataSource_get() throws UnsupportedEncodingException {
        TomcatDataSource tomcatDataSource = new TomcatDataSource();

        expect(settingsService.loadTomcatDataSource(settingsId, this.dataSourceId)).andReturn(tomcatDataSource);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.viewTomcatDataSource(model, settingsId, this.encodedDataSourceId);
        verify(settingsService);

        assertEquals("resources/tomcatDataSource", view);
        assertTrue(model.get("tomcatDataSource") instanceof HumanIdEncodingTomcatDataSource);
        assertSame(tomcatDataSource, ((HumanIdEncodingTomcatDataSource) model.get("tomcatDataSource")).getDelegate());
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testViewTomcatDataSource_put() throws UnsupportedEncodingException {
        TomcatDataSource dataSource = new TomcatDataSource();
        dataSource.getGeneral().setJndiName("name");
        dataSource.getConnection().setDriverClassName("driver");
        dataSource.getConnection().setUrl("url");
        dataSource.getConnection().setUsername("");
        dataSource.getConnection().setPassword("");

        settingsService.saveDataSource(settingsId, dataSourceId, dataSource);

        replay(settingsService);
        String view = controller.viewTomcatDataSource(model, settingsId, this.encodedDataSourceId, dataSource, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + this.encodedSettingsId + "/resources/jdbc/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testViewDbcpDataSource_get() throws UnsupportedEncodingException {
        DbcpDataSource dbcpDataSource = new DbcpDataSource();

        expect(settingsService.loadDbcpDataSource(settingsId, this.dataSourceId)).andReturn(dbcpDataSource);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.viewDbcpDataSource(model, settingsId, this.encodedDataSourceId);
        verify(settingsService);

        assertEquals("resources/dbcpDataSource", view);
        assertSame(dbcpDataSource, ((HumanIdEncodingDbcpDataSource) model.get("dbcpDataSource")).getDelegate());
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testViewDbcpDataSource_put() throws UnsupportedEncodingException {
        DbcpDataSource dataSource = new DbcpDataSource();
        dataSource.getGeneral().setJndiName("name");
        dataSource.getConnection().setDriverClassName("driver");
        dataSource.getConnection().setUrl("url");
        dataSource.getConnection().setUsername("");
        dataSource.getConnection().setPassword("");
        dataSource.getConnectionPool().setTestOnBorrow(false);

        settingsService.saveDataSource(settingsId, this.dataSourceId, dataSource);

        replay(settingsService);
        String view = controller.viewDbcpDataSource(model, settingsId, this.encodedDataSourceId, dataSource, binder);
        verify(settingsService);

        assertEquals("redirect:/app/" + this.encodedSettingsId + "/resources/jdbc/", view);
        assertEquals("saved", model.get("message"));
    }

    @Test
    public void testDeleteDataSourceConfirm_get() throws UnsupportedEncodingException {
        DataSource dataSource = new TomcatDataSource();

        expect(settingsService.loadDataSource(settingsId, this.dataSourceId)).andReturn(dataSource);
        expect(settingsService.loadSettings(settingsId)).andReturn(settings);
        expect(settingsService.isChangePending(settingsId)).andReturn(false);
        expect(settingsService.isRestartPending(settingsId)).andReturn(false);

        replay(settingsService);
        String view = controller.deleteDataSourceConfirm(model, settingsId, this.encodedDataSourceId);
        verify(settingsService);

        assertEquals("resources/deleteDataSource", view);
        assertSame(dataSource, ((HumanIdEncodingTomcatDataSource) model.get("dataSource")).getDelegate());
        assertSame(settings, model.get("settings"));
        assertFalse((Boolean) model.get("changePending"));
        assertFalse((Boolean) model.get("restartPending"));
    }

    @Test
    public void testDeleteDataSource_delete() throws UnsupportedEncodingException {
        settingsService.deleteDataSource(settingsId, this.dataSourceId);

        replay(settingsService);
        String view = controller.deleteDataSource(model, settingsId, this.encodedDataSourceId);
        verify(settingsService);

        assertEquals("redirect:/app/" + this.encodedSettingsId + "/resources/jdbc/", view);
        assertEquals("deleted", model.get("message"));
    }

}
