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

package com.springsource.hq.plugin.tcserver.util.tomcat;

public final class TomcatNameUtils {
    
    private static final String ROOT_APP_DISPLAYED_NAME = "ROOT";
    
    private static final String ROOT_APP_DISPLAYED_NAME_WITH_SLASH = "/" + ROOT_APP_DISPLAYED_NAME;
    
    private static final String ROOT_APP_PATH_EMPTY_STRING = "";
    
    private static final String ROOT_APP_PATH_FORWARD_SLASH = "/";
    
    private TomcatNameUtils() {
    }
    
    public static String convertPathToName(String path) {
        if (ROOT_APP_PATH_EMPTY_STRING.equals(path) || ROOT_APP_PATH_FORWARD_SLASH.equals(path) 
            || ROOT_APP_DISPLAYED_NAME_WITH_SLASH.equals(path.toUpperCase())) {
            return ROOT_APP_DISPLAYED_NAME;
        } else {
            return path;
        }
    }
    
    public static String convertNameToPath(String name) {
        if (ROOT_APP_DISPLAYED_NAME.equals(name.toUpperCase()) || ROOT_APP_DISPLAYED_NAME_WITH_SLASH.equals(name.toUpperCase())) {
            return ROOT_APP_PATH_EMPTY_STRING;
        } else {
            return name;
        }
    }
}
