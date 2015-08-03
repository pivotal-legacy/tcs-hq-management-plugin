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

package com.springsource.hq.plugin.tcserver.serverconfig.web.services;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.springsource.hq.plugin.tcserver.serverconfig.Settings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.RemoteSettings;
import com.springsource.hq.plugin.tcserver.serverconfig.web.support.SettingsLoaderException;
import com.thoughtworks.xstream.XStream;

/**
 * Default {@link SettingsLoader} implementation. Apache Commons HTTP Client and XStream are used to serialize and
 * deserialize the {@link Settings} from HQ. Connection settings are stored inside the {@link RemoteSettings}.
 * 
 * @since 2.0
 */
@org.springframework.stereotype.Service
public class SettingsLoaderImpl implements SettingsLoader {

    private static final String REQUEST_PARAMETER_CSRF_NONCE = "org.apache.catalina.filters.CSRF_NONCE";

    private HttpClient httpClient;

    private XStream xstream;

    private static final Log logger = LogFactory.getLog(SettingsLoaderImpl.class);

    public static final String SESSION_EXPIRED_MESSAGE = "Your session has expired, please login again in a new window and then come back to this page.";

    public SettingsLoaderImpl() {
        httpClient = new HttpClient();
        xstream = new XStream();
    }

    public RemoteSettings getConfiguration(String eid, String sessionId, String basePath, String csrfNonce) throws SettingsLoaderException {
        try {
            logger.debug("Attempting to load Settings for '" + eid + "'");
            GetMethod method = new GetMethod(basePath + "/hqu/tomcatserverconfig/tomcatserverconfig/getConfiguration.hqu");
            configureMethod(method, eid, sessionId, csrfNonce);
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("Unable to load Settings for '" + eid + "', HQ session expired");
                throw new SettingsLoaderException(SESSION_EXPIRED_MESSAGE);
            } else if (method.getStatusCode() >= 400) {
                logger.warn("Unable to load Settings for '" + eid + "', " + method.getStatusCode() + " " + method.getStatusText());
                throw new SettingsLoaderException(method.getStatusText());
            }
            Settings settings = (Settings) xstream.fromXML(method.getResponseBodyAsString());
            RemoteSettings remoteSettings = new RemoteSettings();
            remoteSettings.setSettings(settings);
            remoteSettings.setBasePath(basePath);
            remoteSettings.setSessionId(sessionId);
            remoteSettings.setCsrfNonce(csrfNonce);
            logger.info("Loaded Settings for '" + eid + "'");
            return remoteSettings;
        } catch (SSLHandshakeException e) {
            logger.error("Server SSL certificate is untrusted: " + e.getMessage(), e);
            throw new SettingsLoaderException("Unable to load settings because the server is using an untrusted SSL certificate.  "
                + "Please check the documentation for more information.", e);
        } catch (IOException e) {
            logger.error("Unable to load Settings for '" + eid + "': " + e.getMessage(), e);
            throw new SettingsLoaderException("Unable to load settings because of a server error, please check the logs for more details", e);
        }
    }

    public void revertToPreviousConfiguration(RemoteSettings remoteSettings) throws SettingsLoaderException {
        try {
            logger.debug("Attempting to revert to previous configuration for '" + remoteSettings.getSettings().getEid() + "'");
            PostMethod method = new PostMethod(remoteSettings.getBasePath()
                + "/hqu/tomcatserverconfig/tomcatserverconfig/revertToPreviousConfiguration.hqu");
            configureMethod(method, remoteSettings.getSettings().getEid(), remoteSettings.getSessionId(), remoteSettings.getCsrfNonce());
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("Unable to revert to previous configuration for '" + remoteSettings.getSettings().getEid() + "', HQ session expired");
                throw new SettingsLoaderException(SESSION_EXPIRED_MESSAGE);
            } else if (method.getStatusCode() >= 400) {
                logger.warn("Unable to revert to previous configuration for '" + remoteSettings.getSettings().getEid() + "', "
                    + method.getStatusCode() + " " + method.getStatusText());
                throw new SettingsLoaderException(method.getStatusText());
            }
            remoteSettings.setCsrfNonce(method.getResponseBodyAsString());
            logger.info("Reverted to previous configuration for '" + remoteSettings.getSettings().getEid() + "'");
        } catch (SSLHandshakeException e) {
            logger.error("Server SSL certificate is untrusted: " + e.getMessage(), e);
            throw new SettingsLoaderException(
                "Unable to revert to previous configuration because the server is using an untrusted SSL certificate.  "
                    + "Please check the documentation for more information.", e);
        } catch (IOException e) {
            logger.error("Unable to revert to previous configuration for '" + remoteSettings.getSettings().getEid() + "': " + e.getMessage(), e);
            throw new SettingsLoaderException(
                "Reverting to previous configuration failed because of a server error, please check the logs for more details", e);
        }
    }

    public void saveConfiguration(RemoteSettings remoteSettings) throws SettingsLoaderException {
        try {
            logger.debug("Attempting to save Settings for '" + remoteSettings.getSettings().getEid() + "'");
            String xml = xstream.toXML(remoteSettings.getSettings());
            PostMethod method = new PostMethod(remoteSettings.getBasePath() + "/hqu/tomcatserverconfig/tomcatserverconfig/saveConfiguration.hqu");
            configureMethod(method, remoteSettings.getSettings().getEid(), remoteSettings.getSessionId(), remoteSettings.getCsrfNonce());
            method.setRequestEntity(new StringRequestEntity(xml, "text/xml", "UTF-8"));
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("Unable to save Settings for '" + remoteSettings.getSettings().getEid() + "', HQ session expired");
                throw new SettingsLoaderException(SESSION_EXPIRED_MESSAGE);
            } else if (method.getStatusCode() >= 400) {
                logger.warn("Unable to save Settings for '" + remoteSettings.getSettings().getEid() + "', " + method.getStatusCode() + " "
                    + method.getStatusText());
                throw new SettingsLoaderException(method.getStatusText());
            }
            remoteSettings.setCsrfNonce(method.getResponseBodyAsString());
            logger.info("Saved Settings for '" + remoteSettings.getSettings().getEid() + "'");
        } catch (SSLHandshakeException e) {
            logger.error("Server SSL certificate is untrusted: " + e.getMessage(), e);
            throw new SettingsLoaderException("Unable to save settings because the server is using an untrusted SSL certificate.  "
                + "Please check the documentation for more information.", e);
        } catch (IOException e) {
            logger.error("Unable to save Settings for '" + remoteSettings.getSettings().getEid() + "': " + e.getMessage(), e);
            throw new SettingsLoaderException("Saving settings failed because of a server error, please check the logs for more details", e);
        }
    }

    public void saveConfigurationFile(String fileName, String config, RemoteSettings remoteSettings) throws SettingsLoaderException {
        try {
            logger.debug("Attempting to save '" + fileName + "' for '" + remoteSettings.getSettings().getEid() + "'");
            PostMethod method = new PostMethod(remoteSettings.getBasePath() + "/hqu/tomcatserverconfig/tomcatserverconfig/saveConfigurationFile.hqu");
            configureMethod(method, remoteSettings.getSettings().getEid(), remoteSettings.getSessionId(), remoteSettings.getCsrfNonce());
            Part[] parts = new Part[] { new StringPart("fileName", fileName), new StringPart("file", config) };
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("Unable to save '" + fileName + "' configuration for '" + remoteSettings.getSettings().getEid() + "', HQ session expired");
                throw new SettingsLoaderException(SESSION_EXPIRED_MESSAGE);
            } else if (method.getStatusCode() >= 400) {
                logger.warn("Unable to save '" + fileName + "' configuration for '" + remoteSettings.getSettings().getEid() + "', "
                    + method.getStatusCode() + " " + method.getStatusText());
                throw new SettingsLoaderException(method.getStatusText());
            }
            remoteSettings.setCsrfNonce(method.getResponseBodyAsString());
            logger.info("Saved '" + fileName + "' for '" + remoteSettings.getSettings().getEid() + "'");
        } catch (SSLHandshakeException e) {
            logger.error("Server SSL certificate is untrusted: " + e.getMessage(), e);
            throw new SettingsLoaderException("Unable to save '" + fileName + "' because the server is using an untrusted SSL certificate.  "
                + "Please check the documentation for more information.", e);
        } catch (IOException e) {
            logger.error("Unable to save '" + fileName + "' for '" + remoteSettings.getSettings().getEid() + "': " + e.getMessage(), e);
            throw new SettingsLoaderException("Saving configuration file failed because of a server error, please check the logs for more details", e);
        }
    }

    public void restartServer(RemoteSettings remoteSettings) throws SettingsLoaderException {
        try {
            logger.debug("Attempting to restart server for '" + remoteSettings.getSettings().getEid() + "'");
            PostMethod method = new PostMethod(remoteSettings.getBasePath() + "/hqu/tomcatserverconfig/tomcatserverconfig/restartServer.hqu");
            method.addParameter("isJmxListenerChanged", Boolean.toString(remoteSettings.isJmxListenerChanged()));
            configureMethod(method, remoteSettings.getSettings().getEid(), remoteSettings.getSessionId(), remoteSettings.getCsrfNonce());
            httpClient.executeMethod(method);
            if (method.getStatusCode() >= 300 && method.getStatusCode() < 400) {
                logger.info("Unable to restart server for '" + remoteSettings.getSettings().getEid() + "', HQ session expired");
                throw new SettingsLoaderException(SESSION_EXPIRED_MESSAGE);
            } else if (method.getStatusCode() >= 400) {
                logger.warn("Unable to restart server for '" + remoteSettings.getSettings().getEid() + "', " + method.getStatusCode() + " "
                    + method.getStatusText());
                throw new SettingsLoaderException(method.getStatusText());
            }
            remoteSettings.setCsrfNonce(method.getResponseBodyAsString());
            logger.debug("Restarted server for '" + remoteSettings.getSettings().getEid() + "'");
        } catch (SSLHandshakeException e) {
            logger.error("Server SSL certificate is untrusted: " + e.getMessage(), e);
            throw new SettingsLoaderException("Unable to restart server because the server is using an untrusted SSL certificate.  "
                + "Please check the documentation for more information.", e);
        } catch (IOException e) {
            logger.error("Unable to restart server for '" + remoteSettings.getSettings().getEid() + "': " + e.getMessage(), e);
            throw new SettingsLoaderException("Server restart failed because of a server error, please check the logs for more details", e);
        }
    }

    private void configureMethod(HttpMethod method, String eid, String sessionId, String csrfNonce) {
        method.setFollowRedirects(false);
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        method.setRequestHeader("Cookie", "JSESSIONID=" + sessionId);
        method.setQueryString(new NameValuePair[] { new NameValuePair("eid", eid), new NameValuePair(REQUEST_PARAMETER_CSRF_NONCE, csrfNonce) });
    }

}
