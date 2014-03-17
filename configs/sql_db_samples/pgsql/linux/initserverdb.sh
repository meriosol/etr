#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

DB=eventium

echo "o Init PgSQL server.."
$PGSQL_HOME/bin/initdb --no-locale -E UTF-8 -D $PGDATA
