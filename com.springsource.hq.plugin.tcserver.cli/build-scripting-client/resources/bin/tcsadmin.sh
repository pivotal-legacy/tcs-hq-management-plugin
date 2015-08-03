#!/bin/sh

# ---------------------------------------------------------------------------
# tc Server Admin Command Line Client
#
# Copyright (C) 2009-2015  Pivotal Software, Inc
#
# This program is is free software; you can redistribute it and/or modify
# it under the terms version 2 of the GNU General Public License as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
# ---------------------------------------------------------------------------

cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

PRG="$0"

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
RUNDIR=`dirname "$PRG"`

#Absolute path
RUNDIR=`cd "$RUNDIR/.." ; pwd`

if $cygwin; then
  [ -n "$RUNDIR" ] && RUNDIR=`cygpath --path --unix "$RUNDIR"`
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --unix "$JAVA_HOME"`
fi

#CLASSPATH=$CLASSPATH$(find "$RUNDIR" -name "*.jar" -exec printf :{} ';'):$RUNDIR/config
CLASSPATH=$CLASSPATH:$RUNDIR/config:

for i in `ls $RUNDIR/dist/*.jar`; do
    CLASSPATH=$CLASSPATH:$i
done

for i in `ls $RUNDIR/lib/*.jar`; do
    CLASSPATH=$CLASSPATH:$i
done

if [ "x" = "x$JAVA_HOME" ] ; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

if $cygwin; then
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
fi

cd "$RUNDIR"
exec "$JAVA" -cp "$CLASSPATH" com.springsource.hq.plugin.tcserver.cli.commandline.Bootstrap "$@"

exit $?
