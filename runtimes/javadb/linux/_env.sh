#!/bin/sh
#set -x

JAVA_HOME=/usr/lib/jvm/java-7-oracle
if [ ! -d "$JAVA_HOME" ]; then
	echo 1>&2 "env init - CAUTION: JAVA_HOME='$JAVA_HOME' not found, please set valid directory."
	exit 1
fi

DERBY_HOME=$JAVA_HOME/db
DERBY_LIB=$DERBY_HOME/lib

DERBY_HOST=localhost
DERBY_PORT=1527

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

BASE_SECURITY_OPTS="-Djava.security.manager -Djava.security.policy=$SCRIPT_DIR/conf"

DERBY_OPTS="-Dderby.install.url=file:$DERBY_LIB/ -Dderby.host=$DERBY_HOST -Dderby.port=$DERBY_PORT"

SERVER_JAVA_OPTS="$BASE_SECURITY_OPTS/server.policy $DERBY_OPTS"
CLIENT_JAVA_OPTS="$BASE_SECURITY_OPTS/client.policy $DERBY_OPTS"

echo " o (_env) Inited env:"
echo " o (_env)  JAVA_HOME = $JAVA_HOME"
echo " o (_env)  DERBY_HOME = $DERBY_HOME"

SERVER_CPATH="$DERBY_LIB/derbynet.jar"
CLIENT_CPATH="$DERBY_LIB/derbyclient.jar:$DERBY_LIB/derbytools.jar"

export JAVA_HOME DERBY_HOST DERBY_PORT SERVER_JAVA_OPTS CLIENT_JAVA_OPTS SERVER_CPATH CLIENT_CPATH
