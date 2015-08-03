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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WebXmlParser extends AbstractXmlParser {

    @Override
    public Element parse(Document document) throws PluginException {
        final NodeList webApps = document.getElementsByTagName("web-app");
        if (webApps.getLength() > 1) {
            throw new PluginException(
                "Unable to read existing tc Runtime configuration.  An error occurred parsing web.xml. Multiple web-app elements found.");
        }
        if (webApps.getLength() == 0) {
            throw new PluginException("Unable to read existing tc Runtime configuration.  An error occurred parsing web.xml. No web-app element found.");
        }
        final Element webApp = (Element) webApps.item(0);
        return webApp;
    }

    public void writeDocument(Document document, ConfigResponse config) throws TransformerException, IOException {
        writeDocument(document, Metric.decode(config.getValue("installpath")) + "/conf/web.xml");
    }

    public Document createDocument(ConfigResponse config) throws ParserConfigurationException, SAXException, IOException {
        return createDocument(Metric.decode(config.getValue("installpath")) + "/conf/web.xml");
    }

}
