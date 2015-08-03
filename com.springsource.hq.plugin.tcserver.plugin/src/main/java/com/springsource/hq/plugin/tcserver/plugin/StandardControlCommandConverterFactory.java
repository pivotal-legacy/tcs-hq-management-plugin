
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

import org.hyperic.hq.product.PluginException;

final class StandardControlCommandConverterFactory implements ControlCommandConverterFactory {

    private final String agentUser;

    public StandardControlCommandConverterFactory() throws PluginException {
        this.agentUser = Utils.getAgentUser();
    }

    public ControlCommandConverter getControlCommandConverter(String instanceUserName) throws PluginException {
        // if running on windows, or the instance user is not found, or if the agent user was not accessible, run
        // as the same user.
        if (Utils.isWindows() || instanceUserName == null || this.agentUser == null) {
            return new IdentityControlCommandConverter();
        } else if (Utils.isRoot(this.agentUser)) {
            if (Utils.isRoot(instanceUserName)) {
                return new IdentityControlCommandConverter();
            } else {
                return new RootAgentControlCommandConverter(instanceUserName);
            }
        } else {
            if (this.agentUser.equals(instanceUserName)) {
                return new IdentityControlCommandConverter();
            } else {
                return new NonRootAgentControlCommandConverter(instanceUserName);
            }
        }
    }
}
