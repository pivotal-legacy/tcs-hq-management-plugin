
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
import java.util.Iterator;
import java.util.List;

final class RootAgentControlCommandConverter implements ControlCommandConverter {

    private static final String SU_COMMAND_SWITCH = "-c";

    private static final File CONTROL_PROGRAM = new File("/bin/su");

    private final String instanceUserName;

    public RootAgentControlCommandConverter(String instanceUserName) {
        this.instanceUserName = instanceUserName;
    }

    public ControlCommand convert(ControlCommand controlCommand) {
        List<String> convertedArguments = new ArrayList<String>();
        convertedArguments.add(instanceUserName);
        convertedArguments.add(SU_COMMAND_SWITCH);
        convertedArguments.add(buildCommandString(controlCommand));

        return new ControlCommand(CONTROL_PROGRAM, convertedArguments);
    }

    private String buildCommandString(ControlCommand controlCommand) {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(controlCommand.getControlProgram().getAbsolutePath());

        Iterator<String> argumentIterator = controlCommand.getArguments().iterator();

        while (argumentIterator.hasNext()) {
            commandBuilder.append(" ");
            commandBuilder.append(argumentIterator.next());
        }

        return commandBuilder.toString();
    }
}
