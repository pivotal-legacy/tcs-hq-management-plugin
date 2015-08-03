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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * This filter is must be placed after the anonymous authentication filter. It is meant to look for existing login
 * credentials as well as a sessionId. If they both exist, then the sessionId is tested against HQ to see if it is still
 * valid. If so, the authentication object will be replaced by a specialized one containing one extra role as well as a
 * copy of the sessionId. This way, sessionId is used to grant a special role, and when the sessionId expires or is
 * invalidated due to logout, access to this web app will be revoked as well.
 * 
 * NOTE: It's not possible to listen for the logout event from HQ, because it is a separate web app, and events are not
 * broadcast. Thus, every request into the plugin requires a check with HQ.
 * 
 */
public class HqAuthenticationFilter extends GenericFilterBean {

    private static final Log logger = LogFactory.getLog(HqAuthenticationFilter.class);

    private String defaultRole = "ROLE_SESSION";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        /**
         * If authentication isn't empty, look for the chance that the user was authenticated by
         * AnonymousAuthenticationProvider. If this is the case, there will be security credentials, but no session role
         * granted yet. The filter pulls sessionId from the session, and checks against HQ. If successful, it replaces
         * the existing token with an HQ-specific one.
         * 
         * If there is already an HqAuthenticationToken then grab the token's sessionId, and re-verify that the session
         * is active. If the sessionId is null (which only happens when manually entering URLs), send user down the
         * filter stack.
         * 
         * If there is no HqAuthenticationToken at all (which should only happen if there is a faulty change in
         * configuration), then go down the filter stack, allowing Spring Security to fail on lack of security
         * credentials. Spring Security tends to throw some sort of meaningful error indicating what is missing.
         */
        if (auth != null) {
            logger.debug("Authentication exists => " + auth);
            String sessionId = request.getParameter("sessionId");
            if (sessionId != null) {
                logger.debug("SessionId found => " + sessionId);

                if (!sessionIdExpired(request, sessionId)) {

                    UsernamePasswordAuthenticationToken newToken = createHqAuthenticationToken(auth, sessionId);
                    logger.debug("Replacing existing authentication with new one => " + newToken);
                    SecurityContextHolder.getContext().setAuthentication(newToken);

                }
            } else {
                if (auth instanceof HqAuthenticationToken) {
                    HqAuthenticationToken token = (HqAuthenticationToken) auth;
                    if (sessionIdExpired(request, token.getSessionId())) {
                        throw new BadCredentialsException("Session has expired. Re-login.");
                    }
                } else {
                    logger.debug("sessionId not found at all. Unable to check against Hyperic.");
                }
            }
        } else {
            logger.debug("Authentication is currently empty. Unable to check against Hyperic.");
        }

        chain.doFilter(request, response);
    }

    /**
     * This takes an existing Authentication object, and converts it into an tc Server plugin-based object.
     * 
     * @param auth
     * @param sessionId
     * @return
     */
    private UsernamePasswordAuthenticationToken createHqAuthenticationToken(Authentication auth, String sessionId) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        auths.addAll(auth.getAuthorities());
        auths.add(new GrantedAuthorityImpl(defaultRole));
        UsernamePasswordAuthenticationToken newToken = new HqAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), auths, sessionId);
        return newToken;
    }

    /**
     * This method makes a request to a hook inside HQU tomcatserverconfig plugin. If the returned status code indicates
     * a redirect, then this would be expiration of the session, and being sent back to the login page. Otherwise, it
     * indicates the session is active.
     * 
     * @param request
     * @param sessionId
     * @return
     */
    private boolean sessionIdExpired(ServletRequest request, String sessionId) {
        String basePath = RequestUtils.getLocalHqUrl(request);

        GetMethod method = new GetMethod(basePath + "/hqu/tomcatserverconfig/tomcatserverconfig/checkSessionStatus.hqu");

        logger.debug("About to check status at " + method.toString());

        method.setFollowRedirects(false);
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        method.setRequestHeader("Cookie", "JSESSIONID=" + sessionId);
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("User's session has expired. Redirecting to login page");
                return true;
            }
        } catch (HttpException e) {
            logger.warn("Failure occurred when checking if session has expired. Redirecting to login page.", e);
            return true;
        } catch (IOException e) {
            logger.warn("Failure occurred when checking if session has expired. Redirecting to login page.", e);
            return true;
        }

        return false;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }
}
