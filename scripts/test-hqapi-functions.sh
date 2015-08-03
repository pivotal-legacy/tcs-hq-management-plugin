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
HQAPI_DIR=${TCS_APP_DIR:-$USER_DIR/hq}/hqapi1-3.0



# 
# Set up some variables for the data dirs
# 
DATA_DIR=$SCRIPT_DIR/data
START_DIR=$DATA_DIR/start
END_DIR=$DATA_DIR/end


# 
# The location of the hqapi.sh script 
#
HQAPI_SH=$HQAPI_DIR/bin/hqapi.sh 

# 
# Show the top level command help page
# 
show_help() {
    $HQAPI_SH tcserver
}

# 
# List the existing tcServer instances
# 
list_servers() {
    $HQAPI_SH tcserver list-servers
}

# 
# @serverid
# 
stop() {
    $HQAPI_SH tcserver stop --serverid $1
}

#
# @groupname
#
stop_group() {
    $HQAPI_SH tcserver stop --groupname $1
}

#
# @appname
# @serverid
#
stop_application() {
    $HQAPI_SH tcsever stop-application --application $1 --serverid $2
}

# 
# @serverid
# 
start() {
    $HQAPI_SH tcserver start --serverid $1
}

#
# @groupname
#
start_group() {
    $HQAPI_SH tcserver start --groupname $1
}


#
# @appname
# @serverid
#
start_application() {
    $HQAPI_SH tcserver start-application --application $1 --serverid $2
}

# 
# @serverid
# 
restart() {
    $HQAPI_SH tcserver restart --serverid $1
}

#
# @groupname
#
restart_group() {
    $HQAPI_SH tcserver restart --groupname $1
}

# 
# @serverid
# @file
# @targetfile
# 
get_file() {
    $HQAPI_SH tcserver get-file --serverid $1 --file $2 --targetfile $3
}

# 
# @serverid
# @file
# @targetfile
put_file() {
    $HQAPI_SH tcserver put-file --serverid $1 --file $2 --targetfile $3
}

# 
# Deploy a local application 
# @serverid
# @localpath
# 
deploy_app_local() {
    $HQAPI_SH tcserver deploy-application --serverid $1 --localpath $2
}

# 
# Undeploy a local application 
# @serverid
# @application
# 
undeploy_app_local() {
    $HQAPI_SH tcserver undeploy-application --serverid $1 --application $2
}

# 
# Reload a local application 
# @serverid
# @application
# 
reload_app() {
    $HQAPI_SH tcserver reload-application --serverid $1 --application $2
}

# 
# Deploy a remote application 
# @serverid
# @remotepath
# 
deploy_app_remote() {
    $HQAPI_SH tcserver deploy-application --serverid $1 --remotepath $2
}

# 
# Undeploy a remote application 
# @serverid
# @application 
# 
undeploy_app_remote() {
    $HQAPI_SH tcserver undeploy-application --serverid $1 --application $2
}

# 
# Alter the name of a server
# @serverid
# @name
# 
modify_server() {
    $HQAPI_SH tcserver modify-server --serverid $1 --name $2
}

# 
# @serverid
# 
list_apps() {
    $HQAPI_SH tcserver list-applications --serverid $1
}

# 
#@group-name
# 
create_group() {
$HQAPI_SH tcserver create-group --name $1
}

# 
# @groupname
# @serverid
# 
add_server_to_group() {
    $HQAPI_SH tcserver add-server-to-group --groupname $1 --serverid $2
}

#
# @groupname
# serverid
#
remove_server_from_group() {
    $HQAPI_SH tcserver remove-server-from-group --groupname $1 --serverid $2
}

list_groups() {
    $HQAPI_SH tcserver list-groups
}

# 
# @name
# 
delete_group() {
    $HQAPI_SH tcserver delete-group --name $1
}

# 
# @serverid
# 
list_jvm_options() {
    $HQAPI_SH tcserver list-jvm-options --serverid $1
}

# 
# @serverid
# @options
# 
set_jvm_options() {
    $HQAPI_SH tcserver set-jvm-options --serverid $1 --options=$2
}

#
# @serverid
#
revert() {
    $HQAPI_SH tcserver revert-to-previous-configuration --serverid $1
}

log() {
    echo 
    echo "$1"
    echo
}

log_to_file() {
    echo "$1" >> test.log
}
