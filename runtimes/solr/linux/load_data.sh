#!/bin/sh
# ---------------------------------------------------------
# -- Loads data to solr
# ---------------------------------------------------------

# Script dir:
SCRIPT_DIR=$( cd $(dirname $0) ; pwd -P )

. "$SCRIPT_DIR/_env.sh"

# TODO: Consider more production ready setup.

EXAMPLE_DOCS_SOLR_DIR=$SOLR_HOME/example/exampledocs
POST_JAR_NAME=post.jar

# DML files:
DML_DIR=$SCRIPT_DIR/../../../../configs/no_sql_db_samples/solr/dml
EVENT_CATEGORIES_DATA_PATH=$DML_DIR/add_event_categories.xml
EVENT_DATA_PATH=$DML_DIR/add_events.xml

# Core names:
EVENT_CATEGORIES_CORE=event_categories
EVENTS_CORE=events

# Base UPDATE URL parts (assuming port remained default):
URL_PREFIX=http://localhost:8983/solr
URL_SUFFIX=update?commit=true&wt=xml
# NOTE: UPDATE URL is: $URL_PREFIX/<CORE_NAME>/$URL_SUFFIX 


echo "o About to load data to ETR solr cores.."

JAVA_OPTS=-Ddata=files 

cd $EXAMPLE_DOCS_SOLR_DIR


echo "o Loading data to $EVENT_CATEGORIES_CORE core.."
$JAVA_HOME/bin/java -Ddata=files -Durl=$URL_PREFIX/$EVENT_CATEGORIES_CORE/$URL_SUFFIX -jar $POST_JAR_NAME $EVENT_CATEGORIES_DATA_PATH

echo "o Loading data to $EVENTS_CORE core.."
$JAVA_HOME/bin/java -Ddata=files -Durl=$URL_PREFIX/$EVENTS_CORE/$URL_SUFFIX -jar $POST_JAR_NAME $EVENT_DATA_PATH

