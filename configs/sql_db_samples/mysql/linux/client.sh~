#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

echo "To exit from mysql CLI, enter \q"

USER=etracker
PWD=shmetracker

$MYSQL_CMD -h localhost -p$PWD -u $USER

