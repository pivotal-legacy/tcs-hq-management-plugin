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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * Unit tests for {@link DbcpConnectionPool}
 */
public class DbcpConnectionPoolTests {

    private DbcpConnectionPool dbcpConnectionPool;

    @Before
    public void setup() {
        dbcpConnectionPool = new DbcpConnectionPool();
    }

    @Test
    public void testConnectionPool() {
        assertTrue(dbcpConnectionPool instanceof ConnectionPool);
    }

    @Test
    public void testValidator() {
        assertTrue(dbcpConnectionPool instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(dbcpConnectionPool.supports(dbcpConnectionPool.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dbcpConnectionPool, "dbcpConnectionPool");
        dbcpConnectionPool.validate(dbcpConnectionPool, errors);
        assertEquals(0, errors.getFieldErrorCount());
        // assertEquals("resource.dataSource.connectionPool.validationQuery.required",
        // errors.getFieldError("validationQuery").getCode());
    }

}
