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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.util.Map;

import org.hyperic.hq.product.PluginException;

/**
 * Retrieves the properties from a file.
 * 
 */
public interface XmlPropertiesFileRetriever {

    /**
     * Retrieves the properties of a node from the specified file.
     * 
     * @param filePath The path to the file.
     * @param nodeName The name of the node to retrieve its properties.
     * @param nodeAttributeName The name of an attribute to identify the correct node.
     * @param nodeAttributeValue The value of an attribute to identify the correct node.
     * @return All of the properties of the specified node with the matching attribute name/value pair.
     * @throws PluginException
     */
    Map<String, String> getPropertiesFromFile(String filePath, String nodeName, String nodeAttributeName, String nodeAttributeValue)
        throws PluginException;
}
