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

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Settings for AJP based connectors (http://tomcat.apache.org/tomcat-6.0-doc/config/ajp.html)
 * 
 * <p>
 * Java and APR AJP connectors are supported
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "ajp-connector")
public class AjpConnector extends Connector implements Validator {

    private static final String DEFAULT_PROTOCOL = "org.apache.coyote.ajp.AjpProtocol";

    private static final Boolean DEFAULT_REQUEST_USE_SECRET = Boolean.FALSE;

    /**
     * This attribute value must be AJP/1.3 to use the AJP handler.
     */
    private String protocol = DEFAULT_PROTOCOL;

    /**
     * Only requests from workers with this secret keyword will be accepted.
     */
    private String requestSecret;

    /**
     * If set to true, then a random value for request.secret will be generated. It is for use with
     * request.shutdownEnabled. This is set to false by default.
     */
    private Boolean requestUseSecret = DEFAULT_REQUEST_USE_SECRET;

    @XmlAttribute(name = "protocol")
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @XmlAttribute(name = "request-secret")
    public String getRequestSecret() {
        return requestSecret;
    }

    public void setRequestSecret(String requestSecret) {
        this.requestSecret = requestSecret;
    }

    @XmlAttribute(name = "request-use-secret")
    public Boolean getRequestUseSecret() {
        return requestUseSecret;
    }

    public void setRequestUseSecret(Boolean requestUseSecret) {
        this.requestUseSecret = requestUseSecret;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AjpConnector ajpConnector = (AjpConnector) target;
        super.validate(ajpConnector, errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AjpConnector)) {
            return false;
        }
        AjpConnector connector = (AjpConnector) obj;
        return ObjectUtils.nullSafeEquals(this.getProtocol(), connector.getProtocol())
            && ObjectUtils.nullSafeEquals(this.getRequestSecret(), connector.getRequestSecret())
            && ObjectUtils.nullSafeEquals(this.getRequestUseSecret(), connector.getRequestUseSecret())
            && ObjectUtils.nullSafeEquals(this.getAddress(), connector.getAddress())
            && ObjectUtils.nullSafeEquals(this.getConnectionTimeout(), connector.getConnectionTimeout())
            && ObjectUtils.nullSafeEquals(this.getConnectorName(), connector.getConnectorName())
            && ObjectUtils.nullSafeEquals(this.getMaxThreads(), connector.getMaxThreads())
            && ObjectUtils.nullSafeEquals(this.getPort(), connector.getPort())
            && ObjectUtils.nullSafeEquals(this.getProxyName(), connector.getProxyName())
            && ObjectUtils.nullSafeEquals(this.getProxyPort(), connector.getProxyPort())
            && ObjectUtils.nullSafeEquals(this.getRedirectPort(), connector.getRedirectPort())
            && ObjectUtils.nullSafeEquals(this.getScheme(), connector.getScheme());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.protocol) + ObjectUtils.nullSafeHashCode(this.requestSecret)
            + ObjectUtils.nullSafeHashCode(this.requestUseSecret) + super.hashCode() * 29;
    }

}
