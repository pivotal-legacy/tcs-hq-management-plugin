#!/bin/sh
#
# author: Greg Turnquist
# author: Bruce Snyder
# 
# This script is used for testing features of tcserver plugin's command-line 
# interface. It contains only functions for testing. This script is meant to
# be included in the head of an actual test script that utilizes the
# functions. 
#
# Overrides that can be set in .profile or .bashrc
#   TCS_SCRIPT_DIR - path to dev-tools scripts
#   TCS_APP_DIR  - path where hq is installed
# 
# 

#
# The USER_DIR may need to be changed 
# to point to the appropriate home dir. 
# 
USER_DIR=$HOME
SCRIPT_DIR=${TCS_SCRIPT_DIR:-`dirname $0`}


# 
# In what directory does the hqapi runtime distribution live? 
# 
TCSADM_DIR=${TCS_APP_DIR:-$USER_DIR/hq}/springsource-tcserver-scripting-client



# 
# Set up some variables for the data dirs
# 
TEST_DIR=${SPRING_ROOT:-$USER_DIR/src/springsource/tc-server}/hq-plugin/scripts
DATA_DIR=$TEST_DIR/data
START_DIR=$DATA_DIR/start
END_DIR=$DATA_DIR/end


# 
# The location of the hqapi.sh script 
#
TCSADM_SH=$TCSADM_DIR/bin/tcsadmin.sh

# 
# Show the top level command help page
# 
show_help() {
    $TCSADM_SH
}

# 
# List the existing tcServer instances
# 
list_servers() {
    $TCSADM_SH list-servers
}

# 
# @serverid
# 
stop() {
    $TCSADM_SH stop --serverid $1
}

#
# @groupname
#
stop_group() {
    $TCSADM_SH stop --groupname $1
}

#
# @appname
# @serverid
#
stop_application() {
    $TCSADM_SH stop-application --application $1 --serverid $2
}

# 
# @serverid
# 
start() {
    $TCSADM_SH start --serverid $1
}

#
# @groupname
#
start_group() {
    $TCSADM_SH start --groupname $1
}


#
# @appname
# @serverid
#
start_application() {
    $TCSADM_SH start-application --application $1 --serverid $2
}

# 
# @serverid
# 
restart() {
    $TCSADM_SH restart --serverid $1
}

#
# @groupname
#
restart_group() {
    $TCSADM_SH restart --groupname $1
}

# 
# @serverid
# @file
# @targetfile
# 
get_file() {
    $TCSADM_SH get-file --serverid $1 --file $2 --targetfile $3
}

# 
# @serverid
# @file
# @targetfile
put_file() {
    $TCSADM_SH put-file --serverid $1 --file $2 --targetfile $3
}

# 
# Deploy a local application 
# @serverid
# @localpath
# 
deploy_app_local() {
    $TCSADM_SH deploy-application --serverid $1 --localpath $2
}

# 
# Undeploy a local application 
# @serverid
# @application
# 
undeploy_app_local() {
    $TCSADM_SH undeploy-application --serverid $1 --application $2
}

# 
# Reload a local application 
# @serverid
# @application
# 
reload_app() {
    $TCSADM_SH reload-application --serverid $1 --application $2
}

# 
# Deploy a remote application 
# @serverid
# @remotepath
# 
deploy_app_remote() {
    $TCSADM_SH deploy-application --serverid $1 --remotepath $2
}

# 
# Undeploy a remote application 
# @serverid
# @application 
# 
undeploy_app_remote() {
    $TCSADM_SH undeploy-application --serverid $1 --application $2
}

# 
# Alter the name of a server
# @serverid
# @name
# 
modify_server() {
    $TCSADM_SH modify-server --serverid $1 --name $2
}

# 
# @serverid
# 
list_apps() {
    $TCSADM_SH list-applications --serverid $1
}

# 
#@group-name
# 
create_group() {
$TCSADM_SH create-group --name $1
}

# 
# @groupname
# @serverid
# 
add_server_to_group() {
    $TCSADM_SH add-server-to-group --groupname $1 --serverid $2
}

#
# @groupname
# serverid
#
remove_server_from_group() {
    $TCSADM_SH remove-server-from-group --groupname $1 --serverid $2
}

list_groups() {
    $TCSADM_SH list-groups
}

# 
# @name
# 
delete_group() {
    $TCSADM_SH delete-group --name $1
}

# 
# @serverid
# 
list_jvm_options() {
    $TCSADM_SH list-jvm-options --serverid $1
}

# 
# @serverid
# @options
# 
set_jvm_options() {
    $TCSADM_SH set-jvm-options --serverid $1 --options=$2
}

#
# @serverid
#
revert() {
    $TCSADM_SH revert-to-previous-configuration --serverid $1
}

log() {
    echo 
    echo "$1"
    echo
}

log_to_file() {
    echo "$1" >> test.log
}
