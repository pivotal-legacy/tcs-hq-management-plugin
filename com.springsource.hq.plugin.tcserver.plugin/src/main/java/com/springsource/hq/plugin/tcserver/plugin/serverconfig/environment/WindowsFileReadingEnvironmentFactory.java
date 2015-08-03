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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

/**
 * Create an Environment object based on the Windows wrapper.conf file.
 * 
 */
public class WindowsFileReadingEnvironmentFactory implements EnvironmentFactory {

    private JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    private WindowsSpecificNonJvmOptsUtil windowsOptsUtil = new WindowsSpecificNonJvmOptsUtil();

    public Environment create(ConfigResponse config) throws PluginException {
        try {
            Environment environment = new Environment();
            Resource wrapperConf = new FileSystemResource(Metric.decode(config.getValue("installpath")) + "/conf/wrapper.conf");
            if (wrapperConf.exists()) {
                environment.setJvmOptions(createJvmOptions(wrapperConf));
                environment.setJavaHome(createJavaHome(wrapperConf));
                return environment;
            }
            return environment;
        } catch (Exception e) {
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e.getMessage());
        }
    }

    private JvmOptions createJvmOptions(final Resource wrapperConf) throws IOException {
        BufferedReader envFileReader = new BufferedReader(new InputStreamReader(wrapperConf.getInputStream()));
        try {
            List<String> jvmOpts = new LinkedList<String>();
            String line = envFileReader.readLine();
            for (; line != null; line = envFileReader.readLine()) {
                if (line.trim().startsWith("wrapper.java.additional")) {
                    jvmOpts.add(windowsOptsUtil.stripQuotes(line.trim().substring(line.indexOf("=") + 1)));
                }
            }
            jvmOpts = windowsOptsUtil.removeProtectedOpts(jvmOpts);
            return jvmOptionsConverter.convert(jvmOpts);
        } finally {
            envFileReader.close();
        }
    }

    private String createJavaHome(final Resource wrapperConf) throws IOException {
        BufferedReader envFileReader = new BufferedReader(new InputStreamReader(wrapperConf.getInputStream()));
        try {
            String line = envFileReader.readLine();
            for (; line != null; line = envFileReader.readLine()) {
                if (line.trim().startsWith("set.JAVA_HOME") && line.indexOf("=") != -1) {
                    return windowsOptsUtil.stripQuotes(line.substring(line.indexOf("=") + 1));
                }
            }
            return null;
        } finally {
            envFileReader.close();
        }
    }

}
