/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

package com.springsource.hq.plugin.tcserver.serverconfig.web.controllers;

import java.io.UnsupportedEncodingException;

import org.springframework.validation.Errors;
import org.springframework.web.util.UriUtils;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.General;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;

final class HumanIdEncodingTomcatDataSource extends TomcatDataSource {

    private final TomcatDataSource delegate;

    public HumanIdEncodingTomcatDataSource(TomcatDataSource delegate) {
        this.delegate = delegate;
    }

    public TomcatConnectionPool getConnectionPool() {
        return delegate.getConnectionPool();
    }

    public void setConnectionPool(TomcatConnectionPool connectionPool) {
        delegate.setConnectionPool(connectionPool);
    }

    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return delegate.supports(clazz);
    }

    public General getGeneral() {
        return delegate.getGeneral();
    }

    public void setGeneral(General general) {
        delegate.setGeneral(general);
    }

    public void validate(Object target, Errors errors) {
        delegate.validate(target, errors);
    }

    public Connection getConnection() {
        return delegate.getConnection();
    }

    public void setConnection(Connection connection) {
        delegate.setConnection(connection);
    }

    public Settings parent() {
        return delegate.parent();
    }

    public void setParent(Settings parent) {
        delegate.setParent(parent);
    }

    public String getHumanId() {
        try {
            return UriUtils.encodePathSegment(delegate.getHumanId(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return delegate.getId();
    }

    public void applyParentToChildren() {
        delegate.applyParentToChildren();
    }

    public void setId(String id) {
        delegate.setId(id);
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public String toString() {
        return delegate.toString();
    }

    TomcatDataSource getDelegate() {
        return this.delegate;
    }

    public boolean isGatherDiagnostics() {
        return this.delegate.isGatherDiagnostics();
    }

    public void setGatherDiagnostics(boolean requestDiagnostics) {
        this.delegate.setGatherDiagnostics(requestDiagnostics);
    }
}
