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
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Data source with a DBCP based connection pool.
 * 
 * @since 2.0
 */
@BindableAttributes
@XmlType(name = "dbcp-data-source")
public class DbcpDataSource extends DataSource {

    private DbcpConnectionPool connectionPool;

    public DbcpDataSource() {
        connectionPool = new DbcpConnectionPool();
    }

    @XmlElement(name = "connection-pool", required = true)
    public DbcpConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(DbcpConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        DbcpDataSource dataSource = (DbcpDataSource) target;
        errors.pushNestedPath("general");
        dataSource.getGeneral().validate(dataSource.getGeneral(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("connection");
        dataSource.getConnection().validate(dataSource.getConnection(), errors);
        errors.popNestedPath();
        errors.pushNestedPath("connectionPool");
        dataSource.getConnectionPool().validate(dataSource.getConnectionPool(), errors);
        errors.popNestedPath();
    }

    public void applyParentToChildren() {
        connectionPool.setParent(this);
        connectionPool.applyParentToChildren();
        getConnection().setParent(this);
        getConnection().applyParentToChildren();
        getGeneral().setParent(this);
        getGeneral().applyParentToChildren();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DbcpDataSource)) {
            return false;
        }
        DbcpDataSource dataSource = (DbcpDataSource) obj;
        return ObjectUtils.nullSafeEquals(this.getConnection(), dataSource.getConnection())
            && ObjectUtils.nullSafeEquals(this.getConnectionPool(), dataSource.getConnectionPool())
            && ObjectUtils.nullSafeEquals(this.getGeneral(), dataSource.getGeneral());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.connectionPool) * 29 + ObjectUtils.nullSafeHashCode(this.getGeneral()) * 29
            + ObjectUtils.nullSafeHashCode(this.getConnection()) * 29;
    }

}
