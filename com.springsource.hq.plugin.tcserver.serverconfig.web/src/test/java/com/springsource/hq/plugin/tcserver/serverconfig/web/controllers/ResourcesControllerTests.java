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

package com.springsource.hq.plugin.tcserver.serverconfig.web.controllers;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@ResourceController}.
 */
public class ResourcesControllerTests {

    private ResourcesController controller;

    @Before
    public void setup() {
        controller = new ResourcesController();
    }

    @Test
    public void testIndex() throws UnsupportedEncodingException {
        assertEquals("redirect:/app/eid/resources/jdbc/", controller
                .index("eid"));
    }

    @Test
    public void testIndexWithSpaces() throws UnsupportedEncodingException {
        assertEquals("redirect:/app/eid%20with%20spaces/resources/jdbc/",
                controller.index("eid with spaces"));
    }

    @Test
    public void testIndexWithPercents() throws UnsupportedEncodingException {
        assertEquals("redirect:/app/eid%25with%25percents/resources/jdbc/",
                controller.index("eid%with%percents"));
    }

}
