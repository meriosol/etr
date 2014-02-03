#!/bin/sh
# ---------------------------------------------------------
# -- Starts JavaDB (apache derby) server
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

echo " o (clij) Env:"
echo " o (clij)  JAVA_HOME = $JAVA_HOME"
echo " o (clij)  SERVER_CPATH = $SERVER_CPATH"
echo " o (clij)  SERVER_JAVA_OPTS = $SERVER_JAVA_OPTS"

$JAVA_HOME/bin/java -cp "$SERVER_CPATH" $SERVER_JAVA_OPTS org.apache.derby.drda.NetworkServerControl shutdown -h $DERBY_HOST -p $DERBY_PORT
echo "o NetworkServer maybe stopped. See derby.log for details."

