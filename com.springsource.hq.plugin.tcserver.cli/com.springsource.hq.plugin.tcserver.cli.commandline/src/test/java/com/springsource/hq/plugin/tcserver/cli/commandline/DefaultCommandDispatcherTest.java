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

package com.springsource.hq.plugin.tcserver.cli.commandline;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import joptsimple.OptionParser;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link DefaultCommandDispatcher}
 */
public class DefaultCommandDispatcherTest {

    private static String newline = System.getProperty("line.separator");

    private static final String HELP_MESSAGE = "Usage: tcsadmin <command name> [options]" + newline + newline +
            "Available Commands:" + newline +
            "   test" + newline + newline +
            "For specific command information, run 'tcsadmin <command name> --help'" + newline + newline +
            "Common Options:" + newline +
            HelpMessages.HEADER + newline +
            HelpMessages.HELP + newline +
            HelpMessages.HOST + newline +
            HelpMessages.PASSWORD + newline +
            HelpMessages.PORT + newline +
            HelpMessages.SECURE + newline +
            HelpMessages.USER;

    private Command command;

    private CommandDispatcher commandDispatcher;

    private StringWriter errorStringWriter;

    private PrintWriter errorWriter;

    private OptionParser optionParser;

    private StringWriter outputStringWriter;

    private PrintWriter outWriter;

    /**
     * Sets up the tests
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.command = EasyMock.createMock(Command.class);
        final Map<String, Command> commands = new HashMap<String, Command>(1, 1);
        commands.put("test", command);
        this.outputStringWriter = new StringWriter();
        this.errorStringWriter = new StringWriter();
        this.errorWriter = new PrintWriter(errorStringWriter);
        this.optionParser = (OptionParser) new OptionParserFactory().getObject();
        this.outWriter = new PrintWriter(outputStringWriter);
        this.commandDispatcher = new DefaultCommandDispatcher(commands, optionParser, outWriter, errorWriter);
    }

    /**
     * Tears down the tests
     */
    @After
    public void tearDown() {
        this.errorWriter.close();
        this.outWriter.close();
    }

    /**
     * Verifies successful dispatch of a known command
     * 
     * @throws IOException
     */
    @Test
    public void testDispatchCommand() throws IOException {
        EasyMock.expect(command.execute(EasyMock.aryEq(new String[] { "--host=something" }))).andReturn(1);
        EasyMock.replay(command);
        int exitCode = commandDispatcher.dispatch(new String[] { "test", "--host=something" });
        EasyMock.verify(command);
        assertEquals(1, exitCode);
    }

    /**
     * Tests the proper error message is displayed if general IO Exception with no Cause
     * 
     * @throws IOException
     */
    @Test
    public void testDispatchCommandGeneralIOException() throws IOException {
        EasyMock.expect(command.execute(EasyMock.aryEq(new String[0]))).andThrow(new IOException("Something went wrong"));
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "test" });
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command test.  Reason: Something went wrong.", output);
    }

    /**
     * Tests the proper error message is displayed if general IO Exception with no Cause
     * 
     * @throws IOException
     */
    @Test
    public void testDispatchCommandSSLHandshakeException() throws IOException {
        EasyMock.expect(command.execute(EasyMock.aryEq(new String[0]))).andThrow(new SSLHandshakeException("Something went wrong"));
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "test" });
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command test.  The server's SSL certificate is not trusted.  Check the documentation for details.", output);
    }

    /**
     * Verifies that help statement is displayed if executing help command
     */
    @Test
    public void testDispatchCommandHelpCommand() {
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "help" });
        EasyMock.verify(command);
        final String output = outputStringWriter.getBuffer().toString().trim();
        assertEquals(HELP_MESSAGE, output);
    }

    /**
     * Tests the proper error message is displayed if general IO Exception with Cause
     * 
     * @throws IOException
     */
    @Test
    public void testDispatchCommandIOExceptionWithCause() throws IOException {
        IOException e = new IOException();
        e.initCause(new ConnectException("Unable to connect"));
        EasyMock.expect(command.execute(EasyMock.aryEq(new String[0]))).andThrow(e);
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "test" });
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command test.  Cause: Unable to connect.", output);
    }

    /**
     * Verifies that usage statement is displayed if executing dispatcher with no command name
     */
    @Test
    public void testDispatchCommandNoCommand() {
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[0]);
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(HELP_MESSAGE, output);
    }

    /**
     * Verifies that usage statement is displayed if executing a command name not known
     */
    @Test
    public void testDispatchCommandNotFound() {
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "not there" });
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals(HELP_MESSAGE, output);
    }

    /**
     * Tests the proper error message is displayed if an unknown host name is used
     * 
     * @throws IOException
     */
    @Test
    public void testDispatchCommandUnknownHost() throws IOException {
        EasyMock.expect(command.execute(EasyMock.aryEq(new String[0]))).andThrow(new UnknownHostException("fakeHost"));
        EasyMock.replay(command);
        commandDispatcher.dispatch(new String[] { "test" });
        EasyMock.verify(command);
        final String output = errorStringWriter.getBuffer().toString().trim();
        assertEquals("Failed to execute command test. Unknown host: fakeHost", output);
    }
}
