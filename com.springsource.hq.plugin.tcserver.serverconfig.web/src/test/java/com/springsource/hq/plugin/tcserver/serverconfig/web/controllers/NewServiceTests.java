/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class NewServiceTests {

    @Test
    public void bindableAttributesContainsNewEnginesAttributes() {
        String[] bindableAttributes = BinderUtil
                .getBindableAttributes(NewService.class);
        List<String> bindableAttributesList = Arrays.asList(bindableAttributes);

        assertTrue(bindableAttributesList.contains("engine.newHost"));
        assertTrue(bindableAttributesList.contains("engine.newHost.appBase"));
        assertTrue(bindableAttributesList.contains("engine.newHost.autoDeploy"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.deployOnStartup"));
        assertTrue(bindableAttributesList.contains("engine.newHost.deployXML"));
        assertTrue(bindableAttributesList.contains("engine.newHost.humanId"));
        assertTrue(bindableAttributesList.contains("engine.newHost.id"));
        assertTrue(bindableAttributesList.contains("engine.newHost.logging"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.directory"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.enabled"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.fileDateFormat"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.pattern"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.prefix"));
        assertTrue(bindableAttributesList
                .contains("engine.newHost.logging.suffix"));
        assertTrue(bindableAttributesList.contains("engine.newHost.name"));
        assertTrue(bindableAttributesList.contains("engine.newHost.unpackWARs"));
        assertTrue(bindableAttributesList.contains("engine.newHost.workDir"));
    }
}
