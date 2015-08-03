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
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
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

import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;
import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;

/**
 * Spring MVC controller for tc Runtime services
 * 
 * @since 2.0
 */
@Controller
public class ServicesController {

    private SettingsService settingsService;

    @Autowired
    public ServicesController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * List of services
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/services/", method = RequestMethod.GET)
    public String services(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("services", loadServices(settingsId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/services";
    }

    /**
     * Form for new service.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @return
     */
    @RequestMapping(value = "/{settingsId}/services-new/", method = RequestMethod.GET)
    public String newService(Model model, @PathVariable("settingsId") String settingsId) {
        model.addAttribute("newService", new NewService());
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/newService";
    }

    /**
     * Validate and create service.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param service the new service
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services-new/", method = RequestMethod.POST)
    public String newService(Model model, @PathVariable("settingsId") String settingsId, NewService service, BindingResult binder)
        throws UnsupportedEncodingException {
        service.getEngine().setDefaultHost(((NewEngine) service.getEngine()).getNewHost().getName());
        service.validate(service, binder);
        if (!binder.hasErrors()) {
            settingsService.addService(settingsId, service.asService());
            if (service.getHttpConnector()) {
                HttpConnector connector = new HttpConnector();
                connector.setPort(8080L);
                settingsService.addConnector(settingsId, service.getHumanId(), connector);
            }
            if (service.getAjpConnector()) {
                AjpConnector connector = new AjpConnector();
                connector.setPort(8009L);
                settingsService.addConnector(settingsId, service.getHumanId(), connector);
            }
            if (service.getLogging()) {
                settingsService.loadEngine(settingsId, service.getHumanId()).getLogging().setEnabled(true);
            }
            if (service.getThreadDiagnostics()) {
                settingsService.loadEngine(settingsId, service.getHumanId()).getThreadDiagnostics().setEnabled(true);
            }
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/";
        } else {
            model.addAttribute("service", service);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/newService";
        }
    }

    /**
     * Service details
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/", method = RequestMethod.GET)
    public String service(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/engine/";
    }

    /**
     * Confirmation dialog before deleting service.
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the servie human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/delete/", method = RequestMethod.GET)
    public String deleteService(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/deleteService";
    }

    /**
     * Delete service
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/delete/", method = RequestMethod.DELETE)
    public String deleteServiceConfirm(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        deleteService(settingsId, serviceId);
        model.addAttribute("message", "deleted");
        return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/";
    }

    /**
     * List connectors
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/", method = RequestMethod.GET)
    public String connectors(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("ajpConnectors", loadAjpConnectors(settingsId, serviceId));
        model.addAttribute("httpConnectors", loadHttpConnectors(settingsId, serviceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/connectors";
    }

    /**
     * Form for new AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp-new/", method = RequestMethod.GET)
    public String newAjpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("ajpConnector", new AjpConnector());
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/newAjpConnector";
    }

    /**
     * Validate and create new AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param ajpConnector the new AJP connector
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp-new/", method = RequestMethod.POST)
    public String newAjpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        AjpConnector ajpConnector, BindingResult binder) throws UnsupportedEncodingException {
        ajpConnector.validate(ajpConnector, binder);
        if (!binder.hasErrors()) {
            addConnector(settingsId, serviceId, ajpConnector);
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/connectors/";
        } else {
            model.addAttribute("ajpConnector", ajpConnector);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/newAjpConnector";
        }
    }

    /**
     * Form for updating AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp/{connectorId}/", method = RequestMethod.GET)
    public String ajpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        model.addAttribute("ajpConnector", loadConnector(settingsId, serviceId, connectorId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/ajpConnector";
    }

    /**
     * Validate and update the AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @param ajpConnector the update AJP connector
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp/{connectorId}/", method = RequestMethod.PUT)
    public String ajpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId, AjpConnector ajpConnector, BindingResult binder) throws UnsupportedEncodingException {
        ajpConnector.validate(ajpConnector, binder);
        if (!binder.hasErrors()) {
            saveConnector(settingsId, serviceId, connectorId, ajpConnector);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/connectors/";
        } else {
            model.addAttribute("ajpConnector", ajpConnector);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/ajpConnector";
        }
    }

    /**
     * Confirmation dialog to delete AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp/{connectorId}/delete/", method = RequestMethod.GET)
    public String ajpConnectorDelete(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        return connectorDelete(model, settingsId, serviceId, connectorId);
    }

    /**
     * Delete AJP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/ajp/{connectorId}/delete/", method = RequestMethod.DELETE)
    public String ajpConnectorDeleteConfirm(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        return connectorDeleteConfirm(model, settingsId, serviceId, connectorId);
    }

    /**
     * Form for new HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http-new/", method = RequestMethod.GET)
    public String newHttpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("httpConnector", new HttpConnector());
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/newHttpConnector";
    }

    /**
     * Validate and create new HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param httpConnector the new connector
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http-new/", method = RequestMethod.POST)
    public String newHttpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        HttpConnector httpConnector, BindingResult binder) throws UnsupportedEncodingException {
        httpConnector.validate(httpConnector, binder);
        if (!binder.hasErrors()) {
            addConnector(settingsId, serviceId, httpConnector);
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/connectors/";
        } else {
            model.addAttribute("httpConnector", httpConnector);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/newHttpConnector";
        }
    }

    /**
     * Form to update HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http/{connectorId}/", method = RequestMethod.GET)
    public String httpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        model.addAttribute("httpConnector", loadConnector(settingsId, serviceId, connectorId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/httpConnector";
    }

    /**
     * Validate and update HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @param httpConnector the update HTTP connector
     * @param binder the binding result
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http/{connectorId}/", method = RequestMethod.PUT)
    public String httpConnector(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId, HttpConnector httpConnector, BindingResult binder) throws UnsupportedEncodingException {
        httpConnector.validate(httpConnector, binder);
        if (!binder.hasErrors()) {
            saveConnector(settingsId, serviceId, connectorId, httpConnector);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/connectors/";
        } else {
            model.addAttribute("httpConnector", httpConnector);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/httpConnector";
        }
    }

    /**
     * Confirmation dialog to delete HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http/{connectorId}/delete/", method = RequestMethod.GET)
    public String httpConnectorDelete(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        return connectorDelete(model, settingsId, serviceId, connectorId);
    }

    /**
     * Delete HTTP connector
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param connectorId the connector human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/connectors/http/{connectorId}/delete/", method = RequestMethod.DELETE)
    public String httpConnectorDeleteConfirm(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("connectorId") String connectorId) throws UnsupportedEncodingException {
        return connectorDeleteConfirm(model, settingsId, serviceId, connectorId);
    }

    private String connectorDelete(Model model, String settingsId, String serviceId, String connectorId) throws UnsupportedEncodingException {
        model.addAttribute("connector", loadConnector(settingsId, serviceId, connectorId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/deleteConnector";
    }

    private String connectorDeleteConfirm(Model model, String eid, String serviceId, String connectorId) throws UnsupportedEncodingException {
        deleteConnector(eid, serviceId, connectorId);
        model.addAttribute("message", "deleted");
        return "redirect:/app/" + UriUtils.encodePathSegment(eid, "UTF-8") + "/services/" + serviceId + "/connectors/";
    }

    /**
     * Form to update engine
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/", method = RequestMethod.GET)
    public String engine(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("engine", loadEngine(settingsId, serviceId));
        model.addAttribute("hosts", loadHosts(settingsId, serviceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/engine";
    }

    /**
     * Validate and update engine
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param engine the updated engine
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/", method = RequestMethod.PUT)
    public String engine(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId, Engine engine,
        BindingResult binder) throws UnsupportedEncodingException {
        engine.validate(engine, binder);
        if (!binder.hasErrors()) {
            // copy properties instead of wholesale overwrite
            Engine currentEngine = loadEngine(settingsId, serviceId);
            BeanUtils.copyProperties(engine, currentEngine, new String[] { "hosts" });
            saveEngine(settingsId, serviceId, currentEngine);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/engine/";
        } else {
            model.addAttribute("engine", engine);
            model.addAttribute("hosts", settingsService.loadHosts(settingsId, serviceId));
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/engine";
        }
    }

    /**
     * List hosts
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts/", method = RequestMethod.GET)
    public String hosts(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("hosts", loadHosts(settingsId, serviceId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/hosts";
    }

    /**
     * Form for new host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts-new/", method = RequestMethod.GET)
    public String newHost(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId)
        throws UnsupportedEncodingException {
        model.addAttribute("host", new Host());
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/newHost";
    }

    /**
     * Validate and create new host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param host the new host
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts-new/", method = RequestMethod.POST)
    public String newHost(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId, Host host,
        BindingResult binder) throws UnsupportedEncodingException {
        host.validate(host, binder);
        if (!binder.hasErrors()) {
            addHost(settingsId, serviceId, host);
            model.addAttribute("message", "added");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/engine/hosts/";
        } else {
            model.addAttribute("host", host);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/newHost";
        }
    }

    /**
     * From to update host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param hostId the host human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts/{hostId}/", method = RequestMethod.GET)
    public String host(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("hostId") String hostId) throws UnsupportedEncodingException {
        model.addAttribute("host", loadHost(settingsId, serviceId, hostId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/host";
    }

    /**
     * Validate and update host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param hostId the host human id
     * @param host the updated host
     * @param binder the binding results
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts/{hostId}/", method = RequestMethod.PUT)
    public String host(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("hostId") String hostId, Host host, BindingResult binder) throws UnsupportedEncodingException {
        host.validate(host, binder);
        if (!binder.hasErrors()) {
            saveHost(settingsId, serviceId, hostId, host);
            model.addAttribute("message", "saved");
            return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/engine/hosts/";
        } else {
            model.addAttribute("host", host);
            model.addAttribute("settings", settingsService.loadSettings(settingsId));
            model.addAttribute("service", loadService(settingsId, serviceId));
            model.addAttribute("changePending", settingsService.isChangePending(settingsId));
            model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
            return "services/host";
        }
    }

    /**
     * Confirmation dialog to delete host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param hostId the host id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts/{hostId}/delete/", method = RequestMethod.GET)
    public String hostDelete(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("hostId") String hostId) throws UnsupportedEncodingException {
        model.addAttribute("host", loadHost(settingsId, serviceId, hostId));
        model.addAttribute("settings", settingsService.loadSettings(settingsId));
        model.addAttribute("service", loadService(settingsId, serviceId));
        model.addAttribute("changePending", settingsService.isChangePending(settingsId));
        model.addAttribute("restartPending", settingsService.isRestartPending(settingsId));
        return "services/deleteHost";
    }

    /**
     * Delete host
     * 
     * @param model
     * @param settingsId the tc Runtime instance id
     * @param serviceId the service human id
     * @param hostId the host human id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{settingsId}/services/{serviceId}/engine/hosts/{hostId}/delete/", method = RequestMethod.DELETE)
    public String hostDeleteConfirm(Model model, @PathVariable("settingsId") String settingsId, @PathVariable("serviceId") String serviceId,
        @PathVariable("hostId") String hostId) throws UnsupportedEncodingException {
        deleteHost(settingsId, serviceId, hostId);
        model.addAttribute("message", "deleted");
        return "redirect:/app/" + UriUtils.encodePathSegment(settingsId, "UTF-8") + "/services/" + serviceId + "/engine/hosts/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
        String[] attributes = BinderUtil.getBindableAttributes(Host.class, Engine.class, HttpConnector.class, NewService.class, AjpConnector.class);
        binder.setAllowedFields(attributes);
    }

    private Object loadServices(String settingsId) {
        Set<Service> services = new HashSet<Service>();
        for (Service service : this.settingsService.loadServices(settingsId)) {
            services.add(new HumanIdEncodingService(service));
        }
        return services;
    }

    private Service loadService(String settingsId, String serviceId) throws UnsupportedEncodingException {
        return new HumanIdEncodingService(this.settingsService.loadService(settingsId, UriUtils.decode(serviceId, "UTF-8")));
    }

    private void deleteService(String settingsId, String serviceId) throws UnsupportedEncodingException {
        this.settingsService.deleteService(settingsId, UriUtils.decode(serviceId, "UTF-8"));
    }

    private Set<AjpConnector> loadAjpConnectors(String settingsId, String serviceId) throws UnsupportedEncodingException {
        return this.settingsService.loadAjpConnectors(settingsId, UriUtils.decode(serviceId, "UTF-8"));
    }

    private Set<HttpConnector> loadHttpConnectors(String settingsId, String serviceId) throws UnsupportedEncodingException {
        return this.settingsService.loadHttpConnectors(settingsId, UriUtils.decode(serviceId, "UTF-8"));
    }

    private void addConnector(String settingsId, String serviceId, AjpConnector ajpConnector) throws UnsupportedEncodingException {
        this.settingsService.addConnector(settingsId, UriUtils.decode(serviceId, "UTF-8"), ajpConnector);
    }

    private Connector loadConnector(String settingsId, String serviceId, String connectorId) throws UnsupportedEncodingException {
        return this.settingsService.loadConnector(settingsId, UriUtils.decode(serviceId, "UTF-8"), connectorId);
    }

    private void saveConnector(String settingsId, String serviceId, String connectorId, AjpConnector ajpConnector)
        throws UnsupportedEncodingException {
        this.settingsService.saveConnector(settingsId, UriUtils.decode(serviceId, "UTF-8"), connectorId, ajpConnector);
    }

    private void addConnector(String settingsId, String serviceId, HttpConnector httpConnector) throws UnsupportedEncodingException {
        this.settingsService.addConnector(settingsId, UriUtils.decode(serviceId, "UTF-8"), httpConnector);
    }

    private void saveConnector(String settingsId, String serviceId, String connectorId, HttpConnector httpConnector)
        throws UnsupportedEncodingException {
        this.settingsService.saveConnector(settingsId, UriUtils.decode(serviceId, "UTF-8"), connectorId, httpConnector);
    }

    private void deleteConnector(String eid, String serviceId, String connectorId) throws UnsupportedEncodingException {
        this.settingsService.deleteConnector(eid, UriUtils.decode(serviceId, "UTF-8"), connectorId);
    }

    private Engine loadEngine(String settingsId, String serviceId) throws UnsupportedEncodingException {
        return this.settingsService.loadEngine(settingsId, UriUtils.decode(serviceId, "UTF-8"));
    }

    private Set<Host> loadHosts(String settingsId, String serviceId) throws UnsupportedEncodingException {
        return this.settingsService.loadHosts(settingsId, UriUtils.decode(serviceId, "UTF-8"));
    }

    private void saveEngine(String settingsId, String serviceId, Engine currentEngine) throws UnsupportedEncodingException {
        this.settingsService.saveEngine(settingsId, UriUtils.decode(serviceId, "UTF-8"), currentEngine);
    }

    private void addHost(String settingsId, String serviceId, Host host) throws UnsupportedEncodingException {
        this.settingsService.addHost(settingsId, UriUtils.decode(serviceId, "UTF-8"), host);
    }

    private Host loadHost(String settingsId, String serviceId, String hostId) throws UnsupportedEncodingException {
        return this.settingsService.loadHost(settingsId, UriUtils.decode(serviceId, "UTF-8"), hostId);
    }

    private void saveHost(String settingsId, String serviceId, String hostId, Host host) throws UnsupportedEncodingException {
        this.settingsService.saveHost(settingsId, UriUtils.decode(serviceId, "UTF-8"), hostId, host);
    }

    private void deleteHost(String settingsId, String serviceId, String hostId) throws UnsupportedEncodingException {
        this.settingsService.deleteHost(settingsId, UriUtils.decode(serviceId, "UTF-8"), hostId);
    }

}
