#!/bin/sh
# ---------------------------------------------------------
# -- Starts JavaDB (apache derby) CLI ij
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

echo " o (clij) Env:"
echo " o (clij)  JAVA_HOME = $JAVA_HOME"
echo " o (clij)  CLIENT_CPATH = $CLIENT_CPATH"
echo " o (clij)  CLIENT_JAVA_OPTS = $CLIENT_JAVA_OPTS"

echo "To exit from derby CLI, enter quit;"
$JAVA_HOME/bin/java -cp "$CLIENT_CPATH" $CLIENT_JAVA_OPTS org.apache.derby.tools.ij



