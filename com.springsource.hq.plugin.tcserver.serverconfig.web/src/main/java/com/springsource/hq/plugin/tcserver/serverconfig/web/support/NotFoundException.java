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

package com.springsource.hq.plugin.tcserver.serverconfig.web.support;

/**
 * Exception thrown when the requested resource is not found. This exception
 * should be caught by an exception handler and converted into an HTTP 404
 * response.
 * 
 * <code>
 * 	&lt;bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver"&gt;
 * 		&lt;property name="defaultStatusCode" value="404" /&gt;
 * 		&lt;property name="exceptionMappings"&gt;
 * 			&lt;value&gt;
 * 				NotFoundException=404
 * 			&lt;/value&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 * </code>
 * 
 * @since 2.0
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 244314070372918646L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

}
