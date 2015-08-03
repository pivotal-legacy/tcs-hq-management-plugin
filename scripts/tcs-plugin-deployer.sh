#!/bin/sh 
#
# author: Bruce Snyder
# 
# A script to help with development build deployment to 
# both the HQ server and HQ agent. This script requires 
# that the HQ server and an HQ agent be installed on 
# the same machine. 
# 

#
# The USER_DIR may need to be changed 
# to point to the appropriate home dir. 
# 
USER_DIR=$HOME

# 
# In what directory does the tcserver-plugin source code live? 
# 
TCSERVER_PLUGIN_SRC_DIR=${SPRING_ROOT:-$USER_DIR/src/springsource/tc-server}/hq-plugin

# 
# In what directory is the HQ server installed? 
# 
HQ_SERVER_DIR=${TCS_APP_DIR:-$USER_DIR/hq}/server-${HQ45_VERSION:-4.5.0.BUILD-SNAPSHOT}${HQ45_RELEASE:--EE}

# 
# In what directory is the HQ agent installed? 
# 
HQ_AGENT_DIR=${TCS_APP_DIR:-$USER_DIR/hq}/agent-${HQ45_VERSION:-4.5.0.BUILD-SNAPSHOT}${HQ45_RELEASE:--EE}


HQ_SERVER_DEPLOY_DIR=$HQ_SERVER_DIR/hq-engine/${HQ45_DEPLOY_SUBDIR:-hq-server/webapps}

# The tomcatserver config web app
# (com.springsource.hq.plugin.tcserver.serverconfig.web)
# 
TCSERVER_TOMCATSERVERCONFIG_WAR=$HQ_SERVER_DEPLOY_DIR/tomcatserverconfig.war
TCSERVER_TOMCATSERVERCONFIG_FOLDER=$HQ_SERVER_DEPLOY_DIR/tomcatserverconfig

# The tcserver-plugin for the server
# (com.springsource.hq.plugin.tcserver.plugin)
TCSERVER_PLUGIN_JAR_SERVER_LOCATION=$HQ_SERVER_DEPLOY_DIR/ROOT/WEB-INF/hq-plugins/springsource-tcserver-plugin.jar


#
# HQU Plugins
# 

# HQU plugin deploy dir 
HQ_SERVER_HQU_DEPLOY_DIR=$HQ_SERVER_DEPLOY_DIR/${HQU_DEPLOY_SUBDIR:-ROOT/hqu}

# (com.springsource.hq.plugin.tcserver.ui.tcserverclient)
TCSERVER_CLIENT_HQU_PLUGIN=$HQ_SERVER_HQU_DEPLOY_DIR/tcserverclient/
# (com.springsource.hq.plugin.tcserver.ui.tomcatappmgmt)
TCSERVER_TOMCATAPPMGMT_HQU_PLUGIN=$HQ_SERVER_HQU_DEPLOY_DIR/tomcatappmgmt/
# (com.springsource.hq.plugin.tcserver.ui.tomcatserverconfig)
TCSERVER_TOMCATSERVERCONFIG_HQU_PLUGIN=$HQ_SERVER_HQU_DEPLOY_DIR/tomcatserverconfig/

# HQ agent plugin deploy dir 
HQ_AGENT_PLUGIN_DEPLOY_DIR=$HQ_AGENT_DIR/bundles/agent-${HQ45_VERSION:-4.5.0.BUILD-SNAPSHOT}

# The tcserver-plugin JAR file
TCSERVER_PLUGIN_JAR_AGENT_LOCATION=$HQ_AGENT_PLUGIN_DEPLOY_DIR/pdk/plugins/springsource-tcserver-plugin.jar

HQ_BUILD_DIR=$TCSERVER_PLUGIN_SRC_DIR/build-tcserver-plugin/target/artifacts
TCSERVER_HQ_PLUGIN_NAME=springsource-tc-server-hq-plugin*
TCSERVER_HQ_PLUGIN_ZIP=springsource-tc-server-hq-plugin*.zip 
HQ_SERVER_ZIP=$HQ_BUILD_DIR/$TCSERVER_HQ_PLUGIN_ZIP

HQU_DIR=$HQ_SERVER_HQU_DEPLOY_DIR

TMP=/tmp



###############################################################################
# Functions begin here
###############################################################################

deploy_zip() {
    echo 
    echo " Deploying the tc Server HQ Plugin ..."
    echo 

    clean_tomcat_temp
    remove_zip
    move_to $TMP
    expand $HQ_SERVER_ZIP
    run_installer 
    copy_plugin_jar $TCSERVER_PLUGIN_JAR_SERVER_LOCATION $TCSERVER_PLUGIN_JAR_AGENT_LOCATION
    sleep 2
    remove $TMP/$TCSERVER_HQ_PLUGIN_NAME
}

move_to() {
    echo "Moving to $1" 
    cd $1
}

expand() {
    echo "Expanding $1"
    jar xf $1
}

run_installer() {
    sh $TMP/springsource-tc-server-hq-plugin-*/bin/install.sh $HQ_SERVER_DIR
}

copy_plugin_jar() {
    echo
    echo "Copying JAR from $1 to $2"
    echo 
    cp $1 $2
}

remove() {
    echo "Removing $1"
    rm -rf $1
}

clean_tomcat_temp() {
    remove $HQ_SERVER_DIR/hq-engine/hq-server/temp/hqu
    remove $HQ_SERVER_DIR/hq-engine/hq-server/temp/pdknull
}

remove_zip() {
    remove $TCSERVER_TOMCATSERVERCONFIG_WAR
    remove $TCSERVER_TOMCATSERVERCONFIG_FOLDER
    remove $TCSERVER_PLUGIN_JAR_SERVER_LOCATION
    remove $TCSERVER_CLIENT_HQU_PLUGIN
    remove $TCSERVER_TOMCATAPPMGMT_HQU_PLUGIN
    remove $TCSERVER_TOMCATSERVERCONFIG_HQU_PLUGIN
    remove $TCSERVER_PLUGIN_JAR_AGENT_LOCATION
}

copy_file() {
    FILENAME_ARG=$2
    SRC_FILENAME=`find $TCSERVER_PLUGIN_SRC_DIR -name $FILENAME_ARG | grep -v target`
    DEST_FILENAME=`find $HQU_DIR -name $FILENAME_ARG`

    echo " I'm ready to copy "
    echo "  FROM HERE: $SRC_FILENAME "
    echo "    TO HERE: $DEST_FILENAME " 
    echo " Is this correct? (y): " 
    read ANSWER 

    if [ x$ANSWER = "x" -o x$ANSWER != "xn" -o x$ANSWER != "xN" ] ; then 
        cp $SRC_FILENAME $DEST_FILENAME
        move_dir $DEST_FILENAME
    else 
        echo " Fine! Don't execute the command" 
    fi 
}

move_dir() {
    TCSERVER_PLUGIN_APP_DIR=$1 
    APP_DIR=`echo $TCSERVER_PLUGIN_APP_DIR | sed "s,$HQU_DIR\/,," | awk -F/ '{print $1}'`
    FQ_APP_DIR=$HQU_DIR/$APP_DIR

    mv $FQ_APP_DIR $FQ_APP_DIR.deploy
    mv $FQ_APP_DIR.deploy $FQ_APP_DIR
}

help() {
    echo 
    echo " ERROR - Please enter a command " 
    echo 
    echo "  Available commands:"
    echo "   copy <filname> - Copies a Groovy or GSP file to the HQ deploy directory"
    echo "   deploy - Used to deploy the zip files to the HQ server and agent"
    echo "   remove - Used to remove the zip files from the HQ server and agent"
    echo 
    echo "  Examples: "
    echo "   $0  copy TomcatappmgmtController.groovy" 
    echo "    OR " 
    echo "   $0 deploy" 
    echo "    OR " 
    echo "   $0 remove" 
    echo 
}

finish() {
    if [ x$FUNCTION != x ] ; then 
        echo 
        echo " The $FUNCTION function is complete" 
        echo 
    fi
}

main() { 
    FUNCTION=$1

    case $FUNCTION in 
        #"copy"  ) copy_file $@ ;; 
        "deploy") deploy_zip   ;; 
        #"remove") remove_zip   ;;
        *       ) help         ;; 
    esac

    finish
}

main $@
