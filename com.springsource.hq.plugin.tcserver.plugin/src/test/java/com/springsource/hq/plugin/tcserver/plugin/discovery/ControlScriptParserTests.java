
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

package com.springsource.hq.plugin.tcserver.plugin.discovery;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.hyperic.hq.product.PluginException;
import org.junit.Test;

public class ControlScriptParserTests {

    private final ControlScriptParser controlScriptParser = new ControlScriptParser();

    private final File expectedInstallBase = new File("src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/");

    @Test
    public void tcServer21to26BashScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.1-to-2.6.sh"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer21to26BatScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.1-to-2.6.bat"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer20BashScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.0.sh"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer20BatScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.0.bat"));

        assertEquals(this.expectedInstallBase, installBase);
    }
}
