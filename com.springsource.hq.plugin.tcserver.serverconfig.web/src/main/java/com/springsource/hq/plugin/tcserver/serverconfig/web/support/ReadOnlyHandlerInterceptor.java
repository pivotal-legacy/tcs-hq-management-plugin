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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.springsource.hq.plugin.tcserver.serverconfig.web.services.SettingsService;

@Component("readOnlyHandlerInterceptor")
public class ReadOnlyHandlerInterceptor extends HandlerInterceptorAdapter {

    private SettingsService settingsService;

    private static final Log logger = LogFactory
            .getLog(ReadOnlyHandlerInterceptor.class);

    @Autowired
    public ReadOnlyHandlerInterceptor(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle
     * (javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object,
     * org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        try {
            if (modelAndView.isReference()
                    && !modelAndView.getViewName().startsWith("redirect:")) {
                String eid = parseeid(request);
                if (eid != null) {
                    Map<String, Object> model = modelAndView.getModel();
                    model.put("readOnly", settingsService.isReadOnly(eid));
                }
            }
        }
        catch (Exception e) {
            logger.info("Unable to populate readonly attribute into model", e);
        }
    }

    private String parseeid(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            return null;
        }
        int eidStart = pathInfo.indexOf("/");
        int eidEnd = pathInfo.indexOf("/", eidStart + 1);
        if (eidEnd == -1) {
            return null;
        }
        return pathInfo.substring(eidStart + 1, eidEnd);
    }
}
