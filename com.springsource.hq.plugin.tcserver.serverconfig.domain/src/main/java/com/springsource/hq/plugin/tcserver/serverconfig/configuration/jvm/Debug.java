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
 * Debugging related JVM options
 * 
 * @since 2.0
 */
@BindableAttributes
public class Debug implements Validator, Hierarchical<JvmOptions> {

    private Boolean heapDumpOnOutOfMemoryError;

    private Boolean printGC;

    private Boolean printHeapAtGC;

    private Boolean printGCApplicationStoppedTime;

    private Boolean printGCTimeStamps;

    private Boolean printGCDetails;

    private String loggc;

    private JvmOptions parent;

    @XmlAttribute(name = "heap-hump-on-out-of-memory-error")
    public Boolean getHeapDumpOnOutOfMemoryError() {
        return heapDumpOnOutOfMemoryError;
    }

    public void setHeapDumpOnOutOfMemoryError(Boolean heapDumpOnOutOfMemoryError) {
        this.heapDumpOnOutOfMemoryError = heapDumpOnOutOfMemoryError;
    }

    @XmlAttribute(name = "print-gc")
    public Boolean getPrintGC() {
        return printGC;
    }

    public void setPrintGC(Boolean printGC) {
        this.printGC = printGC;
    }

    @XmlAttribute(name = "print-heap-at-gc")
    public Boolean getPrintHeapAtGC() {
        return printHeapAtGC;
    }

    public void setPrintHeapAtGC(Boolean printHeapAtGC) {
        this.printHeapAtGC = printHeapAtGC;
    }

    @XmlAttribute(name = "print-gc-application-stopped-time")
    public Boolean getPrintGCApplicationStoppedTime() {
        return printGCApplicationStoppedTime;
    }

    public void setPrintGCApplicationStoppedTime(Boolean printGCApplicationStoppedTime) {
        this.printGCApplicationStoppedTime = printGCApplicationStoppedTime;
    }

    @XmlAttribute(name = "print-gc-time-stamps")
    public Boolean getPrintGCTimeStamps() {
        return printGCTimeStamps;
    }

    public void setPrintGCTimeStamps(Boolean printGCTimeStamps) {
        this.printGCTimeStamps = printGCTimeStamps;
    }

    @XmlAttribute(name = "print-gc-details")
    public Boolean getPrintGCDetails() {
        return printGCDetails;
    }

    public void setPrintGCDetails(Boolean printGCDetails) {
        this.printGCDetails = printGCDetails;
    }

    @XmlAttribute(name = "loggc")
    public String getLoggc() {
        return loggc;
    }

    public void setLoggc(String loggc) {
        this.loggc = loggc;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // Debug debug = (Debug) target;
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
        if (!(obj instanceof Debug)) {
            return false;
        }
        Debug debug = (Debug) obj;
        return ObjectUtils.nullSafeEquals(this.getHeapDumpOnOutOfMemoryError(), debug.getHeapDumpOnOutOfMemoryError())
            && ObjectUtils.nullSafeEquals(this.getLoggc(), debug.getLoggc()) && ObjectUtils.nullSafeEquals(this.getPrintGC(), debug.getPrintGC())
            && ObjectUtils.nullSafeEquals(this.getPrintGCApplicationStoppedTime(), debug.getPrintGCApplicationStoppedTime())
            && ObjectUtils.nullSafeEquals(this.getPrintGCDetails(), debug.getPrintGCDetails())
            && ObjectUtils.nullSafeEquals(this.getPrintGCTimeStamps(), debug.getPrintGCTimeStamps())
            && ObjectUtils.nullSafeEquals(this.getPrintHeapAtGC(), debug.getPrintHeapAtGC());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.heapDumpOnOutOfMemoryError) * 29 + ObjectUtils.nullSafeHashCode(this.loggc) * 29
            + ObjectUtils.nullSafeHashCode(this.printGC) * 29 + ObjectUtils.nullSafeHashCode(this.printGCApplicationStoppedTime) * 29
            + ObjectUtils.nullSafeHashCode(this.printGCDetails) * 29 + ObjectUtils.nullSafeHashCode(this.printGCTimeStamps) * 29
            + ObjectUtils.nullSafeHashCode(this.printHeapAtGC) * 29;
    }

}
