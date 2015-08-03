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

package com.springsource.hq.plugin.tcserver.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.LiveDataPlugin;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;
import org.hyperic.hq.product.ProductPluginManager;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.plugin.appmgmt.ApplicationManager;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.FileOwnershipChanger;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.FilePermissionsChanger;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.ScriptingApplicationManager;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.StandardFileOwnershipChangerFactory;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.StandardFilePermissionsChangerFactory;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.TomcatJmxApplicationManager;
import com.springsource.hq.plugin.tcserver.plugin.appmgmt.TomcatJmxScriptingApplicationManager;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.DefaultServerConfigManager;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.ServerConfigManager;
import com.springsource.hq.plugin.tcserver.plugin.wrapper.MxUtilJmxUtils;

public class TomcatLiveDataPlugin extends LiveDataPlugin {

    private static final String CMD_DEPLOY = "deployApp";

    private static final String CMD_RELOAD_APPLICATIONS = "reloadApplications";

    private static final String CMD_START_APPLICATIONS = "startApplications";

    private static final String CMD_STOP_APPLICATIONS = "stopApplications";

    private static final String CMD_UNDEPLOY_APPLICATIONS = "undeployApplications";

    private static final String CMD_DEPLOY_APPLICATION = "deployApplication";

    private static final String CMD_LIST_APPLICATIONS = "listApplications";

    // ------ Begin used by manageApplications.gsp ------

    private static final String CMD_RELOAD = "reloadApps";

    private static final String CMD_START = "startApps";

    private static final String CMD_STOP = "stopApps";

    private static final String CMD_UNDEPLOY = "undeployApps";

    // Used by both manageApplcations.gsp and TomcatappmgmtController.groovy
    private static final String CMD_LIST = "listApps";

    // ------ End used by manageApplications.gsp ------

    private static final String CMD_GET_CONFIG = "getConfiguration";

    private static final String CMD_SAVE_CONFIG = "saveConfiguration";

    private static final String CMD_PREPARE_FILE = "prepareFile";

    private static final String CMD_PUT_FILE = "putFile";

    private static final String CMD_GET_FILE = "getFile";

    private static final String CMD_COPY_FILE = "copyFile";

    private static final String CMD_FILE_EXISTS = "fileExists";

    private static final String CMD_PUT_JVM_OPTIONS = "putJvmOptions";

    private static final String CMD_GET_JVM_OPTIONS = "getJvmOptions";

    private static final String CMD_GET_TEMP_DIR = "getTemporaryWebAppDirectory";

    private static final String CMD_GET_SERVICE_HOST_MAPPINGS = "getServiceHostMappings";

    private static final String CMD_REMOVE_TEMP_WAR_FILE = "removeTemporaryWarFile";

    private static final String CMD_GET_APPBASE = "getAppBase";

    private static final String CMD_REVERT_PREVIOUS_CONFIGURATION = "revertToPreviousConfiguration";

    private static final String CMD_CHANGE_FILE_PERMISSIONS_AND_OWNERSHIP = "changeFilePermissionsAndOwnership";

    private static final String CONFIG_FILE_LOCATION = "FILE_LOCATION";

    private static final List<String> COMMANDS = new ArrayList<String>();

    static {
        COMMANDS.add(CMD_DEPLOY);
        COMMANDS.add(CMD_GET_TEMP_DIR);
        COMMANDS.add(CMD_LIST);
        COMMANDS.add(CMD_UNDEPLOY);
        COMMANDS.add(CMD_STOP);
        COMMANDS.add(CMD_START);
        COMMANDS.add(CMD_RELOAD);
        COMMANDS.add(CMD_GET_CONFIG);
        COMMANDS.add(CMD_SAVE_CONFIG);
        COMMANDS.add(CMD_PUT_FILE);
        COMMANDS.add(CMD_PREPARE_FILE);
        COMMANDS.add(CMD_GET_FILE);
        COMMANDS.add(CMD_COPY_FILE);
        COMMANDS.add(CMD_FILE_EXISTS);
        COMMANDS.add(CMD_PUT_JVM_OPTIONS);
        COMMANDS.add(CMD_GET_JVM_OPTIONS);
        COMMANDS.add(CMD_GET_SERVICE_HOST_MAPPINGS);
        COMMANDS.add(CMD_LIST_APPLICATIONS);
        COMMANDS.add(CMD_RELOAD_APPLICATIONS);
        COMMANDS.add(CMD_START_APPLICATIONS);
        COMMANDS.add(CMD_STOP_APPLICATIONS);
        COMMANDS.add(CMD_UNDEPLOY_APPLICATIONS);
        COMMANDS.add(CMD_DEPLOY_APPLICATION);
        COMMANDS.add(CMD_REMOVE_TEMP_WAR_FILE);
        COMMANDS.add(CMD_GET_APPBASE);
        COMMANDS.add(CMD_REVERT_PREVIOUS_CONFIGURATION);
        COMMANDS.add(CMD_CHANGE_FILE_PERMISSIONS_AND_OWNERSHIP);
    }

    private ApplicationManager applicationManager;

    private ScriptingApplicationManager scriptingApplicationManager;

    private ServerConfigManager serverConfigManager;

    private volatile FileOwnershipChanger fileOwnershipChanger;

    private volatile FilePermissionsChanger filePermissionsChanger;

    private final Log logger = LogFactory.getLog(TomcatLiveDataPlugin.class);

    public TomcatLiveDataPlugin() {
    }

    /**
     * Constructor used for testing
     * 
     * @param applicationManager
     * @param serverConfigManager
     */
    TomcatLiveDataPlugin(final ApplicationManager applicationManager, final ServerConfigManager serverConfigManager) {
        this.applicationManager = applicationManager;
        this.serverConfigManager = serverConfigManager;
    }

    @Override
    public void init(PluginManager pluginManager) throws PluginException {
        super.init(pluginManager);

        if (((ProductPluginManager) pluginManager.getParent()).isClient()) {
            MxUtilJmxUtils jmxUtils = new MxUtilJmxUtils();
            FilePermissionsChanger filePermissionsChanger = new StandardFilePermissionsChangerFactory().getFilePermissionsChanger();
            FileOwnershipChanger fileOwnershipChanger = new StandardFileOwnershipChangerFactory().getFileOwnershipChanger();

            this.applicationManager = new TomcatJmxApplicationManager(jmxUtils, filePermissionsChanger, fileOwnershipChanger);
            this.scriptingApplicationManager = new TomcatJmxScriptingApplicationManager(jmxUtils, filePermissionsChanger, fileOwnershipChanger);

            this.serverConfigManager = new DefaultServerConfigManager();

            this.filePermissionsChanger = filePermissionsChanger;
            this.fileOwnershipChanger = fileOwnershipChanger;
        }
    }

    @Override
    public String[] getCommands() {
        return COMMANDS.toArray(new String[COMMANDS.size()]);
    }

    @Override
    public Object getData(final String command, final ConfigResponse config) throws PluginException {
        logger.debug("COMMAND = " + command);
        logger.debug("CONFIG = " + config);
        if (CMD_LIST.equals(command)) {
            return applicationManager.list(config);
        } else if (CMD_LIST_APPLICATIONS.equals(command)) {
            return scriptingApplicationManager.list(config);
        } else if (CMD_START_APPLICATIONS.equals(command)) {
            return scriptingApplicationManager.start(config);
        } else if (CMD_STOP_APPLICATIONS.equals(command)) {
            return scriptingApplicationManager.stop(config);
        } else if (CMD_RELOAD_APPLICATIONS.equals(command)) {
            return scriptingApplicationManager.reload(config);
        } else if (CMD_UNDEPLOY_APPLICATIONS.equals(command)) {
            return scriptingApplicationManager.undeploy(config);
        } else if (CMD_DEPLOY_APPLICATION.equals(command)) {
            return scriptingApplicationManager.deploy(config);
        } else if (CMD_GET_SERVICE_HOST_MAPPINGS.equals(command)) {
            return applicationManager.getServiceHostMappings(config);
        } else if (CMD_GET_TEMP_DIR.equals(command)) {
            return getTemporaryWebAppDirectory();
        } else if (CMD_DEPLOY.equals(command)) {
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put(CMD_DEPLOY, applicationManager.deploy(config));
            return resultMap;
        } else if (CMD_UNDEPLOY.equals(command)) {
            return applicationManager.undeploy(config);
        } else if (CMD_START.equals(command)) {
            return applicationManager.start(config);
        } else if (CMD_STOP.equals(command)) {
            return applicationManager.stop(config);
        } else if (CMD_RELOAD.equals(command)) {
            return applicationManager.reload(config);
        } else if (CMD_GET_CONFIG.equals(command)) {
            return serverConfigManager.getConfiguration(config);
        } else if (CMD_SAVE_CONFIG.equals(command)) {
            serverConfigManager.saveConfiguration(config);
            return null;
        } else if (CMD_PUT_FILE.equals(command)) {
            serverConfigManager.putFile(config);
            return null;
        } else if (CMD_PREPARE_FILE.equals(command)) {
            serverConfigManager.prepareFile(config);
            return null;
        } else if (CMD_GET_FILE.equals(command)) {
            return serverConfigManager.getFile(config);
        } else if (CMD_COPY_FILE.equals(command)) {
            serverConfigManager.copyFile(config);
            return null;
        } else if (CMD_FILE_EXISTS.equals(command)) {
            return serverConfigManager.fileExists(config);
        } else if (CMD_PUT_JVM_OPTIONS.equals(command)) {
            serverConfigManager.putJvmOpts(config);
            return null;
        } else if (CMD_GET_JVM_OPTIONS.equals(command)) {
            return serverConfigManager.getJvmOpts(config);
        } else if (CMD_REMOVE_TEMP_WAR_FILE.equals(command)) {
            applicationManager.removeTemporaryWarFile(config);
            return null;
        } else if (CMD_GET_APPBASE.equals(command)) {
            return applicationManager.getAppBase(config);
        } else if (CMD_REVERT_PREVIOUS_CONFIGURATION.equals(command)) {
            serverConfigManager.revertToPreviousConfiguration(config);
            return null;
        } else if (CMD_CHANGE_FILE_PERMISSIONS_AND_OWNERSHIP.equals(command)) {
            changeFilePermissionsAndOwnership(config);
            return null;
        }
        final String exceptionMessage = "Unknown command: " + command;
        throw new PluginException(exceptionMessage);
    }

    private void changeFilePermissionsAndOwnership(ConfigResponse configResponse) throws PluginException {
        File file = new File(configResponse.getValue(CONFIG_FILE_LOCATION));
        this.filePermissionsChanger.changeFilePermissions(file);
        this.fileOwnershipChanger.changeFileOwnership(file, configResponse.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_USERNAME),
            configResponse.getValue(Utils.SERVER_RESOURCE_CONFIG_PROCESS_GROUP));
    }

    private String getTemporaryWebAppDirectory() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String userDir = System.getProperty("user.dir");
        userDir = userDir.replace("\\", "/");
        if (tempDir.startsWith(".") && !tempDir.startsWith("..")) {
            tempDir = tempDir.replaceFirst("\\.", userDir);
        } else if (tempDir.startsWith("..")) {
            int index = userDir.lastIndexOf("/");
            if (index == 0) {
                index = 0;
            } else if (index == userDir.length()) {
                index = userDir.substring(0, index - 1).lastIndexOf("/");
            }
            userDir = userDir.substring(0, index);
            tempDir = tempDir.replaceFirst("..", userDir);
        }
        return tempDir.replace("\\", "/");
    }

}
