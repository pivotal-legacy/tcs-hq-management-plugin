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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;

/**
 * Unit tests for {@link StaticDefaults}
 */
public class StaticDefaultsTests {

    private StaticDefaults staticDefaults;

    @Before
    public void setup() {
        staticDefaults = new StaticDefaults();
    }

    @Test
    public void testHierarchical() {
        assertTrue(staticDefaults instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(staticDefaults.parent());
    }

    @Test
    public void testParent_reflective() {
        ServerDefaults serverDefaults = new ServerDefaults();
        staticDefaults.setParent(serverDefaults);
        assertSame(serverDefaults, staticDefaults.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        staticDefaults.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(staticDefaults instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(staticDefaults.supports(staticDefaults.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(staticDefaults, "staticDefaults");
        staticDefaults.validate(staticDefaults, errors);
        assertFalse(errors.hasErrors());
    }

}
