#!/bin/sh

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"


DB=eventium

echo "o Dropping DB '$DB'(don't forget to start server before).."
$PGSQL_HOME/bin/dropdb $DB
