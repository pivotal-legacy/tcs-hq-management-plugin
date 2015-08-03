
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

package com.springsource.hq.plugin.tcserver.plugin;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.PluginException;
import org.hyperic.sigar.ProcCred;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Utility code for use in the tc Server product plugin
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe
 * 
 */
public final class Utils {

    public static final String SERVER_RESOURCE_CONFIG_PROCESS_USERNAME = "process.username";

    public static final String SERVER_RESOURCE_CONFIG_PROCESS_GROUP = "process.group";

    private static final String USER_NAME_ROOT = "root";

    private static volatile String agentUser;

    private final static Log LOGGER = LogFactory.getLog(Utils.class);

    private Utils() {

    }

    /**
     * Returns <code>true</code> when running on Windows, otherwise <code>false</code>.
     * 
     * @return <code>true</code> if running on Windows, otherwise <code>false</code>.
     */
    public static boolean isWindows() {
        return File.separatorChar == '\\';
    }

    public static boolean isRoot(String username) {
        return USER_NAME_ROOT.equals(username);
    }

    public static String getAgentUser() throws PluginException {
        String localAgentUser = Utils.agentUser;
        if (localAgentUser == null) {
            Sigar sigar = new Sigar();
            long pid = sigar.getPid();
            try {
                localAgentUser = sigar.getProcCredName(pid).getUser();
            } catch (SigarException se) {
                // This can happen if kerberos is used and sigar is unable to retrieve the non-local username.
                LOGGER.warn("Failed to get the name of the user running the agent process with pid: " + pid);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(se);
                }
                ProcCred procCred;
                try {
                    procCred = sigar.getProcCred(pid);
                    localAgentUser = String.valueOf(procCred.getUid());
                } catch (SigarException e) {
                    LOGGER.warn("Failed to get the id of the user running the agent process with pid: " + pid);
                }
            } finally {
                LOGGER.info("Using " + localAgentUser + " as the user for agent process with pid: " + pid);
                sigar.close();
            }
        }

        Utils.agentUser = localAgentUser;
        return localAgentUser;
    }
}
