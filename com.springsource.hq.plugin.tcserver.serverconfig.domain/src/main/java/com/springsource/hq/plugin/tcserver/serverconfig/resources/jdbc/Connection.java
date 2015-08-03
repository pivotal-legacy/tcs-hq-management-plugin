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

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Settings for basic JDBC connections (http://tomcat.apache.org/tomcat-6.0-doc/jndi-resources-howto.html)
 * 
 * @since 2.0
 */
@BindableAttributes
public class Connection implements Validator, Hierarchical<DataSource> {

    /**
     * This string represents the masked version of the password displayed on screens, in order to hide the existing
     * password.
     */
    private final static String OBSCURED_PASSWORD = "****************";

    /**
     * This string represents the masked, empty version of the password, displayed when first creating an entry.
     */
    private final static String EMPTY_PASSWORD = "";

    /**
     * The connection username to be passed to our JDBC driver to establish a connection.
     */
    private String username;

    /**
     * The connection password to be passed to our JDBC driver to establish a connection.
     */
    private String password;

    /**
     * The connection URL to be passed to our JDBC driver to establish a connection.
     */
    private String url;

    /**
     * The fully qualified Java class name of the JDBC driver to be used.
     */
    private String driverClassName;

    /**
     * The connection properties that will be sent to our JDBC driver when establishing new connections. Format of the
     * string must be [propertyName=property;]*
     * 
     * <p>
     * NOTE - The "user" and "password" properties will be passed explicitly, so they do not need to be included here.
     */
    private String connectionProperties;

    private DataSource parent;

    private boolean hasEnteredEmptyPassword = false;

    @XmlAttribute(name = "username", required = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlAttribute(name = "password", required = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getObscuredPassword() {
        if (password == null || password.equals("")) {
            return EMPTY_PASSWORD;
        } else {
            return OBSCURED_PASSWORD;
        }
    }

    public void setObscuredPassword(String password) {
        /**
         * If value is the obscured password, then ignore it. This will end up leaving password empty. Since this is
         * indistinguishable from the user entering an empty string, a boolean flag is switched on to support the
         * validation logic.
         */
        if (!password.equals(OBSCURED_PASSWORD)) {
            setPassword(password);
        }

        if (password.equals("")) {
            hasEnteredEmptyPassword = true;
        }
    }

    /**
     * Since a new Connection object is created every time the controller is exercised, the password has to be brought
     * forth from the old connection object if there is no new password.
     * 
     * @param oldConnection
     */
    public void bringForwardOldPassword(Connection oldConnection) {
        if (getPassword() != null && !getPassword().equals("") && !getPassword().equals(OBSCURED_PASSWORD)) {
            return;
        }
        String oldPassword = oldConnection.getPassword();
        setPassword(oldPassword);
    }

    @XmlAttribute(name = "url", required = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlAttribute(name = "driver-class-name", required = true)
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @XmlAttribute(name = "connection-properties")
    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Connection connection = (Connection) target;
        if (!errors.hasFieldErrors("username")) {
            if (connection.getUsername() == null) {
                errors.rejectValue("username", "resource.dataSource.connection.username.required");
            }
        }
        if (!errors.hasFieldErrors("obscuredPassword")) {
            if (connection.hasEnteredEmptyPassword) {
                errors.rejectValue("obscuredPassword", "resource.dataSource.connection.password.required");
            }
        }
        if (!errors.hasFieldErrors("url")) {
            if (!StringUtils.hasText(connection.getUrl())) {
                errors.rejectValue("url", "resource.dataSource.connection.url.required");
            }
        }
        if (!errors.hasFieldErrors("driverClassName")) {
            if (!StringUtils.hasText(connection.getDriverClassName())) {
                errors.rejectValue("driverClassName", "resource.dataSource.connection.driverClassName.required");
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
        if (!(obj instanceof Connection)) {
            return false;
        }
        Connection connection = (Connection) obj;
        return ObjectUtils.nullSafeEquals(this.getConnectionProperties(), connection.getConnectionProperties())
            && ObjectUtils.nullSafeEquals(this.getDriverClassName(), connection.getDriverClassName())
            && ObjectUtils.nullSafeEquals(this.getPassword(), connection.getPassword())
            && ObjectUtils.nullSafeEquals(this.getUrl(), connection.getUrl())
            && ObjectUtils.nullSafeEquals(this.getUsername(), connection.getUsername());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.connectionProperties) * 29 + ObjectUtils.nullSafeHashCode(this.driverClassName) * 29
            + ObjectUtils.nullSafeHashCode(this.password) * 29 + ObjectUtils.nullSafeHashCode(this.url) * 29
            + ObjectUtils.nullSafeHashCode(this.username) * 29;
    }

}
