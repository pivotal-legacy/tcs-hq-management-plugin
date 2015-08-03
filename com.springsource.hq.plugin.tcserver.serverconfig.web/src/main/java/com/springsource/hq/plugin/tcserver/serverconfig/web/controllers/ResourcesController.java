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

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;

import com.springsource.hq.plugin.tcserver.serverconfig.web.support.LongPropertyEditor;

/**
 * Spring MVC controller for tc Runtime resources
 * @since 2.0
 */
@Controller
public class ResourcesController {

    /**
     * Root request for tc Runtime resources. At the moment, only JDBC resources are
     * supported, so redirect the user to that handler.
     * 
     * @param eid the tc Runtime instance id
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{eid}/resources/", method = RequestMethod.GET)
    public String index(@PathVariable("eid") String eid)
            throws UnsupportedEncodingException {
        return "redirect:/app/" + UriUtils.encodePathSegment(eid, "UTF-8")
                + "/resources/jdbc/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new LongPropertyEditor());
    }

}
