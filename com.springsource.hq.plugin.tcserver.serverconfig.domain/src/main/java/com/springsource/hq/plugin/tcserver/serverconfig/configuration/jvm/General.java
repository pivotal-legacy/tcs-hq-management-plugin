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
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General JVM options
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jvm-general")
public class General implements Validator, Hierarchical<JvmOptions> {

    private Boolean server;

    private JvmOptions parent;

    @XmlAttribute(name = "server")
    public Boolean getServer() {
        return server;
    }

    public void setServer(Boolean server) {
        this.server = server;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // General general = (General) target;
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
        if (!(obj instanceof General)) {
            return false;
        }
        General general = (General) obj;
        return ObjectUtils.nullSafeEquals(this.getServer(), general.getServer());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.server) * 29;
    }

}
