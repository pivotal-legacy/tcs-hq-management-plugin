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
#   TCS_USER_DIR - root directory 
#   TCS_SCRIPT_DIR - path to dev-tools scripts
#   TCS_APP_DIR  - path where hq is installed
# 

########## CHANGE ME ##########
SERVER_ID=${TCS_SERVER_ID:-10696}

#
# The USER_DIR may need to be changed 
# to point to the appropriate home dir. 
# 
USER_DIR=${TCS_USER_DIR:-$HOME}
SCRIPT_DIR=${TCS_SCRIPT_DIR:-`dirname $0`}

# 
# In what directory does the hqapi runtime distribution live? 
# 
HQAPI_DIR=${TCS_APP_DIR:-$USER_DIR/hq}/hqapi1-3.0}

# 
# Include the test-hqapi-functions.sh script 
# 
init() {
    . $SCRIPT_DIR/test-hqapi-functions.sh
}


# 
# Perform a start, a stop and a restart of tc Server.
# 
start_stop_and_restart() {
    # TODO Verify each step 
    start $SERVER_ID
    stop $SERVER_ID
    restart $SERVER_ID
}

put_file_and_get_file() {
    DEMO_WAR_FILE_START=$START_DIR/demo.war
    DEMO_WAR_FILE_END=$END_DIR/demo.war
    REMOTE_DEMO_WAR_FILE=webapps/demo.war
    MD5_SUM_START=`md5 $DEMO_WAR_FILE_START | awk {'print $4'}`
    put_file $SERVER_ID $DEMO_WAR_FILE_START $REMOTE_DEMO_WAR_FILE
    get_file $SERVER_ID $REMOTE_DEMO_WAR_FILE $DEMO_WAR_FILE_END
    MD5_SUM_END=`md5 $DEMO_WAR_FILE_START | awk {'print $4'}`

    if [ $MD5_SUM_START = $MD5_SUM_END ] ; then 
        log "SUCCESS: put_file_and_get_file" 
    else
        log "FAILURE: put_file_and_get_file" 
    fi
}

get_non_existent_file() {
    # TODO Verify this function 
    get_file $SERVER_ID foobar.txt /tmp/foobar.txt 
}

#
# Deploy a local application, reload it, then undeploy it
#
deploy_local_app_and_reload_and_undeploy_app() {
    # TODO Verify each step 
    EXAMPLES_WAR_FILE=$START_DIR/examples.war
    deploy_app_local $SERVER_ID $EXAMPLES_WAR_FILE
    reload_app $SERVER_ID examples
    undeploy_app_local $SERVER_ID examples
}

# 
# Deploy a remote application, and then undeploy it
#  
deploy_remote_app_and_undeploy_app() {
    # TODO Verify each step 
    EXAMPLES_WAR_FILE=$START_DIR/examples.war
    deploy_app_remote $SERVER_ID $EXAMPLES_WAR_FILE
    undeploy_app_remote $SERVER_ID examples
}

# 
# Alter the name of a server
# 
alter_server_name() {
    SERVER_NAME="Test Server"
    modify_server $SERVER_ID $SERVER_NAME
    # TODO Verify the name change 
    list_servers $SERVER_ID
}


#
# List applications, stop them, then start them.
# 
list_apps_stop_app_start_app() {
    # TODO Verify app stops and starts 
    APP_NAME=ROOT
    list_apps $SERVER_ID
    stop_application $APP_NAME $SERVER_ID
    stop_application $APP_NAME $SERVER_ID
    start_application $APP_NAME $SERVER_ID
    start_application $APP_NAME $SERVER_ID
}

#
# Test out the group functionality by creating a group, adding a server to it, stopping/staring/restarting,
# then remove the server from the group and delete the group
# 
test_group_functions() {
    # TODO Verify each step 
    GROUP_NAME=a-test-group
    create_group $GROUP_NAME
    add_server_to_group $GROUP_NAME $SERVER_ID
    list_groups
    stop_group $GROUP_NAME
    start_group $GROUP_NAME
    restart_group $GROUP_NAME
    remove_server_from_group $GROUP_NAME $SERVER_ID
    delete_group $GROUP_NAME
}

# 
# Test out the JVM-altering configuration options.
# 
test_jvm_options() {
    # TODO Verify each step 
    OPTIONS=-Dtest=true,-Dtest2=false
    list_jvm_options $SERVER_ID
    set_jvm_options $SERVER_ID $OPTIONS
    list_jvm_options $SERVER_ID
    set_jvm_options $SERVER_ID $OPTIONS
    list_jvm_options $SERVER_ID
}

# 
# These are the actual tests. They utilize the functions in the
# test-hqapi-functions.sh script to all functions that model various scenarios for
# using the tcserver-plugin CLI. 
# 
do_tests() {
    # TODO Verify this function 
    show_help

    # List the existing tcServer instances
    # TODO Verify this function 
    list_servers

    start_stop_and_restart

    put_file_and_get_file

    get_non_existent_file

    deploy_local_app_and_reload_and_undeploy_app

    deploy_remote_app_and_undeploy_app

    alter_server_name

    list_apps_stop_app_start_app

    test_group_functions

    test_jvm_options

    revert $SERVER_ID
}


main() {
    init 
    do_tests
}

main $@
