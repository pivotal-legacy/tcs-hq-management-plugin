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

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Advanced JVM options allowing the user to specify their own command line arguments
 * 
 * @since 2.0
 */
@BindableAttributes
public class Advanced implements Validator, Hierarchical<JvmOptions> {

    private String cliArgs;

    private JvmOptions parent;

    @XmlAttribute(name = "cli-args")
    public String getCliArgs() {
        return cliArgs;
    }

    public void setCliArgs(String cliArgs) {
        this.cliArgs = cliArgs;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // Advanced advanced = (Advanced) target;
        // TODO define validation rules
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public JvmOptions parent() {
        return parent;
    }

    public void setParent(JvmOptions parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Advanced)) {
            return false;
        }
        Advanced advanced = (Advanced) obj;
        return ObjectUtils.nullSafeEquals(this.getCliArgs(), advanced.getCliArgs());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.cliArgs) * 29;
    }

}
