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

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.AprLifecycleListener;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.ValidationUtils;

/**
 * Root object containing all remote settings for a tc Runtime instance.
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "settings", propOrder = { "configuration", "dataSources", "services" })
public class Settings implements Validator, Hierarchical<Settings>, Identity {

    @XmlElement(name = "configuration", required = true)
    private Configuration configuration = new Configuration();

    @XmlElementWrapper(name = "data-sources", required = true)
    @XmlElement(name = "data-source")
    private Set<DataSource> dataSources = new LinkedHashSet<DataSource>();

    /**
     * the identifier for the tc Runtime instance inside HQ
     */
    @XmlTransient
    private String eid;

    @XmlElementWrapper(name = "services", required = true)
    @XmlElement(name = "service")
    private Set<Service> services = new LinkedHashSet<Service>();

    @XmlTransient
    private String id;

    public void applyParentToChildren() {
        configuration.setParent(this);
        configuration.applyParentToChildren();
        for (DataSource dataSource : dataSources) {
            dataSource.setParent(this);
            dataSource.applyParentToChildren();
        }
        for (Service service : services) {
            service.setParent(this);
            service.applyParentToChildren();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Settings)) {
            return false;
        }
        Settings settings = (Settings) obj;
        return ObjectUtils.nullSafeEquals(this.getEid(), settings.getEid())
            && ObjectUtils.nullSafeEquals(this.getConfiguration(), settings.getConfiguration())
            && ObjectUtils.nullSafeEquals(this.getDataSources(), settings.getDataSources())
            && ObjectUtils.nullSafeEquals(this.getServices(), settings.getServices());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Set<DataSource> getDataSources() {
        return dataSources;
    }

    /**
     * @return the identifier for the tc Runtime instance inside HQ
     */
    public String getEid() {
        return eid;
    }

    public String getHumanId() {
        return eid.replace("/", "");
    }

    public Set<Service> getServices() {
        return services;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.eid) * 29 + ObjectUtils.nullSafeHashCode(this.configuration) * 29
            + ObjectUtils.nullSafeHashCode(this.dataSources) * 29 + ObjectUtils.nullSafeHashCode(this.services) * 29;
    }

    public Settings parent() {
        return null;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setDataSources(Set<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    /**
     * @param eid the identifier for the tc Runtime instance inside HQ
     */
    public void setEid(String eid) {
        this.eid = eid;
    }

    public void setParent(Settings parent) {
        // no-op, this is the top of the hierarchy
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Settings settings = (Settings) target;
        errors.pushNestedPath("configuration");
        settings.getConfiguration().validate(settings.getConfiguration(), errors);
        errors.popNestedPath();

        ValidationUtils.validateCollection(settings.getDataSources(), "dataSources", errors);
        ValidationUtils.validateCollection(settings.getServices(), "services", errors);

        if (isAprConnectorConfigured(settings)) {
            settings.getConfiguration().getGeneralConfig().setAprLifecycleListener(new AprLifecycleListener());
        } else {
            settings.getConfiguration().getGeneralConfig().setAprLifecycleListener(null);
        }
    }

    private boolean isAprConnectorConfigured(Settings settings) {
        boolean foundJavaAprConnector = false;

        // Check for HTTP/APR, and make sure settings are appropriate
        for (Service service : settings.getServices()) {
            for (Connector connector : service.getConnectors()) {
                if (connector instanceof HttpConnector) {
                    HttpConnector httpConnector = (HttpConnector) connector;
                    if (httpConnector.getProtocol().equals("org.apache.coyote.http11.Http11AprProtocol")) {
                        foundJavaAprConnector = true;
                    }
                }
            }
        }
        return foundJavaAprConnector;
    }
}
