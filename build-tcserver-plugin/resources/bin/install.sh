#!/bin/bash

if [[ $# -ne 1 ]]; then
	echo "Usage: install.sh <hq-server-home>"
	exit 1;
fi

SCRIPT_DIR=`cd \`dirname ${0}\` ; pwd -P`
SERVER_HOME="$1"

echo "Uninstalling tc Server HQ Plugin from '$SERVER_HOME'"

CANDIDATES=" \
	ROOT/hqu/tcserverclient/ \
	ROOT/hqu/tomcatappmgmt/ \
	ROOT/hqu/tomcatserverconfig/ \
	ROOT/ui_docs/DOC/styles/site.css \
	ROOT/ui_docs/DOC/ui-tcserver.ApplicationManagement.html \
	ROOT/ui_docs/DOC/ui-tcserver.ServerConfiguration.html \
	ROOT/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html \
	ROOT/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html \
	ROOT/ui_docs/DOC/ui-tcserver.ServerServicesRef.html \
	ROOT/WEB-INF/hq-plugins/springsource-tcserver-plugin.jar \
	tomcatserverconfig.war"

for FILE in $CANDIDATES ; do
	CANDIDATE="$1/hq-engine/hq-server/webapps/$FILE"
	if [[ -e $CANDIDATE ]]; then
		rm -r "$CANDIDATE"
	fi
done

sleep 5

echo "Installing tc Server HQ Plugin to '$SERVER_HOME'"
cp -R "$SCRIPT_DIR/../dist/." "$SERVER_HOME/hq-engine/hq-server/webapps"
