
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

import org.hyperic.hq.product.PluginException;

import com.springsource.hq.plugin.tcserver.plugin.Utils;

/**
 * Standard implementations of <code>FilePermissionsChangerFactory</code>
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe
 * 
 */
public final class StandardFilePermissionsChangerFactory implements FilePermissionsChangerFactory {

    private static final String ROOT_AGENT_FILE_PERMISSIONS = "600";

    private static final String NON_ROOT_AGENT_FILE_PERMISSIONS = "640";

    private final FilePermissionsChanger filePermissionsChanger;

    public StandardFilePermissionsChangerFactory() throws PluginException {
        if (!Utils.isWindows()) {
            if (Utils.isRoot(Utils.getAgentUser())) {
                this.filePermissionsChanger = new ChmodFilePermissionsChanger(ROOT_AGENT_FILE_PERMISSIONS);
            } else {
                this.filePermissionsChanger = new ChmodFilePermissionsChanger(NON_ROOT_AGENT_FILE_PERMISSIONS);
            }
        } else {
            this.filePermissionsChanger = new NoOpFilePermissionsChanger();
        }
    }

    public FilePermissionsChanger getFilePermissionsChanger() {
        return this.filePermissionsChanger;
    }
}
