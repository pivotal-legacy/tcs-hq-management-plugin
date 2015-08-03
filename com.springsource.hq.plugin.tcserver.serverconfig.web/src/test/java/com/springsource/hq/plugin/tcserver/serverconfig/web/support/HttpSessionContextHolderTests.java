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

package com.springsource.hq.plugin.tcserver.serverconfig.web.support;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.web.support.HttpServletContextHolder;

/**
 * Unit tests for {@link HttpServletContextHolder}
 */
public class HttpSessionContextHolderTests {

    private HttpServletContextHolder holder;

    @Before
    public void setup() {
        holder = new HttpServletContextHolder();
    }

    @After
    public void teardown() {
        holder.setHttpServletRequest(null);
        holder.setHttpServletResponse(null);
    }

    @Test
    public void testHttpServletRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(holder.getHttpServletRequest());
        holder.setHttpServletRequest(request);
        assertSame(request, holder.getHttpServletRequest());
    }

    @Test
    public void testHttpServletResponse() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertNull(holder.getHttpServletRequest());
        holder.setHttpServletResponse(response);
        assertSame(response, holder.getHttpServletResponse());
    }

}
