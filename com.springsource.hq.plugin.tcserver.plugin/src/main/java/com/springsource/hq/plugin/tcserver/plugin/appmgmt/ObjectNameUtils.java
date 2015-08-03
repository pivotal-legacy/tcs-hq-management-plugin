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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.domain.Application;
import com.springsource.hq.plugin.tcserver.util.application.ApplicationUtils;
import com.springsource.hq.plugin.tcserver.util.tomcat.TomcatNameUtils;

final class ObjectNameUtils {

    private ObjectNameUtils() {

    }

    static String getManagerMBeanObjectNameForApplication(String host, Application application, boolean tomcat7) {
        String applicationIdentifier = TomcatNameUtils.convertNameToPath(application.getName());

        if (applicationIdentifier.equals("")) {
            applicationIdentifier = "/";
        } else if (!applicationIdentifier.startsWith("/")) {
            applicationIdentifier = "/" + applicationIdentifier;
        }

        if (tomcat7 && application.getVersion() > 0) {
            applicationIdentifier = applicationIdentifier + "##" + ApplicationUtils.convertVersionToPaddedString(application.getVersion());
        }

        String pathOrContext;

        if (tomcat7) {
            pathOrContext = "context";
        } else {
            pathOrContext = "path";
        }

        return "Catalina:type=Manager," + pathOrContext + "=" + applicationIdentifier + ",host=" + host;
    }
}
