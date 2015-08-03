
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

package com.springsource.hq.plugin.tcserver.plugin.discovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyperic.hq.product.PluginException;

final class ControlScriptParser {

    private static final Pattern PATTERN_INSTALL_BASE = Pattern.compile("(?:set )*INSTALL_BASE=(.+)");

    private static final Object INSTALL_BASE_PLACEHOLDER = "placeholder";

    File getInstallBase(String catalinaBase, File controlScript) throws PluginException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(controlScript));

            String line = null;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = PATTERN_INSTALL_BASE.matcher(line);
                if (matcher.matches()) {
                    String installBase = matcher.group(1);

                    File installationRoot = new File(installBase.replaceAll("\"", ""));
                    if (installationRoot.isDirectory()) {
                        return installationRoot;
                    } else if (INSTALL_BASE_PLACEHOLDER.equals(installBase)) {
                        return controlScript.getParentFile().getParentFile().getParentFile();
                    }
                }
            }
            throw new PluginException("Did not find INSTALL_BASE in control script '" + controlScript + "'");
        } catch (IOException ioe) {
            throw new PluginException("Failed to parse control script '" + controlScript
                + "' when attempting to determine installation root of combined instance '" + catalinaBase + "'", ioe);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    // Do nothing
                }
            }
        }
    }
}
