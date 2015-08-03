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

package com.springsource.hq.plugin.tcserver.serverconfig.services.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.Hierarchical;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

/**
 * Unit tests for {@link Engine}
 */
public class EngineTests {

    private Engine engine;

    @Before
    public void setup() {
        engine = new Engine();
    }

    @Test
    public void testHierarchical() {
        assertTrue(engine instanceof Hierarchical);
    }

    @Test
    public void testParent_null() {
        assertNull(engine.parent());
    }

    @Test
    public void testParent_reflective() {
        Service service = new Service();
        engine.setParent(service);
        assertSame(service, engine.parent());
    }

    @Test
    public void testApplyParentToChildren() {
        Host host = new Host();
        engine.getHosts().add(host);

        engine.applyParentToChildren();

        assertSame(engine, engine.getLogging().parent());
        for (Host h : engine.getHosts()) {
            assertSame(engine, h.parent());
        }
    }

    @Test
    public void testValidator() {
        assertTrue(engine instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(engine.supports(engine.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(engine, "engine");
        engine.validate(engine, errors);
        assertEquals(2, errors.getFieldErrorCount());
        assertEquals("service.engine.name.required", errors.getFieldError("name").getCode());
        assertEquals("service.engine.defaultHost.required", errors.getFieldError("defaultHost").getCode());
    }

    @Test
    public void validateWithInvalidHost() {
        Set<Host> hosts = new HashSet<Host>();
        hosts.add(new Host());
        engine.setHosts(hosts);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(engine, "engine");
        engine.validate(engine, errors);

        assertEquals("service.engine.host.name.required", errors.getFieldError("hosts[0].name").getCode());
    }
}
