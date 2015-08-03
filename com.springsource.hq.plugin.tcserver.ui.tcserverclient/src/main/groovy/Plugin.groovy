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

import org.hyperic.hq.hqu.rendit.HQUPlugin

import ApplicationmanagementController

class Plugin extends HQUPlugin {
    
    void initialize(File pluginDir) {
        super.initialize(pluginDir)
        addAdminView(true, '/basemanagement/index.hqu', 'tc Server Command-line Interface')
    }
}

