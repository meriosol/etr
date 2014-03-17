#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"


echo "To exit from PgSQL CLI, enter \q"
DB=eventium


echo "PgSQL CLI session for OS user (configured as a root)"
$PGSQL_HOME/bin/psql $DB
