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
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.serverdefaults.StaticDefaults;

public class StaticDefaultsConverter extends AbstractXmlElementConverter implements XmlElementConverter<StaticDefaults> {

    private static final String STATIC_DEFAULTS = "Static Defaults";

    private static final String SENDFILE_SIZE = "sendfileSize";

    private static final String README_FILE = "readmeFile";

    private static final String READONLY = "readonly";

    private static final String OUTPUT = "output";

    private static final String LISTINGS = "listings";

    private static final String INPUT = "input";

    private static final String FILE_ENCODING = "fileEncoding";

    private static final String DEBUG = "debug";

    public void convert(Document document, Element servlet, StaticDefaults from, Properties catalinaProperties) {
        updateServletInitParam(document, servlet, DEBUG, from.getDebug(), catalinaProperties);
        updateServletInitParam(document, servlet, FILE_ENCODING, from.getFileEncoding(), catalinaProperties);
        updateServletInitParam(document, servlet, INPUT, from.getInput(), catalinaProperties);
        updateServletInitParam(document, servlet, LISTINGS, from.getListings(), catalinaProperties);
        updateServletInitParam(document, servlet, OUTPUT, from.getOutput(), catalinaProperties);
        updateServletInitParam(document, servlet, README_FILE, from.getReadmeFile(), catalinaProperties);
        updateServletInitParam(document, servlet, READONLY, from.getReadonly(), catalinaProperties);
        updateServletInitParam(document, servlet, SENDFILE_SIZE, from.getSendfileSize(), catalinaProperties);
    }

    public StaticDefaults convert(Element servlet, Properties catalinaProperties) {
        StaticDefaults staticDefaults = new StaticDefaults();
        List<Element> initParams = getChildElements(servlet, "init-param");
        for (Element initParam : initParams) {
            final String paramName = getChildElements(initParam, "param-name").get(0).getTextContent();
            updateParameter(staticDefaults, paramName, initParam, catalinaProperties);
        }
        return staticDefaults;
    }

    private void updateParameter(StaticDefaults staticDefaults, String paramName, Element initParam, Properties catalinaProperties) {
        final String paramValue = parseProperties(getChildElements(initParam, "param-value").get(0).getTextContent(), catalinaProperties);
        if (!(EMPTY_STRING.equals(paramValue))) {
            if (DEBUG.equals(paramName)) {
                try {
                    staticDefaults.setDebug(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, DEBUG, paramValue);
                }
            } else if (FILE_ENCODING.equals(paramName)) {
                staticDefaults.setFileEncoding(paramValue);
            } else if (INPUT.equals(paramName)) {
                try {
                    staticDefaults.setInput(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, INPUT, paramValue);
                }
            } else if (LISTINGS.equals(paramName)) {
                staticDefaults.setListings(Boolean.valueOf(paramValue));
            } else if (OUTPUT.equals(paramName)) {
                try {
                    staticDefaults.setOutput(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, OUTPUT, paramValue);
                }
            } else if (READONLY.equals(paramName)) {
                staticDefaults.setReadonly(Boolean.valueOf(paramValue));
            } else if (README_FILE.equals(paramName)) {
                staticDefaults.setReadmeFile(paramValue);
            } else if (SENDFILE_SIZE.equals(paramName)) {
                try {
                    staticDefaults.setSendfileSize(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    logNonNumericValue(STATIC_DEFAULTS, SENDFILE_SIZE, paramValue);
                }
            }
        }
    }
}
