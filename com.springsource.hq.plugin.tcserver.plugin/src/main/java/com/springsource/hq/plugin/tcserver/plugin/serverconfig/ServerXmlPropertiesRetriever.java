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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.hyperic.hq.product.PluginException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Retrieves the properties of the first listener node matching the node attribute specified.
 * 
 */
public class ServerXmlPropertiesRetriever extends AbstractDocumentCreator implements XmlPropertiesFileRetriever {

    public Map<String, String> getPropertiesFromFile(String filePath, String nodeName, String nodeAttributeName, String nodeAttributeValue)
        throws PluginException {
        ServerXmlParser serverParser = new ServerXmlParser();
        Element serverElement;
        try {
            serverElement = serverParser.parse(createDocument(filePath));
        } catch (ParserConfigurationException e) {
            throw new PluginException("Parser exception: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new PluginException("Error parsing file: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException("File error: " + e.getMessage(), e);
        }
        return getPropertiesFromServerElementByAttribute(serverElement, nodeName, nodeAttributeName, nodeAttributeValue);
    }

    private Map<String, String> getPropertiesFromServerElementByAttribute(final Element serverElement, final String nodeName,
        final String nodeAttributeName, final String nodeAttributeValue) {
        Map<String, String> listenerProperties = new LinkedHashMap<String, String>();
        final NodeList children = serverElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (nodeName.equals(children.item(i).getNodeName())) {
                Element listenerElement = (Element) children.item(i);
                if (listenerElement.getAttribute(nodeAttributeName).equals(nodeAttributeValue)) {
                    NamedNodeMap nodeMap = listenerElement.getAttributes();
                    for (int j = 0; j < nodeMap.getLength(); j++) {
                        listenerProperties.put(nodeMap.item(j).getNodeName(), nodeMap.item(j).getNodeValue());
                    }
                    break;
                }
            }
        }
        return listenerProperties;
    }
}
