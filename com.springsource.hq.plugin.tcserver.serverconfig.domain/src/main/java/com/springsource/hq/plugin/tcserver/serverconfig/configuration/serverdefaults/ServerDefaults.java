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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for static and dynamic server defaults
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "server-defaults", propOrder = { "jspDefaults", "staticDefaults" })
public class ServerDefaults implements Validator, Hierarchical<Configuration> {

    private JspDefaults jspDefaults;

    private StaticDefaults staticDefaults;

    private Configuration parent;

    public ServerDefaults() {
        jspDefaults = new JspDefaults();
        staticDefaults = new StaticDefaults();
    }

    @XmlElement(name = "jsp-defaults", required = true)
    public JspDefaults getJspDefaults() {
        return jspDefaults;
    }

    public void setJspDefaults(JspDefaults jspDefaults) {
        this.jspDefaults = jspDefaults;
    }

    @XmlElement(name = "static-defaults", required = true)
    public StaticDefaults getStaticDefaults() {
        return staticDefaults;
    }

    public void setStaticDefaults(StaticDefaults staticDefaults) {
        this.staticDefaults = staticDefaults;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ServerDefaults serverDefaults = (ServerDefaults) target;
        errors.pushNestedPath("jspDefaults");
        serverDefaults.getJspDefaults().validate(serverDefaults.getJspDefaults(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("staticDefaults");
        serverDefaults.getStaticDefaults().validate(serverDefaults.getStaticDefaults(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        jspDefaults.setParent(this);
        jspDefaults.applyParentToChildren();
        staticDefaults.setParent(this);
        staticDefaults.applyParentToChildren();
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
        if (!(obj instanceof ServerDefaults)) {
            return false;
        }
        ServerDefaults serverDefaults = (ServerDefaults) obj;
        return ObjectUtils.nullSafeEquals(this.getJspDefaults(), serverDefaults.getJspDefaults())
            && ObjectUtils.nullSafeEquals(this.getStaticDefaults(), serverDefaults.getStaticDefaults());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.jspDefaults) * 29 + ObjectUtils.nullSafeHashCode(this.staticDefaults) * 29;
    }

}
