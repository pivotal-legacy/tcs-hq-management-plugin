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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Populates and cleans up the objects in the {@link HttpServletContextHolder}
 * by hooking into the Spring MVC lifecyle as a
 * {@link HandlerInterceptorAdapter}.
 * 
 * @since 2.0
 */
@Component("httpServletTheadLocalHandlerInterceptor")
public class HttpServletTheadLocalHandlerInterceptor extends
        HandlerInterceptorAdapter {

    private HttpServletContextHolder context;

    @Autowired
    public HttpServletTheadLocalHandlerInterceptor(
            HttpServletContextHolder context) {
        this.context = context;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        context.setHttpServletRequest(request);
        context.setHttpServletResponse(response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        context.setHttpServletRequest(null);
        context.setHttpServletResponse(null);
    }

}
