/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * This is a specialized token that contains everything a normal authentication
 * token has PLUS the session id. This is needed to support the specialized
 * filter used to authenticate against HQ sessions.
 */
public class HqAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private String sessionId;

    public HqAuthenticationToken(Object principal, Object credentials,
            Collection<GrantedAuthority> authorities, String sessionId) {
        super(principal, credentials, authorities);
        this.sessionId = sessionId;
    }

    public HqAuthenticationToken(Object principal, Object credentials,
            String sessionId) {
        super(principal, credentials);
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return super.toString() + " sessionId: " + getSessionId();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
