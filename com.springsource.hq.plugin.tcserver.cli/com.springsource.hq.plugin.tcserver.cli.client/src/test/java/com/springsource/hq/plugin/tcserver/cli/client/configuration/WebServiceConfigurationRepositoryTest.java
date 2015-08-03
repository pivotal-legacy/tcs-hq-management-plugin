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

package com.springsource.hq.plugin.tcserver.cli.client.configuration;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.hyperic.hq.hqapi1.Connection;
import org.hyperic.hq.hqapi1.ResponseHandler;
import org.junit.After;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.cli.client.ParamsEquals;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ConfigurationStatusResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Group;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptions;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsGroupRequest;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsRequest;
import com.springsource.hq.plugin.tcserver.cli.client.schema.JvmOptionsResponse;
import com.springsource.hq.plugin.tcserver.cli.client.schema.Resource;
import com.springsource.hq.plugin.tcserver.cli.client.schema.ResponseStatus;

/**
 * Unit test of the {@link WebServiceConfigurationRepository}
 */
public class WebServiceConfigurationRepositoryTest {

    private final Connection connection = EasyMock.createMock(Connection.class);

    @SuppressWarnings("unchecked")
    private final ResponseHandler<ConfigurationStatusResponse> configurationResponseHandler = createMock(ResponseHandler.class);

    @SuppressWarnings("unchecked")
    private final ResponseHandler<JvmOptionsResponse> jvmOptionsResponseHandler = createMock(ResponseHandler.class);

    private final ConfigurationRepository configurationRepository = new WebServiceConfigurationRepository(connection, configurationResponseHandler,
        jvmOptionsResponseHandler);

    private final File targetFile = new File(System.getProperty("user.dir") + "/configRepositoryTest.txt");

    private Map<String, String[]> eqParams(Map<String, String[]> in) {
        EasyMock.reportMatcher(new ParamsEquals(in));
        return null;
    }

    /**
     * Tears down the tests
     */
    @After
    public void tearDown() {
        targetFile.delete();
    }

    /**
     * Verifies successful retrieval of file and writing of contents to an existing local file
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetFileExistingTarget() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final ConfigurationStatusResponse expected = new ConfigurationStatusResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("file", new String[] { "conf/server.xml" });
        targetFile.createNewFile();
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/serverconfig/getFile.hqu"), eqParams(expectedParams), EasyMock.eq(targetFile),
                EasyMock.isA(FileResponseHandler.class))).andReturn(expected);
        EasyMock.replay(connection);
        ConfigurationStatusResponse response = configurationRepository.getFile(server, "conf/server.xml", targetFile);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies an Exception is thrown when an invalid target file path is specified
     * 
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void testGetFileInvalidTargetPath() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        File fakeTargetFile = new File("/something/fake.txt");
        configurationRepository.getFile(server, "conf/server.xml", fakeTargetFile);
    }

    /**
     * Verifies successful retrieval of file and writing of contents to a new local file
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetFileNewTarget() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final ConfigurationStatusResponse expected = new ConfigurationStatusResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        expectedParams.put("file", new String[] { "conf/server.xml" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/serverconfig/getFile.hqu"), eqParams(expectedParams), EasyMock.eq(targetFile),
                EasyMock.isA(FileResponseHandler.class))).andReturn(expected);
        EasyMock.replay(connection);
        ConfigurationStatusResponse response = configurationRepository.getFile(server, "conf/server.xml", targetFile);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful retrieval of JVM Options
     * 
     * @throws IOException
     */
    @Test
    public void testGetJvmOptions() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final JvmOptionsResponse expected = new JvmOptionsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions options = new JvmOptions();
        options.getOption().add("-server");
        expected.setJvmOptions(options);
        final Map<String, String[]> expectedParams = new HashMap<String, String[]>();
        expectedParams.put("serverid", new String[] { "1234" });
        expectedParams.put("servername", new String[] { "server1" });
        EasyMock.expect(
            connection.doGet(EasyMock.eq("/hqu/tcserverclient/serverconfig/getJvmOptions.hqu"), eqParams(expectedParams),
                EasyMock.eq(this.jvmOptionsResponseHandler))).andReturn(expected);
        EasyMock.replay(connection);
        JvmOptionsResponse jvmOptions = configurationRepository.getJvmOptions(server);
        EasyMock.verify(connection);
        assertEquals(expected, jvmOptions);
    }

    /**
     * Verifies successful writing of file to a group
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileGroup() throws IOException {
        Group group = new Group();
        group.setName("Group1");
        targetFile.createNewFile();
        final ConfigurationStatusResponse expected = new ConfigurationStatusResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("groupname", "Group1");
        expectedParams.put("targetfile", "conf/server.xml");
        expectedParams.put("nobackupfile", "false");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/serverconfig/putFile.hqu", expectedParams, targetFile, this.configurationResponseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        final ConfigurationStatusResponse response = configurationRepository.putFile(group, targetFile, "conf/server.xml", false);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies an Exception is thrown when attempting to write a file that doesn't exist locally
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testPutFileGroupInvalidFile() throws IOException {
        Group group = new Group();
        group.setId(1234);
        group.setName("Group1");
        configurationRepository.putFile(group, targetFile, "conf/server.xml", false);
    }

    /**
     * Verifies successful writing of file to a server
     * 
     * @throws IOException
     */
    @Test
    public void testPutFileServer() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        targetFile.createNewFile();
        final ConfigurationStatusResponse expected = new ConfigurationStatusResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put("serverid", "1234");
        expectedParams.put("servername", "server1");
        expectedParams.put("targetfile", "conf/server.xml");
        expectedParams.put("nobackupfile", "false");
        EasyMock.expect(
            connection.doPost("/hqu/tcserverclient/serverconfig/putFile.hqu", expectedParams, targetFile, this.configurationResponseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        final ConfigurationStatusResponse response = configurationRepository.putFile(server, targetFile, "conf/server.xml", false);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies an Exception is thrown when attempting to write a file that doesn't exist locally
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testPutFileServerInvalidFile() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        configurationRepository.putFile(server, targetFile, "conf/server.xml", false);
    }

    /**
     * Verifies successful set of JVM Options to a group of servers
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsGroup() throws IOException {
        Group group = new Group();
        group.setId(1234);
        group.setName("Group1");
        final JvmOptionsResponse expected = new JvmOptionsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions options = new JvmOptions();
        options.getOption().add("-server");
        options.getOption().add("-Xmx512m");
        final JvmOptionsGroupRequest request = new JvmOptionsGroupRequest();
        request.setGroup(group);
        request.setJvmOptions(options);
        EasyMock.expect(connection.doPost("/hqu/tcserverclient/serverconfig/putJvmOptions.hqu", request, this.jvmOptionsResponseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        final JvmOptionsResponse response = configurationRepository.setJvmOptions(group, options);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

    /**
     * Verifies successful set of JVM Options to a server
     * 
     * @throws IOException
     */
    @Test
    public void testSetJvmOptionsServer() throws IOException {
        Resource server = new Resource();
        server.setId(1234);
        server.setName("server1");
        final JvmOptionsResponse expected = new JvmOptionsResponse();
        expected.setStatus(ResponseStatus.SUCCESS);
        final JvmOptions options = new JvmOptions();
        options.getOption().add("-server");
        options.getOption().add("-Xmx512m");
        final JvmOptionsRequest request = new JvmOptionsRequest();
        request.setResource(server);
        request.setJvmOptions(options);
        EasyMock.expect(connection.doPost("/hqu/tcserverclient/serverconfig/putJvmOptions.hqu", request, this.jvmOptionsResponseHandler)).andReturn(
            expected);
        EasyMock.replay(connection);
        final JvmOptionsResponse response = configurationRepository.setJvmOptions(server, options);
        EasyMock.verify(connection);
        assertEquals(expected, response);
    }

}
