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

import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

/**
 * Holds the {@link HttpServletRequest} and {@link HttpServletResponse} objects in a thread local.
 * 
 * @since 2.0
 */
@Component
public class HttpServletContextHolder {

    private static NamedThreadLocal<HttpServletRequest> requestThreadLocal = new NamedThreadLocal<HttpServletRequest>(
        HttpServletContextHolder.class.getSimpleName() + ".javax.servlet.http.HttpServletRequest");

    private static NamedThreadLocal<HttpServletResponse> responseThreadLocal = new NamedThreadLocal<HttpServletResponse>(
        HttpServletContextHolder.class.getName() + ".javax.servlet.http.HttpServletResponse");

    public HttpServletRequest getHttpServletRequest() {
        return requestThreadLocal.get();
    }

    public void setHttpServletRequest(HttpServletRequest request) {
        requestThreadLocal.set(request);
    }

    public HttpServletResponse getHttpServletResponse() {
        return responseThreadLocal.get();
    }

    public void setHttpServletResponse(HttpServletResponse response) {
        responseThreadLocal.set(response);
    }

}
