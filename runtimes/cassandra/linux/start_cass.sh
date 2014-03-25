#!/bin/sh
# ---------------------------------------------------------
# -- Starts Cassandra server
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

# TODO: Consider more production ready setup.
# NOTE: you'll likely need sudo.

$CASSANDRA_HOME/bin/cassandra "$@"

