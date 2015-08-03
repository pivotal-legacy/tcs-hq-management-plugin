
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

package com.springsource.hq.plugin.tcserver.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class NonRootAgentControlCommandConverter implements ControlCommandConverter {

    private static final String SUDO_NON_INTERACTIVE_SWITCH = "-n";

    private static final String SUDO_USER_SWITCH = "-u";

    private static final File CONTROL_PROGRAM = new File("/usr/bin/sudo");

    private final String instanceUserName;

    NonRootAgentControlCommandConverter(String instanceUserName) {
        this.instanceUserName = instanceUserName;
    }

    public ControlCommand convert(ControlCommand controlCommand) {
        List<String> convertedArguments = new ArrayList<String>();
        convertedArguments.add(SUDO_NON_INTERACTIVE_SWITCH);
        convertedArguments.add(SUDO_USER_SWITCH);
        convertedArguments.add(instanceUserName);
        convertedArguments.add(controlCommand.getControlProgram().getAbsolutePath());
        convertedArguments.addAll(controlCommand.getArguments());

        return new ControlCommand(CONTROL_PROGRAM, convertedArguments);
    }
}
