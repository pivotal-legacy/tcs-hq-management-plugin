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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.context;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for context container settings
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "context-container", propOrder = { "staticResourceCache", "webApplicationLogger" })
public class ContextContainer implements Validator, Hierarchical<Configuration> {

    private StaticResourceCache staticResourceCache;

    private WebApplicationLogger webApplicationLogger;

    private Configuration parent;

    public ContextContainer() {
        staticResourceCache = new StaticResourceCache();
        webApplicationLogger = new WebApplicationLogger();
    }

    @XmlElement(name = "static-resource-cache", required = true)
    public StaticResourceCache getStaticResourceCache() {
        return staticResourceCache;
    }

    public void setStaticResourceCache(StaticResourceCache staticResourceCache) {
        this.staticResourceCache = staticResourceCache;
    }

    @XmlElement(name = "web-application-logger", required = true)
    public WebApplicationLogger getWebApplicationLogger() {
        return webApplicationLogger;
    }

    public void setWebApplicationLogger(WebApplicationLogger webApplicationLogger) {
        this.webApplicationLogger = webApplicationLogger;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ContextContainer contextContainer = (ContextContainer) target;
        errors.pushNestedPath("staticResourceCache");
        contextContainer.getStaticResourceCache().validate(contextContainer.getStaticResourceCache(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("webApplicationLogger");
        contextContainer.getWebApplicationLogger().validate(contextContainer.getWebApplicationLogger(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        staticResourceCache.setParent(this);
        staticResourceCache.applyParentToChildren();
        webApplicationLogger.setParent(this);
        webApplicationLogger.applyParentToChildren();
    }

    public Configuration parent() {
        return parent;
    }

    public void setParent(Configuration parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContextContainer)) {
            return false;
        }
        ContextContainer contextContainer = (ContextContainer) obj;
        return ObjectUtils.nullSafeEquals(this.getStaticResourceCache(), contextContainer.getStaticResourceCache())
            && ObjectUtils.nullSafeEquals(this.getWebApplicationLogger(), contextContainer.getWebApplicationLogger());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.staticResourceCache) * 29 + ObjectUtils.nullSafeHashCode(this.webApplicationLogger) * 29;
    }

}
