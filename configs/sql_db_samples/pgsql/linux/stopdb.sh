#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"


$PGSQL_HOME/bin/pg_ctl -D $PGDATA -m fast stop
echo "o PgSQL Server maybe stopped."
