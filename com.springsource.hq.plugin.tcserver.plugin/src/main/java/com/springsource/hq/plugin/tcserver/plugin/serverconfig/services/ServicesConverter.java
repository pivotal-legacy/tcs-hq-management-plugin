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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.Service;

public class ServicesConverter extends AbstractXmlElementConverter implements XmlElementConverter<Set<Service>> {

    private XmlElementConverter<Service> serviceConverter = new ServiceConverter();

    public void convert(Document document, Element server, Set<Service> from, Properties catalinaProperties) {
        final List<Element> serviceNodes = getChildElements(server, "Service");
        // remove services that were removed via GUI
        final Set<String> serviceIds = new HashSet<String>(from.size());
        for (Service service : from) {
            serviceIds.add(service.getId());
        }
        final Map<String, Element> serviceElements = new HashMap<String, Element>();
        for (int i = 0; i < serviceNodes.size(); i++) {
            final String elementName = parseProperties(((Element) serviceNodes.get(i)).getAttribute("name"), catalinaProperties);
            if (serviceIds.contains(elementName)) {
                serviceElements.put(elementName, (Element) serviceNodes.get(i));
            } else {
                server.removeChild(serviceNodes.get(i));
            }
        }
        // add new ones or update existing ones
        for (Service service : from) {
            if (serviceElements.get(service.getId()) == null) {
                final Element serviceElement = document.createElement("Service");
                serviceConverter.convert(document, serviceElement, service, catalinaProperties);
                server.appendChild(serviceElement);
            } else {
                serviceConverter.convert(document, serviceElements.get(service.getId()), service, catalinaProperties);
            }
        }
    }

    public Set<Service> convert(Element server, Properties catalinaProperties) {
        final Set<Service> services = new HashSet<Service>();
        final List<Element> serviceNodes = getChildElements(server, "Service");
        for (int i = 0; i < serviceNodes.size(); i++) {
            final Service service = serviceConverter.convert((Element) serviceNodes.get(i), catalinaProperties);
            services.add(service);
        }
        return services;
    }

}
