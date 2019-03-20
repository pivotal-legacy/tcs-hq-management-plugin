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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for web app logging (https://tomcat.apache.org/tomcat-6.0-doc/config/context.html)
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "web-application-logger")
public class WebApplicationLogger implements Validator, Hierarchical<ContextContainer> {

    private static final boolean DEFAULT_SWALLOW_OUTPUT = false;

    private Boolean swallowOutput = DEFAULT_SWALLOW_OUTPUT;

    private ContextContainer parent;

    @XmlAttribute(name = "swallow-output")
    public Boolean getSwallowOutput() {
        return swallowOutput;
    }

    public void setSwallowOutput(Boolean swallowOutput) {
        this.swallowOutput = swallowOutput;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // WebApplicationLogger webApplicationLogger = (WebApplicationLogger) target;
        // TODO define validation rules
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public ContextContainer parent() {
        return parent;
    }

    public void setParent(ContextContainer parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WebApplicationLogger)) {
            return false;
        }
        WebApplicationLogger webApplicationLogger = (WebApplicationLogger) obj;
        return ObjectUtils.nullSafeEquals(this.getSwallowOutput(), webApplicationLogger.getSwallowOutput());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.swallowOutput) * 29;
    }

}
