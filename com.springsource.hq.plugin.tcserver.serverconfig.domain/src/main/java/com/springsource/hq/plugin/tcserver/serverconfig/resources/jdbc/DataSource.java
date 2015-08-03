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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;
import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.Identity;
import com.springsource.hq.plugin.tcserver.serverconfig.Settings;

/**
 * Base class for specific data source implementations. Contains commons elements shared by all data sources.
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "data-source", propOrder = { "general", "connection" })
public abstract class DataSource implements Validator, Hierarchical<Settings>, Identity {

    private General general;

    private Connection connection;

    private Settings parent;

    private String id;

    public DataSource() {
        this.general = new General();
        this.connection = new Connection();
    }

    @XmlElement(name = "jdbc-general", required = true)
    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    @XmlElement(name = "connection", required = true)
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Settings parent() {
        return parent;
    }

    public void setParent(Settings parent) {
        this.parent = parent;
    }

    @XmlTransient
    public String getHumanId() {
        return general.getJndiName();
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataSource)) {
            return false;
        }
        DataSource dataSource = (DataSource) obj;
        return ObjectUtils.nullSafeEquals(this.getConnection(), dataSource.getConnection())
            && ObjectUtils.nullSafeEquals(this.getGeneral(), dataSource.getGeneral());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.connection) * 29 + ObjectUtils.nullSafeHashCode(this.general) * 29;
    }

}
