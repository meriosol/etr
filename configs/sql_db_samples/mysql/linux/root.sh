#!/bin/sh
# ---------------------------------------------------------
# -- Starts MySQL CLI for root
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

echo "To exit from mysql CLI, enter \q"
$MYSQL_CMD -h localhost -u root -p
