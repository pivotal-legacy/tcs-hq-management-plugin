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

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import org.springframework.validation.Errors;

/**
 * Mock implementation of {@link DataSource} abstract class
 */
public class MockDataSource extends DataSource {

    public boolean supports(Class<?> clazz) {
        return false;
    }

    public void validate(Object target, Errors errors) {
        // no-op
    }

    public void applyParentToChildren() {
        // no-op
    }

}
