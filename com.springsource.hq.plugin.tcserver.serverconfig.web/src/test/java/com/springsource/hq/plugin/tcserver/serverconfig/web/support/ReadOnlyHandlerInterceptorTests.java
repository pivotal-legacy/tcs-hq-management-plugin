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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.JstlView;

import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;

/**
 * Unit tests for {@link SettingsHandlerInterceptor}.
 */
public class ReadOnlyHandlerInterceptorTests {

    private ReadOnlyHandlerInterceptor interceptor;

    private SettingsService settingsService;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ModelAndView modelAndView;

    @Before
    public void setup() {
        settingsService = createMock(SettingsService.class);
        interceptor = new ReadOnlyHandlerInterceptor(settingsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        modelAndView = new ModelAndView();
    }

    @Test
    public void postHandle() throws Exception {
        request.setPathInfo("/eid/foo/bar/");
        modelAndView.setViewName("myView");

        expect(settingsService.isReadOnly("eid")).andReturn(true);

        assertTrue(modelAndView.isReference());
        replay(settingsService);
        interceptor.postHandle(request, response, null, modelAndView);
        verify(settingsService);

        assertTrue((Boolean) modelAndView.getModel().get("readOnly"));
    }

    @Test
    public void postHandle_nullUrl() throws Exception {
        request.setPathInfo(null);
        modelAndView.setViewName("myView");

        assertTrue(modelAndView.isReference());
        interceptor.postHandle(request, response, null, modelAndView);

        assertNull(modelAndView.getModel().get("readOnly"));
    }

    @Test
    public void postHandle_badUrl() throws Exception {
        request.setPathInfo("/eid");
        modelAndView.setViewName("myView");

        assertTrue(modelAndView.isReference());
        interceptor.postHandle(request, response, null, modelAndView);

        assertNull(modelAndView.getModel().get("readOnly"));
    }

    @Test
    public void postHandle_concreteView() throws Exception {
        request.setPathInfo("/eid/");
        modelAndView.setView(new JstlView());

        assertFalse(modelAndView.isReference());
        interceptor.postHandle(request, response, null, modelAndView);

        assertNull(modelAndView.getModel().get("readOnly"));
    }

    @Test
    public void postHandle_redirect() throws Exception {
        request.setPathInfo("/eid/");
        modelAndView.setViewName("redirect:https://www.springsource.com/");

        assertTrue(modelAndView.isReference());
        interceptor.postHandle(request, response, null, modelAndView);

        assertNull(modelAndView.getModel().get("readOnly"));

    }

}
