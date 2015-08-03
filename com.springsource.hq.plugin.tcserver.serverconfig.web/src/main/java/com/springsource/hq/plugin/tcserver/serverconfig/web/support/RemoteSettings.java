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

import java.io.Serializable;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;

/**
 * Holder for setting from HQ. Also tracks information about how to connect with HQ and basic change tracking info.
 * 
 * @since 2.0
 */
public class RemoteSettings implements Serializable {

    private static final long serialVersionUID = 6860470943897086096L;

    /**
     * Path to connect services back to in HQ
     */
    private String basePath;

    /**
     * Flag used to indicate there are differences between the working copy of the config and the server copy.
     */
    private boolean changePending = false;

    /**
     * Flag used to indicate the server needs to be restarted to pick up changes in the configuration
     */
    private boolean restartPending = false;

    /**
     * Flag used to indicate the server had a JMX configuration change.
     */
    private boolean jmxListenerChanged = false;

    /**
     * Session id for the use in HQ
     */
    private String sessionId;

    private transient Settings settings;

    private boolean readOnly = false;

    private volatile String csrfNonce;

    public String getBasePath() {
        return basePath;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isChangePending() {
        return changePending;
    }

    public boolean isRestartPending() {
        return restartPending;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setChangePending(boolean changePending) {
        this.changePending = changePending;
    }

    public void setRestartPending(boolean restartPending) {
        this.restartPending = restartPending;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isJmxListenerChanged() {
        return jmxListenerChanged;
    }

    public void setJmxListenerChanged(boolean jmxListenerChanged) {
        this.jmxListenerChanged = jmxListenerChanged;
    }

    public void setCsrfNonce(String csrfNonce) {
        this.csrfNonce = csrfNonce;
    }

    public String getCsrfNonce() {
        return csrfNonce;
    }
}
