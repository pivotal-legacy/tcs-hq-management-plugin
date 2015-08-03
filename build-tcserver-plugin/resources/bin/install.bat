@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

IF "%OS%" == "Windows_NT" SETLOCAL

if [%1] == [] (
	ECHO Usage: install.bat ^<hq-server-home^>
	EXIT /B 1
)

SET SCRIPT_DIR=%~dp0
SET SERVER_HOME=%~f1
SET TARGET_DIR=%SERVER_HOME%\hq-engine\hq-server\webapps

ECHO Uninstalling tc Server HQ Plugin from %SERVER_HOME%
SET CANDIDATES=^
 ROOT\hqu\tcserverclient\^
 ROOT\hqu\tomcatappmgmt\^
 ROOT\hqu\tomcatserverconfig\^
 ROOT\ui_docs\DOC\styles\site.css^
 ROOT\ui_docs\DOC\ui-tcserver.ApplicationManagement.html^
 ROOT\ui_docs\DOC\ui-tcserver.ServerConfiguration.html^
 ROOT\ui_docs\DOC\ui-tcserver.ServerConfigurationRef.html^
 ROOT\ui_docs\DOC\ui-tcserver.ServerJDBCRef.html^
 ROOT\ui_docs\DOC\ui-tcserver.ServerServicesRef.html^
 ROOT\WEB-INF\hq-plugins\springsource-tcserver-plugin.jar^
 tomcatserverconfig.war

FOR %%F in (%CANDIDATES%) DO (
	SET CANDIDATE=%TARGET_DIR%\%%F
	IF EXIST !CANDIDATE! (
		DEL /S /Q "!CANDIDATE!" > NUL
	)
)

ECHO Installing tc Server HQ Plugin to %SERVER_HOME%
SET DIST_DIR="%SCRIPT_DIR%..\dist"
XCOPY /S /I /Q %DIST_DIR% "%TARGET_DIR%" > NUL

ECHO The Hyperic Server may require restarting to detect the plugin update
