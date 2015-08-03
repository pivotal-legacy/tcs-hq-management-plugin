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

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.DbcpDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc.TomcatDataSource;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.AjpConnector;
import com.springsource.hq.plugin.tcserver.serverconfig.services.connector.HttpConnector;

/**
 * Profile marshaller used to convert Profile <-> xml. Faux implementation of org.springframework.oxm.Marshaller and
 * org.springframework.oxm.Unmarshaller. TODO implement these interfaces once they are available in Spring 3.
 * 
 * @since 2.0
 */
@Component
public class ProfileMarshaller {

    private JAXBContext jaxbContext;

    private Schema profileSchema;

    public ProfileMarshaller() throws JAXBException, SAXException, IOException {
        jaxbContext = JAXBContext.newInstance(Profile.class, TomcatDataSource.class, DbcpDataSource.class, AjpConnector.class, HttpConnector.class);
        Resource schemaResource = new ClassPathResource("tomcatserverconfig-profile-2.0.xsd", Profile.class);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        profileSchema = schemaFactory.newSchema(schemaResource.getURL());
    }

    public void marshal(Profile graph, Result result) throws JAXBException, IOException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(profileSchema);
        marshaller.marshal(graph, result);
    }

    public Profile unmarshal(Source source) throws JAXBException, IOException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(profileSchema);
        return (Profile) unmarshaller.unmarshal(source);
    }

    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        jaxbContext.generateSchema(outputResolver);
    }

    public boolean supports(Class<?> clazz) {
        return Profile.class.equals(clazz);
    }

}
