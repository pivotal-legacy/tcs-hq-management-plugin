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

package com.springsource.hq.plugin.tcserver.plugin.discovery;

import java.io.File;

public final class TcRuntime80Detector extends TcRuntimeDetector {

    @Override
    protected boolean isTcRuntimeInstance(String catalinaHome, String catalinaBase) {
        return super.isTcRuntimeInstance(catalinaHome, catalinaBase) && (isMyTomcatVersion(catalinaHome, catalinaBase));
    }

    protected boolean isMyTomcatVersion(String catalinaHome, String catalinaBase) {
        boolean tomcat8 = new File(catalinaHome, TOMCAT_8_SPECIFIC_JAR).exists();
        return tomcat8;
    }
}
