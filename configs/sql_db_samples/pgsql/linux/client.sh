#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

echo "To exit from CLI, enter \q"

DB=eventium
USER=etracker
PWD=shmetracker

echo "PgSQL CLI session for user $USER ($PWD)"
$PGSQL_HOME/bin/psql -d $DB -U $USER -W

