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

package com.springsource.hq.plugin.tcserver.serverconfig.configuration.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.GeneralConfig;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.general.ServerProperties;

/**
 * Unit tests for {@link ServerProperties}
 */
public class ServerPropertiesTests {

    private ServerProperties serverProperties;

    @Before
    public void setup() {
        serverProperties = new ServerProperties();
    }

    @Test
    public void testHierarchical() {
        assertTrue(serverProperties instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(serverProperties.parent());
    }

    @Test
    public void testParent_reflective() {
        GeneralConfig generalConfig = new GeneralConfig();
        serverProperties.setParent(generalConfig);
        assertSame(generalConfig, serverProperties.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        serverProperties.applyParentToChildren();
    }

    @Test
    public void testValidator() {
        assertTrue(serverProperties instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(serverProperties.supports(serverProperties.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(serverProperties, "serverProperties");
        serverProperties.validate(serverProperties, errors);
        assertFalse(errors.hasErrors());
    }

}
