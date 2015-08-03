
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

package com.springsource.hq.plugin.tcserver.plugin.appmgmt;

import org.hyperic.hq.product.PluginException;

import com.springsource.hq.plugin.tcserver.plugin.Utils;

/**
 * Standard implementations of <code>FileOwnershipChangerFactory</code>
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe
 * 
 */
public final class StandardFileOwnershipChangerFactory implements FileOwnershipChangerFactory {

    private final FileOwnershipChanger fileOwnershipChanger;

    public StandardFileOwnershipChangerFactory() throws PluginException {
        if ((!Utils.isWindows()) && Utils.isRoot(Utils.getAgentUser())) {
            this.fileOwnershipChanger = new ChownFileOwnershipChanger();
        } else {
            this.fileOwnershipChanger = new NoOpFileOwnershipChanger();
        }
    }

    public FileOwnershipChanger getFileOwnershipChanger() {
        return this.fileOwnershipChanger;
    }
}
