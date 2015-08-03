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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

abstract public class AbstractXmlElementConverter {

    private final Log logger = LogFactory.getLog(AbstractXmlElementConverter.class);

    private PropertySubstituter propertySubstituter;

    public AbstractXmlElementConverter() {
        propertySubstituter = new PropertySubstituter();
    }

    protected void updateServletInitParam(Document document, Element servlet, String attributeName, Object attributeValue,
        Properties catalinaProperties) {
        List<Element> initParams = getChildElements(servlet, "init-param");
        boolean paramFound = false;
        for (int i = 0; i < initParams.size(); i++) {
            Element initParam = (Element) initParams.get(i);
            final String paramName = getChildElements(initParam, "param-name").get(0).getTextContent();
            if (paramName.equals(attributeName)) {
                paramFound = true;
                if (attributeValue != null && !("".equals(attributeValue))) {
                    Element paramValue = getChildElements(initParam, "param-value").get(0);
                    paramValue.setTextContent(determineNewValue(paramValue.getTextContent(), attributeValue, catalinaProperties));
                } else {
                    servlet.removeChild(initParam);
                }
                break;
            }
        }
        if (!(paramFound) && attributeValue != null && !("".equals(attributeValue))) {
            Element newInitParam = document.createElement("init-param");
            Element paramName = document.createElement("param-name");
            Element paramValue = document.createElement("param-value");
            newInitParam.appendChild(paramName);
            newInitParam.appendChild(paramValue);
            paramName.setTextContent(attributeName);
            paramValue.setTextContent(determineNewValue(paramValue.getTextContent(), attributeValue, catalinaProperties));
            // This list contains all the elements that are required by the XSD to occur after init-param node
            List<String> orderedTypes = Arrays.asList("load-on-startup", "enabled", "async-supported", "run-as", "security-role-ref",
                "multipart-config");
            boolean elementInserted = false;
            for (String orderedType : orderedTypes) {
                List<Element> foundElement = getChildElements(servlet, orderedType);
                if (!foundElement.isEmpty()) {
                    servlet.insertBefore(newInitParam, foundElement.get(0));
                    elementInserted = true;
                    break;
                }
            }
            if (!elementInserted) {
                servlet.appendChild(newInitParam);
            }

        }
    }

    protected void logNonNumericValue(final String elementName, final String attributeName, final String attributeValue) {
        logger.warn("Error reading tc Runtime configuration.  " + elementName + " " + attributeName + " set to a non-numeric value: '"
            + attributeValue + "'.  Default value will be displayed instead.");
    }

    protected List<Element> getChildElements(final Element element, final String childName) {
        final List<Element> childElements = new ArrayList<Element>();
        final NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (childName.equals(children.item(i).getNodeName())) {
                childElements.add((Element) children.item(i));
            }
        }
        return childElements;
    }

    protected void setAttribute(Element xmlElement, String attributeName, Object value, Properties catalinaProperties, boolean required) {
        String oldValue = xmlElement.getAttribute(attributeName);
        String valueToWrite = determineNewValue(oldValue, value, catalinaProperties);
        if (required || (valueToWrite != null && !"".equals(valueToWrite))) {
            xmlElement.setAttribute(attributeName, valueToWrite);
        } else {
            xmlElement.removeAttribute(attributeName);
        }
    }

    private String determineNewValue(String exsistingValue, Object newValue, Properties catalinaProperties) {
        if (newValue == null) {
            return null;
        }
        String newValueStr = newValue.toString();
        if (exsistingValue == null) {
            return newValueStr;
        } else if (newValue.equals(exsistingValue)) {
            return newValueStr;
        } else if (newValueStr.equals(parseProperties(exsistingValue, catalinaProperties))) {
            return exsistingValue;
        } else {
            return substituteCatalinaPlaceholders(newValueStr, catalinaProperties);
        }
    }

    // property placeholder parsing

    protected String parseProperties(String strVal, Properties properties) {
        return propertySubstituter.parse(strVal, properties);
    }

    private String substituteCatalinaPlaceholders(String strVal, Properties properties) {
        if (strVal == null) {
            return strVal;
        }
        String catalinaBase = properties.getProperty("catalina.base");
        String catalinaHome = properties.getProperty("catalina.home");
        if (strVal.startsWith(catalinaBase)) {
            strVal = "${catalina.base}" + strVal.substring(catalinaBase.length());
        } else if (strVal.startsWith(catalinaHome)) {
            strVal = "${catalina.home}" + strVal.substring(catalinaHome.length());
        }
        return strVal;
    }

    private static class PropertySubstituter extends PropertyPlaceholderConfigurer {

        public PropertySubstituter() {
            super();
            setIgnoreUnresolvablePlaceholders(true);
            setSearchSystemEnvironment(false);
            setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER);
        }

        public String parse(String strVal, Properties properties) {
            return parseStringValue(strVal, properties, new HashSet<Object>());
        }

    }
}
