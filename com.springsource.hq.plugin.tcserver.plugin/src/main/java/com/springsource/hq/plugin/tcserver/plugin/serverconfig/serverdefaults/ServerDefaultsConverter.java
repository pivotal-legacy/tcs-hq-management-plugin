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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.serverdefaults;

import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.JspDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.ServerDefaults;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;

public class ServerDefaultsConverter extends AbstractXmlElementConverter implements XmlElementConverter<ServerDefaults> {

    private XmlElementConverter<JspDefaults> jspDefaultsConverter = new JspDefaultsConverter();

    private XmlElementConverter<StaticDefaults> staticDefaultsConverter = new StaticDefaultsConverter();

    public ServerDefaults convert(Element webApp, Properties catalinaProperties) {
        final ServerDefaults serverDefaults = new ServerDefaults();
        final List<Element> servlets = getChildElements(webApp, "servlet");
        for (int i = 0; i < servlets.size(); i++) {
            Element servlet = (Element) servlets.get(i);
            final String servletName = getChildElements(servlet, "servlet-name").get(0).getTextContent();
            if ("jsp".equals(servletName)) {
                serverDefaults.setJspDefaults(jspDefaultsConverter.convert(servlet, catalinaProperties));
            } else if ("default".equals(servletName)) {
                serverDefaults.setStaticDefaults(staticDefaultsConverter.convert(servlet, catalinaProperties));
            }
        }
        return serverDefaults;
    }

    public void convert(Document document, Element webApp, ServerDefaults from, Properties catalinaProperties) {
        final List<Element> servlets = getChildElements(webApp, "servlet");
        for (int i = 0; i < servlets.size(); i++) {
            Element servlet = (Element) servlets.get(i);
            final String servletName = getChildElements(servlet, "servlet-name").get(0).getTextContent();
            if ("jsp".equals(servletName)) {
                jspDefaultsConverter.convert(document, servlet, from.getJspDefaults(), catalinaProperties);
            } else if ("default".equals(servletName)) {
                staticDefaultsConverter.convert(document, servlet, from.getStaticDefaults(), catalinaProperties);
            }
        }
    }

}
