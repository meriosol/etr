#!/bin/sh
# ---------------------------------------------------------
# -- Loads data to solr
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

# TODO: Consider more production ready setup.

$CASSANDRA_HOME/bin/cqlsh "$@"
