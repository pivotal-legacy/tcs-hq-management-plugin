#!/bin/sh
#
# author: Bruce Snyder
# 
# A script to help copy and rename the license file for each dependency in the
# project. The directory of licenses can then be supplied to the legal dept
# for review. 
# 
# This script depends upon a successful build of the tcserver-plugin
# because it needs all dependencies listed in the ivy.xml files to be present
# in the ivy-cache/repository dir. 
# 

#
# The USER_DIR may need to be changed 
# to point to the appropriate home dir. 
# 
USER_DIR=$HOME

# 
# In what directory does the tcserver-plugin 
# source code live? 
# 
TCSERVER_PLUGIN_SRC_DIR=$USER_DIR/src/springsource/spring-management/tcserver-plugin/trunk
#TCSERVER_PLUGIN_SRC_DIR=$USER_DIR/src/springsource/spring-management/tcserver-plugin/branches/hqapi-integration 

#
# Temporary file to hold the dependencies list
# 
TMP_DEPS=/tmp/dependencies.txt 

# 
# A directory to hold all the license files 
# 
LICENSES_DIR_NAME=tcserver-plugin-licenses
LOCAL_BUILD_DIR=$TCSERVER_PLUGIN_SRC_DIR/build-tcserver-plugin
LOCAL_LICENSE_DIR_PATH=$LOCAL_BUILD_DIR/$LICENSES_DIR_NAME


#
# Locate all the dependencies across all modules by scanning the ivy.xml files
#
find_all_deps() {
    find $TCSERVER_PLUGIN_SRC_DIR -name ivy.xml | xargs grep '<dependency' > $TMP_DEPS
}

#
# Remove unncessary lines in the dependencies file. These artifacts are owned
# by SpringSource so we don't need to supply their licenses. 
#
remove_lines() {
    sed -i.bak -e '/name=\"com.hyperic.hq/d' $TMP_DEPS
    sed -i.bak -e '/name=\"com.springsource.ams/d' $TMP_DEPS
    sed -i.bak -e '/org=\"com.springsource.ams/d' $TMP_DEPS
    sed -i.bak -e '/name=\"com.springsource.hq/d' $TMP_DEPS
    sed -i.bak -e '/com.springsource.hq.plugin.tcserver.serverconfig.web/d' $TMP_DEPS
    sed -i.bak -e '/com.springsource.hq.plugin.tcserver.ui.tcserverclient/d' $TMP_DEPS
    sed -i.bak -e '/name=\"org.hyperic.hq/d' $TMP_DEPS
    sed -i.bak -e '/name=\"org.hyperic.hq/d' $TMP_DEPS
    sed -i.bak -e '/name=\"org.springframework/d' $TMP_DEPS
}

#
# Remove all the cruft from the dependencies file so it can be more easily
# processed. 
#
remove_cruft() {
    sed -i.bak -e 's/^.* org=/org=/g' $TMP_DEPS
    sed -i.bak -e 's/\" conf.*$//g' $TMP_DEPS
    sed -i.bak -e "s#org=\"#$TCSERVER_PLUGIN_SRC_DIR\/ivy-cache\/repository\/#g" $TMP_DEPS
    sed -i.bak -e 's#\" name=\"#\/#g' $TMP_DEPS
    sed -i.bak -e 's#\" rev=\"#\/#g' $TMP_DEPS
    sed -i.bak -e 's#$#\/license*.txt#' $TMP_DEPS
}

# 
# Copy and rename the license files so that they don't use the generic name
# with which they originate from the ivy-cache. 
# 
handle_licenses() {
    LICENSES_TMP_DIR_PATH=/tmp/$LICENSES_DIR_NAME

    if [ ! -d $LICENSES_TMP_DIR_PATH ] ; then 
        mkdir $LICENSES_TMP_DIR_PATH
    else 
        rm -rf $LICENSES_TMP_DIR_PATH
        mkdir $LICENSES_TMP_DIR_PATH
    fi 

    if [ -d $LOCAL_LICENSE_DIR_PATH ] ; then 
        rm -rf $LOCAL_LICENSE_DIR_PATH
    fi 

    for i in `cat $TMP_DEPS` 
    do 
        # Some munging of the path to grab the artifact name and version 
        LICENSE_PATH=$i
        LICENSE_PATH_TRIMMED=`echo $LICENSE_PATH | perl -pe 's#^.*repository\/##'`
        LICENSE_NAME=`echo $LICENSE_PATH_TRIMMED | perl -pe 's#^.*?\/(.*?)\/.*#\1#'`
        LICENSE_VERSION=`echo $LICENSE_PATH_TRIMMED | perl -pe 's#^.*?\/.*?\/(.*?)\/.*#\1#'`
        FULL_LICENSE_FILE_NAME=$LICENSE_NAME-$LICENSE_VERSION-license.txt
        cp $i $LICENSES_TMP_DIR_PATH/$FULL_LICENSE_FILE_NAME
    done 
}

# 
# Move the directory of licenses into the build dir for easy access. 
# 
move_licenses_dir() {
    mv $LICENSES_TMP_DIR_PATH $LOCAL_LICENSE_DIR_PATH

    echo 
    echo " All licenses have been copied to $LOCAL_LICENSE_DIR_PATH"
    echo " Zip up this directory, attach it to the JIRA issue and provide it to the legal department."
    echo 
}

cleanup() {
    rm $TMP_DEPS*
}

main() {
    find_all_deps
    remove_lines
    remove_cruft
    handle_licenses
    move_licenses_dir
    cleanup
}

main
