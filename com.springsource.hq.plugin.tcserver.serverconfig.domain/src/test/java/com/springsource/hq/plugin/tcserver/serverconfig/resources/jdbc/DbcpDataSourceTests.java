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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * Unit tests for {@link DbcpDataSource}
 */
public class DbcpDataSourceTests {

    private DbcpDataSource dbcpDataSource;

    @Before
    public void setup() {
        dbcpDataSource = new DbcpDataSource();
        dbcpDataSource.getConnection().setObscuredPassword("");
    }

    @Test
    public void testDataSource() {
        assertTrue(dbcpDataSource instanceof DataSource);
    }

    @Test
    public void testApplyParentToChildren() {
        dbcpDataSource.applyParentToChildren();
        assertSame(dbcpDataSource, dbcpDataSource.getConnection().parent());
        assertSame(dbcpDataSource, dbcpDataSource.getGeneral().parent());
        assertSame(dbcpDataSource, dbcpDataSource.getConnectionPool().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(dbcpDataSource instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(dbcpDataSource.supports(dbcpDataSource.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dbcpDataSource, "dbcpDataSource");
        dbcpDataSource.validate(dbcpDataSource, errors);
        assertEquals(5, errors.getFieldErrorCount());
        assertEquals("resource.dataSource.general.jndiName.required", errors.getFieldError("general.jndiName").getCode());
        assertEquals("resource.dataSource.connection.username.required", errors.getFieldError("connection.username").getCode());
        assertEquals("resource.dataSource.connection.password.required", errors.getFieldError("connection.obscuredPassword").getCode());
        assertEquals("resource.dataSource.connection.url.required", errors.getFieldError("connection.url").getCode());
        assertEquals("resource.dataSource.connection.driverClassName.required", errors.getFieldError("connection.driverClassName").getCode());
        // assertEquals("resource.dataSource.connectionPool.validationQuery.required",
        // errors.getFieldError("connectionPool.validationQuery").getCode());
    }

}
