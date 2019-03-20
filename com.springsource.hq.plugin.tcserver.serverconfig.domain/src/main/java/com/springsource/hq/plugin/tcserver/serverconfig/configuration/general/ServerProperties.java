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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.general;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * General server properties (https://tomcat.apache.org/tomcat-6.0-doc/config/server.html)
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "server-properties")
public class ServerProperties implements Validator, Hierarchical<GeneralConfig> {

    private static final long DEFAULT_PORT = 8005l;

    private static final String DEFAULT_SHUTDOWN = "SHUTDOWN";

    private Long port = DEFAULT_PORT;

    private String shutdown = DEFAULT_SHUTDOWN;

    private GeneralConfig parent;

    @XmlAttribute(name = "port")
    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    @XmlAttribute(name = "shutdown")
    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ServerProperties serverProperties = (ServerProperties) target;
        if (!errors.hasFieldErrors("port")) {
            if (serverProperties.getPort() == null) {
                errors.rejectValue("port", "configuration.generalConfig.serverProperties.port.required");
            } else if (serverProperties.getPort() < -1 || serverProperties.getPort() > 65535) {
                errors.rejectValue("port", "invalid.shutdownPort");
            }
        }
        if (!errors.hasFieldErrors("shutdown")) {
            if (!StringUtils.hasText(serverProperties.getShutdown())) {
                errors.rejectValue("shutdown", "configuration.generalConfig.serverProperties.shutdown.required");
            }
        }
    }

    public void applyParentToChildren() {
        // no-op, no children
    }

    public GeneralConfig parent() {
        return parent;
    }

    public void setParent(GeneralConfig parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServerProperties)) {
            return false;
        }
        ServerProperties serverProperties = (ServerProperties) obj;
        return ObjectUtils.nullSafeEquals(this.getPort(), serverProperties.getPort())
            && ObjectUtils.nullSafeEquals(this.getShutdown(), serverProperties.getShutdown());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.port) * 29 + ObjectUtils.nullSafeHashCode(this.shutdown) * 29;
    }

}
