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

package com.springsource.hq.plugin.tcserver.serverconfig.services.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.Connector;

/**
 * Unit tests for {@link AjpConnector}
 */
public class AjpConnectorTests {

    private AjpConnector ajpConnector;

    @Before
    public void setup() {
        ajpConnector = new AjpConnector();
    }

    @Test
    public void testConnector() {
        assertTrue(ajpConnector instanceof Connector);
    }

    @Test
    public void testValidator() {
        assertTrue(ajpConnector instanceof Validator);
    }

    @Test
    public void testSupports() {
        assertTrue(ajpConnector.supports(ajpConnector.getClass()));
    }

    @Test
    public void testValidate_noContext() {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ajpConnector, "ajpConnector");
        ajpConnector.validate(ajpConnector, errors);
        assertEquals(1, errors.getFieldErrorCount());
        assertEquals("service.connector.port.required", errors.getFieldError("port").getCode());
    }

}
