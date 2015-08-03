
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

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods when handling requests
 * 
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe.
 * 
 */
public final class RequestUtils {

    private static final Log LOGGER = LogFactory.getLog(RequestUtils.class);

    private RequestUtils() {

    }

    /**
     * Returns the url to access HQ locally, i.e. without routing through any proxy or load balancer which may be in
     * front of HQ.
     * 
     * @param request A request received by the HQ server from which the HQ URL will be determined
     * @return the local URL for the HQ server hosting the web app
     */
    public static String getLocalHqUrl(ServletRequest request) {
        StringBuilder serverUrl = new StringBuilder();

        serverUrl.append(request.getScheme());
        serverUrl.append("://");
        String hostName = request.getLocalName();
        if (hostName.contains(":")) {
            hostName = "[" + hostName + "]";
        }
        serverUrl.append(hostName);
        serverUrl.append(":");
        serverUrl.append(request.getLocalPort());
        if (request.isSecure()) {
            LOGGER.debug("Registering protocol.");
            UntrustedSSLProtocolSocketFactory.register();
        }
        return serverUrl.toString();
    }

}
