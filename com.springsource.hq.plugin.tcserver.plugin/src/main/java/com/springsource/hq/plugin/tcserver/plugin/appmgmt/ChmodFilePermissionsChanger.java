
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

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.exec.Execute;
import org.hyperic.util.exec.PumpStreamHandler;

final class ChmodFilePermissionsChanger implements FilePermissionsChanger {

    private final Log log = LogFactory.getLog(ChmodFilePermissionsChanger.class);

    private static final String EXECUTABLE_CHMOD = "chmod";

    private final String permissions;

    public ChmodFilePermissionsChanger(String permissions) {
        this.permissions = permissions;
    }

    public void changeFilePermissions(File file) throws PluginException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Execute ex = new Execute(new PumpStreamHandler(output));

        ex.setCommandline(new String[] { EXECUTABLE_CHMOD, permissions, file.getAbsolutePath() });

        this.log.info("Changing file permissions with command '" + ex.getCommandLineString() + "'");

        int returnCode;
        try {
            returnCode = ex.execute();
        } catch (Exception e) {
            throw new PluginException("Failed to set permissions on war file", e);
        }
        if (returnCode != 0) {
            throw new PluginException("Failed to set permissions on war file: " + output.toString());
        }
    }

}
