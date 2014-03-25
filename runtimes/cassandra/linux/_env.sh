#!/bin/sh

JAVA_HOME=/usr/lib/jvm/java-7-oracle
if [ ! -d "$JAVA_HOME" ]; then
	echo 1>&2 "env init - CAUTION: JAVA_HOME='$JAVA_HOME' not found, please set valid directory."
	exit 1
fi

# NOTE: Values are provided as samples. Amend these settings if needed. 

# Cassandra home dir: 
CASSANDRA_HOME=~/progs/java/apache-cassandra-2.0.4


echo " o (_env) Inited env:"
echo " o (_env)  JAVA_HOME = $JAVA_HOME"
echo " o (_env)  CASSANDRA_HOME = $CASSANDRA_HOME"

export JAVA_HOME CASSANDRA_HOME
