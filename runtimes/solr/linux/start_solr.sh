#!/bin/sh
# ---------------------------------------------------------
# -- Loads data to solr
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

# TODO: Consider more production ready setup.


EXAMPLE_SOLR_DIR=$SOLR_HOME/example
START_JAR_NAME=start.jar
START_JAR_PATH=$EXAMPLE_SOLR_DIR/$START_JAR_NAME


echo "o About to start solr example dir"

JAVA_OPTS=-Dsolr.solr.home=$SOLR_ETR_DATA_HOME

cd $EXAMPLE_SOLR_DIR

$JAVA_HOME/bin/java $JAVA_OPTS -jar $START_JAR_NAME
