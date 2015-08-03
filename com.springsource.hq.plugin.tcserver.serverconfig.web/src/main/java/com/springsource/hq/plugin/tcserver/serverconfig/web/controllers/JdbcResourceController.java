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

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;

/**
 * Spring MVC controller for JDBC resources
 * 
 * @since 2.0
 */
@Controller
public class JdbcResourceController {

    private SettingsService settingsService;

    @Autowired
    public JdbcResourceController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * List JDBC resources
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/", method = RequestMethod.GET)
    public String listDataSources(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("dbcpDataSources", loadDbcpDataSources(settingsId));
        model.addAttribute("tomcatDataSources", loadTomcatDataSources(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/dataSources";
    }

    /**
     * Form for new tc Runtime data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/tomcat-new/", method = RequestMethod.GET)
    public String newTomcatDataSource(Model model, @PathVariable("settingsId") String settingsId) {
        TomcatDataSource tomcatDataSource = new TomcatDataSource();
        tomcatDataSource.getConnectionPool().setJdbcInterceptors("ConnectionState;StatementFinalizer");
        model.addAttribute("tomcatDataSource", tomcatDataSource);
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/newTomcatDataSource";
    }

    /**
     * Validate and create a new Tomcat data source.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param tomcatDataSource the new data source
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/tomcat-new/", method = RequestMethod.POST)
    public String newTomcatDataSource(Model model, @PathVariable("settingsId") String settingsId, TomcatDataSource tomcatDataSource,
        BindingResult binder) throws UnsupportedEncodingException {
        tomcatDataSource.validate(tomcatDataSource, binder);
        if (!binder.hasErrors()) {
            settingsService.addDataSource(settingsId, tomcatDataSource);
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/resources/jdbc/";
        } else {
            model.addAttribute("tomcatDataSource", tomcatDataSource);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "resources/newTomcatDataSource";
        }
    }

    /**
     * Form for new DBCP data source.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/dbcp-new/", method = RequestMethod.GET)
    public String newDbcpDataSource(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("dbcpDataSource", new DbcpDataSource());
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/newDbcpDataSource";
    }

    /**
     * Validate and create a new DBCP data source.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dbcpDataSource the new DBCP data source
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/dbcp-new/", method = RequestMethod.POST)
    public String newDbcpDataSource(Model model, @PathVariable("settingsId") String settingsId, DbcpDataSource dbcpDataSource, BindingResult binder)
        throws UnsupportedEncodingException {
        dbcpDataSource.validate(dbcpDataSource, binder);
        if (!binder.hasErrors()) {
            settingsService.addDataSource(settingsId, dbcpDataSource);
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/resources/jdbc/";
        } else {
            model.addAttribute("dbcpDataSource", dbcpDataSource);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "resources/newDbcpDataSource";
        }
    }

    /**
     * Form to edit a Tomcat data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/tomcat/{dataSourceId}/", method = RequestMethod.GET)
    public String viewTomcatDataSource(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("dataSourceId") String dataSourceId)
        throws UnsupportedEncodingException {
        model.addAttribute("tomcatDataSource", loadTomcatDataSource(settingsId, dataSourceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/tomcatDataSource";
    }

    /**
     * Validate and update a Tomcat data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @param tomcatDataSource the updated data source
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/tomcat/{dataSourceId}/", method = RequestMethod.PUT)
    public String viewTomcatDataSource(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("dataSourceId") String dataSourceId,
        TomcatDataSource tomcatDataSource, BindingResult binder) throws UnsupportedEncodingException {
        tomcatDataSource.validate(tomcatDataSource, binder);
        if (!binder.hasErrors()) {
            saveDataSource(settingsId, dataSourceId, tomcatDataSource);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/resources/jdbc/";
        } else {
            model.addAttribute("tomcatDataSource", tomcatDataSource);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "resources/tomcatDataSource";
        }
    }

    /**
     * Form to edit a DBCP data source.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/dbcp/{dataSourceId}/", method = RequestMethod.GET)
    public String viewDbcpDataSource(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("dataSourceId") String dataSourceId)
        throws UnsupportedEncodingException {
        model.addAttribute("dbcpDataSource", loadDbcpDataSource(settingsId, dataSourceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/dbcpDataSource";
    }

    /**
     * Validate and update a DBCP data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @param dbcpDataSource the updated dbcp data source
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/dbcp/{dataSourceId}/", method = RequestMethod.PUT)
    public String viewDbcpDataSource(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("dataSourceId") String dataSourceId,
        DbcpDataSource dbcpDataSource, BindingResult binder) throws UnsupportedEncodingException {
        dbcpDataSource.validate(dbcpDataSource, binder);
        if (!binder.hasErrors()) {
            saveDataSource(settingsId, dataSourceId, dbcpDataSource);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/resources/jdbc/";
        } else {
            model.addAttribute("dbcpDataSource", dbcpDataSource);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "resources/dbcpDataSource";
        }
    }

    /**
     * Confirmation dialog before deleting a data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/{dataSourceId}/delete/", method = RequestMethod.GET)
    public String deleteDataSourceConfirm(Model model, @PathVariable("settingsId") String settingsId,
        @PathVariable("dataSourceId") String dataSourceId) throws UnsupportedEncodingException {
        model.addAttribute("dataSource", loadDataSource(settingsId, dataSourceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "resources/deleteDataSource";
    }

    /**
     * Delete data source
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param dataSourceId the data source human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/resources/jdbc/{dataSourceId}/delete/", method = RequestMethod.DELETE)
    public String deleteDataSource(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("dataSourceId") String dataSourceId)
        throws UnsupportedEncodingException {
        deleteDataSource(settingsId, dataSourceId);
        model.addAttribute("message", "deleted");
        return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/resources/jdbc/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
        String[] attributes = BinderUtil.getBindableAttributes(DbcpDataSource.class, TomcatDataSource.class);
        binder.setAllowedFields(attributes);
    }

    private Set<TomcatDataSource> loadTomcatDataSources(String eid) {
        Set<TomcatDataSource> tomcatDataSources = this.settingsService.loadTomcatDataSources(eid);
        Set<TomcatDataSource> wrappedDataSources = new LinkedHashSet<TomcatDataSource>();
        for (TomcatDataSource tomcatDataSource : tomcatDataSources) {
            wrappedDataSources.add(new HumanIdEncodingTomcatDataSource(tomcatDataSource));
        }
        return wrappedDataSources;
    }

    private Set<DbcpDataSource> loadDbcpDataSources(String eid) {
        Set<DbcpDataSource> dbcpDataSources = this.settingsService.loadDbcpDataSources(eid);
        Set<DbcpDataSource> wrappedDataSources = new LinkedHashSet<DbcpDataSource>();
        for (DbcpDataSource dbcpDataSource : dbcpDataSources) {
            wrappedDataSources.add(new HumanIdEncodingDbcpDataSource(dbcpDataSource));
        }
        return wrappedDataSources;
    }

    private DbcpDataSource loadDbcpDataSource(String settingsId, String dataSourceId) throws UnsupportedEncodingException {
        return new HumanIdEncodingDbcpDataSource(this.settingsService.loadDbcpDataSource(settingsId, UriUtils.decode(dataSourceId, "UTF-8")));
    }

    private TomcatDataSource loadTomcatDataSource(String settingsId, String dataSourceId) throws UnsupportedEncodingException {
        return new HumanIdEncodingTomcatDataSource(this.settingsService.loadTomcatDataSource(settingsId, UriUtils.decode(dataSourceId, "UTF-8")));
    }

    private void saveDataSource(String settingsId, String dataSourceId, DataSource dataSource) throws UnsupportedEncodingException {
        this.settingsService.saveDataSource(settingsId, UriUtils.decode(dataSourceId, "UTF-8"), dataSource);
    }

    private void deleteDataSource(String settingsId, String dataSourceId) throws UnsupportedEncodingException {
        this.settingsService.deleteDataSource(settingsId, UriUtils.decode(dataSourceId, "UTF-8"));
    }

    private DataSource loadDataSource(String settingsId, String dataSourceId) throws UnsupportedEncodingException {
        DataSource dataSource = this.settingsService.loadDataSource(settingsId, UriUtils.decode(dataSourceId, "UTF-8"));
        if (dataSource instanceof TomcatDataSource) {
            return new HumanIdEncodingTomcatDataSource((TomcatDataSource) dataSource);
        } else if (dataSource instanceof DbcpDataSource) {
            return new HumanIdEncodingDbcpDataSource((DbcpDataSource) dataSource);
        } else {
            throw new IllegalStateException("Unexpected DataSource type: " + dataSource.getClass());
        }
    }
}
