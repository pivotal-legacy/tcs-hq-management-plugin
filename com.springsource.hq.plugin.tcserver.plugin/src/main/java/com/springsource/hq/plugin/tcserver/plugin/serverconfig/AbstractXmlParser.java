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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.hyperic.hq.product.PluginException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractXmlParser extends AbstractDocumentCreator implements XmlParser {

    abstract public Element parse(Document document) throws PluginException;

    protected void writeDocument(final Document document, String fileName) throws IOException {
        final OutputFormat out = new OutputFormat(document);
        out.setIndenting(true);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(fileName));
            final XMLSerializer xmlSer = new XMLSerializer(fos, out);
            xmlSer.serialize(document);

            fos.flush();
            fos.getFD().sync();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}
