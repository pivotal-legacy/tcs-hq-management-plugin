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

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.serverconfig.Profile;
import com.springsource.hq.plugin.tcserver.serverconfig.ProfileMarshaller;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;

/**
 * Spring MVC controller to load and save group configuration profiles
 * @since 2.0
 */
@Controller
public class ProfileController {

    private SettingsService settingsService;

    private ProfileMarshaller marshaller;

    private static final Log logger = LogFactory.getLog(ProfileController.class);

    @Autowired
    public ProfileController(SettingsService settingsService, ProfileMarshaller marshaller) {
        this.settingsService = settingsService;
        this.marshaller = marshaller;
    }

    @RequestMapping(value = "/{settingsId}/profile/", method = RequestMethod.GET)
    public String saveProfile(Model model, @PathVariable("settingsId") String settingsId,
        @RequestParam(value = "name", required = false) String name, HttpServletResponse response) throws IOException, JAXBException, SAXException {
        Settings settings = settingsService.loadSettings(settingsId);
        Errors errors = new BeanPropertyBindingResult(settings, "settings");
        settings.validate(settings, errors);
        if (errors.hasErrors()) {
            // the user should not be here
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        }
        String filename;
        if (!StringUtils.hasText(name)) {
            name = "Profile created from " + settingsId;
            filename = "profile-" + settingsId.replaceAll("[^a-zA-Z0-9]", "") + ".xml";
        } else {
            filename = "profile-" + name.replaceAll("[^a-zA-Z0-9]", "") + ".xml";
        }
        Profile profile = new Profile(settings, name);
        response.setContentType("application/vnd.springsource.tomcatserverconfig.profile+xml");
        response.setHeader("Content-Disposition", "inline;filename=" + filename);
        marshaller.marshal(profile, new StreamResult(response.getOutputStream()));
        return null;
    }

    @RequestMapping(value = "/{settingsId}/profile/", method = RequestMethod.POST)
    public String loadProfile(Model model, @PathVariable("settingsId") String settingsId, @RequestParam("profile") MultipartFile file)
        throws UnsupportedEncodingException {
        try {
            Profile profile = marshaller.unmarshal(new StreamSource(file.getInputStream()));
            Settings settings = profile.getSettings();
            settings.setEid(settingsId);
            settings.applyParentToChildren();
            settingsService.updateLocalSettings(settings);
            model.addAttribute("message", "profile-loaded");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        } catch (Exception e) {
            logger.warn("Error loading profile", e);
            model.addAttribute("message", "error-loading-profile");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/";
        }
    }

    @RequestMapping(value = "/{settingsId}/profile/schema/", method = RequestMethod.GET)
    public void profileSchmea(HttpServletResponse response) throws Exception {
        SchemaOutputResolver outputResolver = new HttpSchemaOutputResolver(response);
        marshaller.generateSchema(outputResolver);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
    }

    private static class HttpSchemaOutputResolver extends SchemaOutputResolver {

        private HttpServletResponse response;

        public HttpSchemaOutputResolver(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
            response.setContentType("application/xml");
            response.setHeader("Content-Disposition", "inline;filename=" + suggestedFileName);
            Result r = new StreamResult(response.getOutputStream());
            // Sun's FoolProofResolver requires a non null systemId, even if we
            // don't care about it or ever use it
            r.setSystemId("not null");
            return r;
        }

    }

}
