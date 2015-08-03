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

class Plugin extends HQUPlugin {
    Plugin() {
        /**
         * The following can be un-commented to have the plugin's view rendered in HQ.
         *
         * description:  The brief name of the view (e.g.: "Fast Executor")
         * attachType:   one of ['masthead', 'admin']
         * controller:   The controller to invoke when the view is to be generated
         * action:       The method within 'controller' to invoke
         * category:     (optional)  If set, specifies either 'tracker' or 'resource' menu
         */

        addView(description:  'Application Management',
                attachType:   'resource',
                controller:   TomcatappmgmtController,
                action:       'manageApplications',
                resourceType: ['SpringSource tc Runtime 6.0','SpringSource tc Runtime 7.0', 'Pivotal tc Runtime 8.0'])

		
    }
}

