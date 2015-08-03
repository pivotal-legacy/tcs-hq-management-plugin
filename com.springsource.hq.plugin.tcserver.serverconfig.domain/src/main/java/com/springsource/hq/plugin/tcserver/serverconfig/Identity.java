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

/**
 * Provides human and machine readable identifiers. The human identifier is calculated while the machine identifier must
 * be explicitly set.
 * 
 * @since 2.0
 */
public interface Identity {

    /**
     * @return the machine identifier passed to {@link Identity#setId(String)}, returning null if
     *         {@link Identity#setId(String)} is never invoked for this instance.
     */
    public String getId();

    /**
     * @param id the machine identifier
     */
    public void setId(String id);

    /**
     * Calculate a URI template variable safe, human readable identifier for this instance.
     * 
     * <p>
     * <strong>Note:</strong> URI template variable will be URL encoded, however, many web servers will not respond to
     * requests that contain a URL encoded forward slash '%2F'. Forward slashes should not be present in the identifier,
     * unless it is certain that the web server will accept them.
     * 
     * @return a human readable identifier
     */
    public String getHumanId();

}
