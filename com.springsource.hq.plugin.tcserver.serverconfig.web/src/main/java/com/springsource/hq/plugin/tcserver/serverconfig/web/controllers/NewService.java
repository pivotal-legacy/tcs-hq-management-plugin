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

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.ValidationUtils;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Host;

/**
 * Web specific extension to Service that supports creating the default host along side a new service.
 * 
 * @since 2.0
 */
@BindableAttributes
class NewService extends Service {

    private NewEngine newEngine;

    private Boolean httpConnector = Boolean.TRUE;

    private Boolean ajpConnector = Boolean.TRUE;

    private Boolean logging = Boolean.FALSE;

    private Boolean threadDiagnostics = Boolean.FALSE;

    public NewService() {
        super();
        newEngine = new NewEngine();
    }

    @Override
    public NewEngine getEngine() {
        return newEngine;
    }

    @Override
    public void setEngine(Engine engine) {
        NewEngine newEngine = new NewEngine();
        BeanUtils.copyProperties(engine, newEngine);
        this.newEngine = newEngine;
    }

    public Boolean getHttpConnector() {
        return httpConnector;
    }

    public void setHttpConnector(Boolean httpConnector) {
        this.httpConnector = httpConnector;
    }

    public Boolean getAjpConnector() {
        return ajpConnector;
    }

    public void setAjpConnector(Boolean ajpConnector) {
        this.ajpConnector = ajpConnector;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public Boolean getThreadDiagnostics() {
        return threadDiagnostics;
    }

    public void setThreadDiagnostics(Boolean threadDiagnostics) {
        this.threadDiagnostics = threadDiagnostics;
    }

    public Service asService() {
        Service service = new Service();
        BeanUtils.copyProperties(this, service, new String[] { "engine" });
        service.setEngine(newEngine.asEngine());
        return service;
    }

    @Override
    public void validate(Object target, Errors errors) {
        NewService service = (NewService) target;
        if (!errors.hasFieldErrors("name")) {
            if (!StringUtils.hasText(service.getName())) {
                errors.rejectValue("name", "service.name.required");
            }
        }
        ValidationUtils.validateCollection(service.getConnectors(), "connectors", errors);

        errors.pushNestedPath("engine");
        service.getEngine().validate(service.getEngine(), errors);
        errors.popNestedPath();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}

@BindableAttributes
class NewEngine extends Engine {

    private Host newHost;

    public NewEngine() {
        super();
        newHost = new Host();
    }

    public Host getNewHost() {
        return newHost;
    }

    public void setNewHost(Host newHost) {
        this.newHost = newHost;
    }

    public Engine asEngine() {
        Engine engine = new Engine();
        BeanUtils.copyProperties(this, engine);
        engine.getHosts().add(newHost);
        engine.setDefaultHost(newHost.getName());
        return engine;
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        NewEngine engine = (NewEngine) target;
        errors.pushNestedPath("newHost");
        engine.getNewHost().validate(engine.getNewHost(), errors);
        errors.popNestedPath();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
