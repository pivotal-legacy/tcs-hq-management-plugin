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
 * Garbage collection related JVM options
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "garbage-collection")
public class GarbageCollection implements Validator, Hierarchical<JvmOptions> {

    private Long maxGCPauseMillis;

    private Long maxGCMinorPauseMillis;

    private JvmOptions parent;

    @XmlAttribute(name = "max-gc-pause-millis")
    public Long getMaxGCPauseMillis() {
        return maxGCPauseMillis;
    }

    public void setMaxGCPauseMillis(Long maxGCPauseMillis) {
        this.maxGCPauseMillis = maxGCPauseMillis;
    }

    @XmlAttribute(name = "max-gc-minor-pause-millis")
    public Long getMaxGCMinorPauseMillis() {
        return maxGCMinorPauseMillis;
    }

    public void setMaxGCMinorPauseMillis(Long maxGCMinorPauseMillis) {
        this.maxGCMinorPauseMillis = maxGCMinorPauseMillis;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        GarbageCollection garbageCollection = (GarbageCollection) target;

        // max gc pause
        if (garbageCollection.getMaxGCPauseMillis() != null) {
            if (garbageCollection.getMaxGCPauseMillis() < 1) {
                errors.rejectValue("maxGCPauseMillis", "configuration.startup.garbageCollection.maxGCPauseMillis.tooLow");
            }
        }

        // max gc pause
        if (garbageCollection.getMaxGCMinorPauseMillis() != null) {
            if (garbageCollection.getMaxGCMinorPauseMillis() < 1) {
                errors.rejectValue("maxGCMinorPauseMillis", "configuration.startup.garbageCollection.maxGCMinorPauseMillis.tooLow");
            }
        }

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
        if (!(obj instanceof GarbageCollection)) {
            return false;
        }
        GarbageCollection garbageCollection = (GarbageCollection) obj;
        return ObjectUtils.nullSafeEquals(this.getMaxGCMinorPauseMillis(), garbageCollection.getMaxGCMinorPauseMillis())
            && ObjectUtils.nullSafeEquals(this.getMaxGCPauseMillis(), garbageCollection.getMaxGCPauseMillis());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.maxGCMinorPauseMillis) * 29 + ObjectUtils.nullSafeHashCode(this.maxGCPauseMillis) * 29;
    }

}
