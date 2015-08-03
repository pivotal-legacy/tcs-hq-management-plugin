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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;

/**
 * Holder for environmental configuration options
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "environment", propOrder = { "javaHome", "jvmOptions" })
public class Environment implements Validator, Hierarchical<Configuration> {

    private String javaHome;

    private JvmOptions jvmOptions;

    private Configuration parent;

    public Environment() {
        javaHome = null;
        jvmOptions = new JvmOptions();
    }

    @XmlAttribute(name = "java-home")
    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    @XmlElement(name = "jvm-options", required = true)
    public JvmOptions getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(JvmOptions jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Environment environment = (Environment) target;
        errors.pushNestedPath("jvmOptions");
        environment.getJvmOptions().validate(environment.getJvmOptions(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        jvmOptions.setParent(this);
        jvmOptions.applyParentToChildren();
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
        if (!(obj instanceof Environment)) {
            return false;
        }
        Environment environment = (Environment) obj;
        return ObjectUtils.nullSafeEquals(this.getJavaHome(), environment.getJavaHome())
            && ObjectUtils.nullSafeEquals(this.getJvmOptions(), environment.getJvmOptions());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.javaHome) * 29 + ObjectUtils.nullSafeHashCode(this.jvmOptions) * 29;
    }

}
