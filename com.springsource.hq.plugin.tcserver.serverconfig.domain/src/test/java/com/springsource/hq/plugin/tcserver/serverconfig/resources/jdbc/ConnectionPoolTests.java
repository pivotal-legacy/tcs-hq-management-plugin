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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;

/**
 * Unit tests for {@link ConnectionPool}
 */
public class ConnectionPoolTests {

    private ConnectionPool connectionPool;

    @Before
    public void setup() {
        connectionPool = new MockConnectionPool();
    }

    @Test
    public void testHierarchical() {
        assertTrue(connectionPool instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(connectionPool.parent());
    }

    @Test
    public void testParent_reflective() {
        DataSource dataSource = new MockDataSource();
        connectionPool.setParent(dataSource);
        assertSame(dataSource, connectionPool.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        connectionPool.applyParentToChildren();
    }

    private class MockConnectionPool extends ConnectionPool {

        public boolean supports(Class<?> clazz) {
            return false;
        }

        public void validate(Object target, Errors errors) {
            // no-op
        }

    }

}
