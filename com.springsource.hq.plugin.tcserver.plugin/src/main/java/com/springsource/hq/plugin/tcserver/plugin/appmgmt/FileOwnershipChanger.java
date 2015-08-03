
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

import java.io.File;

import org.hyperic.hq.product.PluginException;

/**
 * A <code>FileOwnershipChanger</code> is used to change the ownership of a file on the file system.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe
 * 
 */
public interface FileOwnershipChanger {

    /**
     * Changes the ownership of the given <code>file</code>.
     * 
     * @param file The file that is to have its ownership changed
     * @param owningUser The user that is to own the file
     * @param owningGroup The group that is to own the file
     * 
     * @throws PluginException if a problem occurs when changing the file's ownership
     */
    void changeFileOwnership(File file, String owningUser, String owningGroup) throws PluginException;
}
