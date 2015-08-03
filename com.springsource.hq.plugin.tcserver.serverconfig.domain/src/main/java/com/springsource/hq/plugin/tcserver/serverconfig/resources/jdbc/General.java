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

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General settings for data sources.
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "jdbc-general")
public class General implements Validator, Hierarchical<DataSource> {

    private String jndiName;

    private DataSource parent;

    @XmlAttribute(name = "jndi-name", required = true)
    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        General general = (General) target;
        if (!errors.hasFieldErrors("jndiName")) {
            if (!StringUtils.hasText(general.getJndiName())) {
                errors.rejectValue("jndiName", "resource.dataSource.general.jndiName.required");
            } else {
                if (general.parent() != null) {
                    // detect duplicate jndi names
                    for (DataSource dataSource : general.parent().parent().getDataSources()) {
                        General g = dataSource.getGeneral();
                        if (g != general && ObjectUtils.nullSafeEquals(general.getJndiName(), g.getJndiName())) {
                            errors.reject("resource.dataSource.general.jndiName.unique", new Object[] { general.getJndiName() }, null);
                        }
                    }
                }
            }
        }
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public DataSource parent() {
        return parent;
    }

    public void setParent(DataSource parent) {
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
        return ObjectUtils.nullSafeEquals(this.getJndiName(), general.getJndiName());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.jndiName) * 29;
    }

}
