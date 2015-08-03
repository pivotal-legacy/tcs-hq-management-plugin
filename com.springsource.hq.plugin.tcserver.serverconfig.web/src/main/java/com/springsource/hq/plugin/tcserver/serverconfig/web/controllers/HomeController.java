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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.RequestUtils;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;

/**
 * Spring MVC controller to manage common actions on the tc Runtime settings.
 * 
 * @since 2.0
 */
@Controller
public class HomeController {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    private SettingsService settingsService;

    private final Set<String> uploadableFiles = new HashSet<String>();
    {
        uploadableFiles.add("/conf/server.xml");
        uploadableFiles.add("/conf/web.xml");
        uploadableFiles.add("/conf/context.xml");
        uploadableFiles.add("/conf/catalina.properties");
        uploadableFiles.add("/conf/logging.properties");
    }

    @Autowired
    public HomeController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Entry point into the tc RuntimeServerConfig application from HQ
     * 
     * @param sessionId the authenticated users session id
     * @param eid the tc Runtime instance id
     * @param basePath the base URL to connect back to HQ
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String enter(@RequestParam String sessionId, @RequestParam String eid,
        @RequestParam(required = false, defaultValue = "false") String readOnly, @RequestParam String username, @RequestParam String csrfNonce,
        Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            if (SecurityContextHolder.getContext() != null) {
                logger.debug("Authentication = " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                logger.debug("There appears to be no security context!");
            }
            checkOrAddUsernameAndResetSession(request, model, username);

            String basePath = RequestUtils.getLocalHqUrl(request);
            settingsService.loadSettings(eid, sessionId, csrfNonce, basePath, Boolean.parseBoolean(readOnly));
            return "redirect:/app/" + UriUtils.encodePathSegment(eid, "UTF-8") + "/";
        } catch (SettingsLoaderException e) {
            model.addAttribute("error", e.getMessage());
            return "home/loadError";
        }
    }

    /**
     * Checks the current session for a username. If one exists, it compares it to the current username. If the two
     * usernames do not match, the session is invalidated, a new session is created and the current username is stored
     * in the new session.
     */
    private void checkOrAddUsernameAndResetSession(HttpServletRequest request, Model model, String currentUsername) {

        logger.info("Current username: " + currentUsername);
        Map<?, ?> modelMap = model.asMap();
        String storedUsername = (String) modelMap.get("username");

        if (storedUsername == null) {
            model.addAttribute("username", currentUsername);
            logger.info("Added username " + currentUsername + " to the session");
        } else if (!storedUsername.equals(currentUsername)) {
            logger.info("Found username (" + currentUsername + ") in the session; invalidating session");
            request.getSession(false).invalidate();
            request.getSession(true);
            model.addAttribute("username", currentUsername);
            logger.info("Added username " + currentUsername + " to the new session");
        }

    }

    /**
     * The 'home' screen for server configuration
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/", method = RequestMethod.GET)
    public String index(Model model, @PathVariable("settingsId") String settingsId) {
        Settings settings = settingsService.loadSettings(settingsId);
        Errors errors = new BeanPropertyBindingResult(settings, "settings");
        settings.validate(settings, errors);
        model.addAttribute("org.springframework.validation.BindingResult.settings", errors);
        model.addAttribute("hasErrors", errors.hasErrors());
        model.addAttribute("settings", settings);
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "home/home";
    }

    /**
     * Confirmation dialog before pushing settings back to the tc Runtime instance. The settings will be validated and
     * errors displayed, if any.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/save/", method = RequestMethod.GET)
    public String save(Model model, @PathVariable("settingsId") String settingsId) throws UnsupportedEncodingException {
        Settings settings = settingsService.loadSettings(settingsId);
        Errors errors = new BeanPropertyBindingResult(settings, "settings");
        settings.validate(settings, errors);
        if (errors.hasErrors()) {
            // if there are errors, the user should not be here.
            // send user home to display errors
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        }
        model.addAttribute("settings", settings);
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "home/save";
    }

    /**
     * Push settings back to the tc Runtime instance. The request will be rejected if there are any validation errors.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/save/", method = RequestMethod.PUT)
    public String saveConfirmed(Model model, @PathVariable("settingsId") String settingsId) throws UnsupportedEncodingException {
        Settings settings = settingsService.loadSettings(settingsId);
        Errors errors = new BeanPropertyBindingResult(settings, "settings");
        settings.validate(settings, errors);
        if (errors.hasErrors()) {
            // if there are errors, the user should not be here.
            // send user home to display errors
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        }
        try {
            settingsService.saveSettings(settingsId);
            model.addAttribute("message", "saved-to-server");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (SettingsLoaderException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "home/save";
        }
    }

    /**
     * Confirmation dialog to reload settings from tc Runtime, overwriting any local changes.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/revert/", method = RequestMethod.GET)
    public String revert(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "home/revert";
    }

    /**
     * Reload settings from tc Runtime, any local changes are overwritten.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/revert/", method = RequestMethod.DELETE)
    public String revertConfirmed(Model model, @PathVariable("settingsId") String settingsId) throws UnsupportedEncodingException {
        try {
            settingsService.reloadSettings(settingsId);
            model.addAttribute("message", "reloaded");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (SettingsLoaderException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "home/revert";
        }
    }

    /**
     * Confirmation dialog to revert to the previous configuration files, overwriting any local changes.
     * 
     * @param model
     * @param settingsId The id of the resource.
     * @return
     */
    @RequestMapping(value = "/{settingsId}/revertToPreviousConfiguration/", method = RequestMethod.GET)
    public String revertToPreviousConfiguration(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "home/revertToPreviousConfiguration";
    }

    /**
     * Reverts the configuration files to the latest backed up version.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/revertToPreviousConfiguration/", method = RequestMethod.DELETE)
    public String revertToPreviousConfigurationConfirmed(Model model, @PathVariable("settingsId") String settingsId)
        throws UnsupportedEncodingException {
        try {
            settingsService.revertToPreviousConfiguration(settingsId);
            model.addAttribute("message", "reverted");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (SettingsLoaderException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "home/revertToPreviousConfiguration";
        }
    }

    /**
     * Confirmation dialog to restart the tc Runtime instance.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/restart/", method = RequestMethod.GET)
    public String restart(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "home/restart";
    }

    /**
     * Restart the tc Runtime instance.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/restart/", method = RequestMethod.POST)
    public String restartConfirmed(Model model, @PathVariable("settingsId") String settingsId) throws UnsupportedEncodingException {
        try {
            settingsService.restartServer(settingsId);
            model.addAttribute("message", "restarted");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (SettingsLoaderException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "home/restart";
        }
    }

    /**
     * Upload a file to tc Runtime.
     * 
     * @param model
     * @param settingsId the tc Runtime instance
     * @param file the server.xml file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/{settingsId}/upload/", method = RequestMethod.POST)
    public String uploadConfigurationFile(Model model, @PathVariable("settingsId") String settingsId, @RequestParam("fileName") String fileName,
        @RequestParam("file") MultipartFile file) throws IOException {
        try {
            if (!uploadableFiles.contains(fileName)) {
                throw new UnsupportedOperationException("File " + fileName + " is not supported for uploading");
            }
            settingsService.uploadConfigurationFile(settingsId, fileName, new String(Base64.encodeBase64(file.getBytes())));
            model.addAttribute("message", "saved-to-server");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "home/home";
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
    }

}
