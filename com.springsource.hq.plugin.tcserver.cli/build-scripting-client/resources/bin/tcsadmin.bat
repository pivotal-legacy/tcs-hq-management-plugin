@echo off
rem ---------------------------------------------------------------------------
rem tc Server Admin Command Line Client
rem
rem Copyright (C) 2009-2015  Pivotal Software, Inc
rem
rem This program is is free software; you can redistribute it and/or modify
rem it under the terms version 2 of the GNU General Public License as
rem published by the Free Software Foundation.
rem
rem This program is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU General Public License for more details.
rem
rem You should have received a copy of the GNU General Public License
rem along with this program; if not, write to the Free Software
rem Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
rem ---------------------------------------------------------------------------
rem Store current directory
set CURRENTDIR=%CD%
rem Change working directory to root
cd /d %~dp0..

rem  Dynamically build classpath
set JARS=
set CLASSPATH=
for %%i in (lib\*.jar) do call bin\cpappend.bat %%i
for %%i in (dist\*.jar) do call bin\cpappend.bat %%i
set CLASSPATH=%JARS%;config

rem Execute command line client
if "%JAVA_HOME%" == "" (
  java -cp %CLASSPATH% com.springsource.hq.plugin.tcserver.cli.commandline.Bootstrap %*
) else (
  "%JAVA_HOME%"\bin\java -cp %CLASSPATH% com.springsource.hq.plugin.tcserver.cli.commandline.Bootstrap %*
)
set RETURNCODE=%ERRORLEVEL%

rem Return to initial directory
cd /d %CURRENTDIR%
rem Exit with java return code
exit /b %RETURNCODE%
