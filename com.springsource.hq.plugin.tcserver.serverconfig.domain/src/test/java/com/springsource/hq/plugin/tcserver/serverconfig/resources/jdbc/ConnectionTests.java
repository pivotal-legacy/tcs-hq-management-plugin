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

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.Connection;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DataSource;

/**
 * Unit tests for {@link Connection}
 */
public class ConnectionTests {

    private Connection connection;

    @Before
    public void setup() {
        connection = new Connection();
    }

    @Test
    public void testHierarchical() {
        assertTrue(connection instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(connection.parent());
    }

    @Test
    public void testParent_reflective() {
        DataSource dataSource = new MockDataSource();
        connection.setParent(dataSource);
        assertSame(dataSource, connection.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        connection.applyParentToChildren();
    }

}
