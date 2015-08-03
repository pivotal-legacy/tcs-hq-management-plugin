/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.engine;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Engine;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.Logging;

/**
 * Reproduce a bug by parsing an XML fragment that contains a log valve configuration.
 * 
 */
public class AccessLogValveConverterTest {

    @Test
    public void testParsingValveXmlFragment() throws ParserConfigurationException, SAXException, IOException {
        File config = new File("src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/services/engine/access_valve_log.xml");
        Assert.assertTrue(config.canRead());

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(config);

        Element valve = document.getDocumentElement();
        Properties catalinaProperties = new Properties();

        AccessLogValveConverter<Engine> converter = new AccessLogValveConverter<Engine>();
        Logging<Engine> loggingEngine = converter.convert(valve, catalinaProperties);

        Assert.assertEquals("${catalina.home}/logs/", loggingEngine.getDirectory());
        Assert.assertEquals(true, loggingEngine.getEnabled());
        Assert.assertEquals("yyyy-MM-dd.HH", loggingEngine.getFileDateFormat());
        Assert.assertEquals("%t %H cookie:%{SESSIONID}c request:%{SESSIONID}r  %m %U %s %q %r", loggingEngine.getPattern());
        Assert.assertEquals("access_log", loggingEngine.getPrefix());
        Assert.assertEquals(".log", loggingEngine.getSuffix());
    }

}
