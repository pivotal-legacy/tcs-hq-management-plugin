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

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import javax.xml.bind.annotation.XmlAttribute;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for logging valve. (http://tomcat.apache.org/tomcat-6.0-doc/config/valve.html)
 * 
 * <p>
 * Can be used for {@link Host} and {@link Engine} level logging valves.
 * 
 * @since 2.0
 * 
 * @param <P> the parent class
 */
@BindableAttributes
public class Logging<P extends Hierarchical<?>> implements Validator, Hierarchical<P> {

    private static final String DEFAULT_DIRECTORY = "logs";

    private static final Boolean DEFAULT_ENABLED = false;

    private static final String DEFAULT_PREFIX = "access_log";

    private static final String DEFAULT_SUFFIX = "";

    /**
     * Absolute or relative pathname of a directory in which log files created by this valve will be placed. If a
     * relative path is specified, it is interpreted as relative to $CATALINA_BASE. If no directory attribute is
     * specified, the default value is "logs" (relative to $CATALINA_BASE).
     */
    private String directory = DEFAULT_DIRECTORY;

    /**
     * Enables or disables the access log valve. This would equate to adding or removing the valve entry from the server
     * configuration
     */
    private Boolean enabled = DEFAULT_ENABLED;

    /**
     * Allows a customized date format in the access log file name. The date format also decides how often the file is
     * rotated. If you wish to rotate every hour, then set this value to: yyyy-MM-dd.HH
     */
    private String fileDateFormat;

    private P parent;

    /**
     * A formatting layout identifying the various information fields from the request and response to be logged, or the
     * word common or combined to select a standard format. See below for more information on configuring this
     * attribute. Note that the optimized access does only support common and combined as the value for this attribute.
     */
    private String pattern;

    /**
     * The prefix added to the start of each log file's name. If not specified, the default value is "access_log.". To
     * specify no prefix, use a zero-length string.
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * The suffix added to the end of each log file's name. If not specified, the default value is "". To specify no
     * suffix, use a zero-length string.
     */
    private String suffix = DEFAULT_SUFFIX;

    public void applyParentToChildren() {
        // no-op, no children
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Logging)) {
            return false;
        }
        Logging<?> logging = (Logging<?>) obj;
        return ObjectUtils.nullSafeEquals(this.getDirectory(), logging.getDirectory())
            && ObjectUtils.nullSafeEquals(this.getFileDateFormat(), logging.getFileDateFormat())
            && ObjectUtils.nullSafeEquals(this.getPattern(), logging.getPattern())
            && ObjectUtils.nullSafeEquals(this.getPrefix(), logging.getPrefix()) && ObjectUtils.nullSafeEquals(this.getSuffix(), logging.getSuffix());
    }

    @XmlAttribute(name = "directory")
    public String getDirectory() {
        return directory;
    }

    @XmlAttribute(name = "enabled", required = true)
    public Boolean getEnabled() {
        return enabled;
    }

    @XmlAttribute(name = "file-date-format")
    public String getFileDateFormat() {
        return fileDateFormat;
    }

    @XmlAttribute(name = "pattern")
    public String getPattern() {
        return pattern;
    }

    @XmlAttribute(name = "prefix")
    public String getPrefix() {
        return prefix;
    }

    @XmlAttribute(name = "suffix")
    public String getSuffix() {
        return suffix;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.directory) * 29 + ObjectUtils.nullSafeHashCode(this.fileDateFormat) * 29
            + ObjectUtils.nullSafeHashCode(this.pattern) * 29 + ObjectUtils.nullSafeHashCode(this.prefix) * 29
            + ObjectUtils.nullSafeHashCode(this.suffix) * 29;
    }

    public P parent() {
        return parent;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setFileDateFormat(String fileDateFormat) {
        this.fileDateFormat = fileDateFormat;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        // Logging<?> logging = (Logging<?>) target;
        // TODO determine validation rules
    }

}
