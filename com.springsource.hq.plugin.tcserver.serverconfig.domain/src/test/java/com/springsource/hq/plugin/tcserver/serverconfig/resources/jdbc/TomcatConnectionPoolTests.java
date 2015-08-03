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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.ConnectionPool;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatConnectionPool;

/**
 * Unit tests for {@link TomcatConnectionPool}
 */
public class TomcatConnectionPoolTests {

    private TomcatConnectionPool tomcatConnectionPool;

    @Before
    public void setup() {
        tomcatConnectionPool = new TomcatConnectionPool();
    }

    @Test
    public void testConnectionPool() {
        assertTrue(tomcatConnectionPool instanceof ConnectionPool);
    }

    @Test
    public void testValidator() {
        assertTrue(tomcatConnectionPool instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(tomcatConnectionPool.supports(tomcatConnectionPool.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(tomcatConnectionPool, "staticDefaults");
        tomcatConnectionPool.validate(tomcatConnectionPool, errors);
        assertFalse(errors.hasErrors());
    }

}
