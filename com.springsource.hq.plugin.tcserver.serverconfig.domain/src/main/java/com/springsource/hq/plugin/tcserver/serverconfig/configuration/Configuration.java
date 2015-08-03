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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

/**
 * Holder for all configuration related settings
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "configuration", propOrder = { "generalConfig", "environment", "contextContainer", "serverDefaults" })
public class Configuration implements Validator, Hierarchical<Settings> {

    private GeneralConfig generalConfig;

    private Environment environment;

    private ContextContainer contextContainer;

    private ServerDefaults serverDefaults;

    private Settings parent;

    public Configuration() {
        generalConfig = new GeneralConfig();
        environment = new Environment();
        contextContainer = new ContextContainer();
        serverDefaults = new ServerDefaults();
    }

    @XmlElement(name = "general-config", required = true)
    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    @XmlElement(name = "environment", required = true)
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @XmlElement(name = "context-container", required = true)
    public ContextContainer getContextContainer() {
        return contextContainer;
    }

    public void setContextContainer(ContextContainer contextContainer) {
        this.contextContainer = contextContainer;
    }

    @XmlElement(name = "server-defaults", required = true)
    public ServerDefaults getServerDefaults() {
        return serverDefaults;
    }

    public void setServerDefaults(ServerDefaults serverDefaults) {
        this.serverDefaults = serverDefaults;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Configuration configuration = (Configuration) target;
        errors.pushNestedPath("generalConfig");
        configuration.getGeneralConfig().validate(configuration.getGeneralConfig(), errors);
        errors.popNestedPath();
        if (configuration.getEnvironment() != null) {
            errors.pushNestedPath("environment");
            configuration.getEnvironment().validate(configuration.getEnvironment(), errors);
            errors.popNestedPath();
        }
        errors.pushNestedPath("contextContainer");
        configuration.getContextContainer().validate(configuration.getContextContainer(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("serverDefaults");
        configuration.getServerDefaults().validate(configuration.getServerDefaults(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        contextContainer.setParent(this);
        contextContainer.applyParentToChildren();
        generalConfig.setParent(this);
        generalConfig.applyParentToChildren();
        if (environment != null) {
            environment.setParent(this);
            environment.applyParentToChildren();
        }
        serverDefaults.setParent(this);
        serverDefaults.applyParentToChildren();
    }

    public Settings parent() {
        return parent;
    }

    public void setParent(Settings parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Configuration)) {
            return false;
        }
        Configuration configuration = (Configuration) obj;
        return ObjectUtils.nullSafeEquals(this.getContextContainer(), configuration.getContextContainer())
            && ObjectUtils.nullSafeEquals(this.getGeneralConfig(), configuration.getGeneralConfig())
            && ObjectUtils.nullSafeEquals(this.getEnvironment(), configuration.getEnvironment())
            && ObjectUtils.nullSafeEquals(this.getServerDefaults(), configuration.getServerDefaults());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.contextContainer) * 29 + ObjectUtils.nullSafeHashCode(this.generalConfig) * 29
            + ObjectUtils.nullSafeHashCode(this.environment) * 29 + ObjectUtils.nullSafeHashCode(this.serverDefaults) * 29;
    }

}
