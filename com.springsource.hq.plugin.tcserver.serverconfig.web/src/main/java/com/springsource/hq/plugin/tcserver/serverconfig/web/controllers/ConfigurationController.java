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

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.JmxListener;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;

/**
 * Spring MVC controller for tc Runtime configuration settings.
 * 
 * @since 2.0
 */
@Controller
public class ConfigurationController {

    private SettingsService settingsService;

    @Autowired
    public ConfigurationController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Form for general configuration settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/configuration/", method = RequestMethod.GET)
    public String general(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("generalConfig", settingsService.loadGeneralConfig(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "configuration/general";
    }

    /**
     * Validate and save new general settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param generalConfig the new general configuration settings
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/configuration/", method = RequestMethod.PUT)
    public String general(Model model, @PathVariable("settingsId") String settingsId, GeneralConfig generalConfig, BindingResult binder)
        throws UnsupportedEncodingException {
        generalConfig.validate(generalConfig, binder);
        JmxListener jmxListenerSettings = settingsService.loadGeneralConfig(settingsId).getJmxListener();
        JmxListener jmxListenerLocal = generalConfig.getJmxListener();
        if (!jmxListenerLocal.equals(jmxListenerSettings)) {
            settingsService.setJmxListenerChanged(settingsId, true);
        } else {
            settingsService.setJmxListenerChanged(settingsId, false);
        }
        if (!binder.hasErrors()) {
            settingsService.saveGeneralConfig(settingsId, generalConfig);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/configuration/";
        } else {
            model.addAttribute("generalConfig", generalConfig);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "configuration/general";
        }
    }

    /**
     * Form for startup settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/configuration/startup/", method = RequestMethod.GET)
    public String startup(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("environment", settingsService.loadEnvironment(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "configuration/startup";
    }

    /**
     * Validate and save new startup settings
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param jvmOptions new JVM options
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/configuration/startup/", method = RequestMethod.PUT)
    public String startup(Model model, @PathVariable("settingsId") String settingsId, Environment environment, BindingResult binder)
        throws UnsupportedEncodingException {
        environment.validate(environment, binder);
        if (!binder.hasErrors()) {
            settingsService.saveEnvironment(settingsId, environment);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/configuration/startup/";
        } else {
            model.addAttribute("environment", environment);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "configuration/startup";
        }
    }

    /**
     * Form for container settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/configuration/container/", method = RequestMethod.GET)
    public String container(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("contextContainer", settingsService.loadContextContainer(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "configuration/container";
    }

    /**
     * Validate and save new container settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param contextContainer the new container settings
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/configuration/container/", method = RequestMethod.PUT)
    public String container(Model model, @PathVariable("settingsId") String settingsId, ContextContainer contextContainer, BindingResult binder)
        throws UnsupportedEncodingException {
        contextContainer.validate(contextContainer, binder);
        if (!binder.hasErrors()) {
            settingsService.saveContextContainer(settingsId, contextContainer);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/configuration/container/";
        } else {
            model.addAttribute("contextContainer", contextContainer);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "configuration/container";
        }
    }

    /**
     * Form for JSP default settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/configuration/defaults-jsp/", method = RequestMethod.GET)
    public String jspDefaults(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("jspDefaults", settingsService.loadJspDefaults(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "configuration/defaultsJsp";
    }

    /**
     * Validate and save new JSP default settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param jspDefaults new JSP default settings
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/configuration/defaults-jsp/", method = RequestMethod.PUT)
    public String jspDefaults(Model model, @PathVariable("settingsId") String settingsId, JspDefaults jspDefaults, BindingResult binder)
        throws UnsupportedEncodingException {
        jspDefaults.validate(jspDefaults, binder);
        if (!binder.hasErrors()) {
            settingsService.saveJspDefaults(settingsId, jspDefaults);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/configuration/defaults-jsp/";
        } else {
            model.addAttribute("jspDefaults", jspDefaults);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "configuration/defaultsJsp";
        }
    }

    /**
     * Form for static content default settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/configuration/defaults-static/", method = RequestMethod.GET)
    public String staticDefaults(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("staticDefaults", settingsService.loadStaticDefaults(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "configuration/defaultsStatic";
    }

    /**
     * Validate and save new static content default settings.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param staticDefaults new static content default settings
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/configuration/defaults-static/", method = RequestMethod.PUT)
    public String staticDefaults(Model model, @PathVariable("settingsId") String settingsId, StaticDefaults staticDefaults, BindingResult binder)
        throws UnsupportedEncodingException {
        staticDefaults.validate(staticDefaults, binder);
        if (!binder.hasErrors()) {
            settingsService.saveStaticDefaults(settingsId, staticDefaults);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/configuration/defaults-static/";
        } else {
            model.addAttribute("staticDefaults", staticDefaults);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "configuration/defaultsStatic";
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
        String[] attributes = BinderUtil.getBindableAttributes(StaticDefaults.class, JspDefaults.class, ContextContainer.class, Environment.class,
            GeneralConfig.class);
        binder.setAllowedFields(attributes);
    }
}
