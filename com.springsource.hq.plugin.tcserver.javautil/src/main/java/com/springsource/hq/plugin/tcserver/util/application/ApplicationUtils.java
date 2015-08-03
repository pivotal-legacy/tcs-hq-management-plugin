/*
 * Copyright (C) 2011-2015  Pivotal Software, Inc
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

package com.springsource.hq.plugin.tcserver.util.application;

import java.util.Set;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

public final class ApplicationUtils {
    
    private static final String FORMAT_STRING_VERSION = "%06d";

    private ApplicationUtils() {
        
    }
    
    public static String getNewRevisionForApplication(Set<Application> deployedApplications, String deployPath) {
        String deployName = TomcatNameUtils.convertPathToName(deployPath);
        int latestRevision = -1;
        
        for (Application application : deployedApplications) {
            if (deployName.equals(application.getName())) {
                latestRevision = Math.max(latestRevision, application.getVersion());
            }
        }
        
        if (latestRevision > -1) {
            return String.format(FORMAT_STRING_VERSION, latestRevision + 1);
        } else {
            return null;
        }
    }   
    
    public static String convertVersionToPaddedString(int version) {
        if (version == 0) {
            return "";
        } else {
            return String.format(FORMAT_STRING_VERSION, version);
        }
    }
    
    public static String convertVersionToPaddedString(String version) {
        return convertVersionToPaddedString(Integer.parseInt(version));
    }
}
