#!/bin/sh

JAVA_HOME=/usr/lib/jvm/java-7-oracle
if [ ! -d "$JAVA_HOME" ]; then
	echo 1>&2 "env init - CAUTION: JAVA_HOME='$JAVA_HOME' not found, please set valid directory."
	exit 1
fi

# NOTE: Values are provided as samples. Amend these settings if needed. 

# Solr home dir: 
SOLR_HOME=enter_path_to_solr/solr-4.7.0


# ETR solr cores home dir: 
SOLR_ETR_DATA_HOME=enter_path_to_solr_db_dir
# NOTE: SOLR_ETR_DATA_HOME points to folder where eventium was copied from configs/no_sql_db_samples/solr/ddl

echo " o (_env) Inited env:"
echo " o (_env)  JAVA_HOME = $JAVA_HOME"
echo " o (_env)  SOLR_HOME = $SOLR_HOME"
echo " o (_env)  SOLR_ETR_DATA_HOME = $SOLR_ETR_DATA_HOME"

export JAVA_HOME SOLR_HOME SOLR_ETR_DATA_HOME
