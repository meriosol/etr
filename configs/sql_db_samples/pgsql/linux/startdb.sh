#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

LOG_PATH=/tmp/logs/pg_logfile.txt

echo "o PgSQL Server is about to start. See logs in '$LOG_PATH'.."
$PGSQL_HOME/bin/pg_ctl -D $PGDATA -l $LOG_PATH start
