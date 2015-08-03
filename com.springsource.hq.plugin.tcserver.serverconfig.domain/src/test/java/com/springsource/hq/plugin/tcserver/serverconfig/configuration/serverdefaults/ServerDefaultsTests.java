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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.Configuration;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;

/**
 * Unit tests for {@link ServerDefaults}
 */
public class ServerDefaultsTests {

    private ServerDefaults serverDefaults;

    @Before
    public void setup() {
        serverDefaults = new ServerDefaults();
    }

    @Test
    public void testHierarchical() {
        assertTrue(serverDefaults instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(serverDefaults.parent());
    }

    @Test
    public void testParent_reflective() {
        Configuration configuration = new Configuration();
        serverDefaults.setParent(configuration);
        assertSame(configuration, serverDefaults.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        serverDefaults.applyParentToChildren();
        assertSame(serverDefaults, serverDefaults.getJspDefaults().parent());
        assertSame(serverDefaults, serverDefaults.getStaticDefaults().parent());
    }

    @Test
    public void testValidator() {
        assertTrue(serverDefaults instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(serverDefaults.supports(serverDefaults.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(serverDefaults, "serverDefaults");
        serverDefaults.validate(serverDefaults, errors);
        assertFalse(errors.hasErrors());
    }

}
