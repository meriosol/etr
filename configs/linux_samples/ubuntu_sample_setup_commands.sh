#-------
# Ubuntu (12 LTS) env setup sample.
#  NOTE: File has sh extention only for syntax highlight in editors.
#  WARN: Commands below are more like hints. Your env likely has specific requirements, so apply them with due care. 
# Inception - [2014-02-02]
#-----

#-----
# Performance monitor(useful tool to monitor system health):
sudo apt-get install indicator-multiload
# To open go to Dash, find 'multi'
#-----

#-----
# Colrorful VI editor vim:
sudo apt-get install vim

#-----
# JDK 1.7:
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get updatee
sudo apt-get install oracle-java7-installer

JAVA_HOME=/usr/lib/jvm/java-7-oracle ; export JAVA_HOME
${JAVA_HOME}/bin/java -version
# java version "1.7.0_51"
# Java(TM) SE Runtime Environment (build 1.7.0_51-b13)
# Java HotSpot(TM) Client VM (build 24.51-b03, mixed mode)
# TODO: Java8 came out. Let's check it later..
#-----

#-----
# Idea IDE (CE is free):
#  - A. Load archive from intellij site and unarchive it. 
#  - B. If you find IDEA in software center, install via it.
# To start idea, try:
IDEA_HOME=/opt/intellij-idea-ce ; export IDEA_HOME
${IDEA_HOME}/bin/idea.sh 


#-----
# Apache Maven:
#  Copy and unarchive distrib in local folder.
#  Sample:
MAVEN_HOME=/home/alen/progs/java/apache-maven-3.1.1 ; export $MAVEN_HOME
$MAVEN_HOME/bin/mvn

#-----
# Setup git:
sudo apt-get install git

#-----
# Setup Mysql:
# If to setup service, run:
sudo apt-get install mysql-server

#-----
# SSHkeys (for git)
#  Ceate them on github and copy them to ~/.ssh

#-----

#-----
# If you want SSH access:
sudo apt-get install openssh-server 

# sshd restart:
sudo /etc/init.d/ssh restart

# Check port:
netstat -en | grep 22

#-----

#-----
# Postgres:
sudo apt-get install postgresql postgresql-contrib

#  Bins: 
ls -la /usr/lib/postgresql/9.1/bin/

# Stop/start service:
sudo /etc/init.d/postgresql stop
sudo /etc/init.d/postgresql start
#  - or(maybe better without sudo, but...):
sudo service postgresql stop
sudo service postgresql start

# Check config:
cd /etc/postgresql/9.1/main
less postgresql.conf

# Set trust temp-ry in pg_hba.conf:
PGSQL_CFG=/etc/postgresql/9.1/main
sudo vim $PGSQL_CFG/pg_hba.conf
#... Now set trust:
local   all             postgres                           trust
local   all             all                                trust
#...

# Sample of how to create DB for ETR:
# Set postgres password:
sudo passwd postgres

# Get postgres shell(well, maybe hacking way):
su - postgres
PGSQL_HOME=/usr/lib/postgresql/9.1
DB=eventium
$PGSQL_HOME/bin/createdb --encoding=UTF8 $DB

# Enter ss pg root and create etracker:
$PGSQL_HOME/bin/psql $DB
CREATE USER etracker PASSWORD 'shmetracker';
REVOKE ALL PRIVILEGES ON SCHEMA PUBLIC FROM etracker;


# Set peer and md5 in pg_hba.conf:
PGSQL_CFG=/etc/postgresql/9.1/main
sudo vim $PGSQL_CFG/pg_hba.conf
#...
local   all             postgres                           peer
local   all             all                                md5
#...

# As etracker perform DDL/DML:
PGSQL_HOME=/usr/lib/postgresql/9.1
DB=eventium
USER=etracker
$PGSQL_HOME/bin/psql -d $DB -U $USER -W
# Perform sql commands as seen e.g. in pgsql/sql

#-----

#-----
# Solr:
#  Copy and unarchive solr distr e.g. to this folder:
ls ~/progs/java/solr-4.7.0

cd your_path_to_etr/etr/runtimes/solr/linux
chmod 700 *.sh

# Amend _env.sh with values relevant in your system:

# Start server:
./start_solr.sh

# Load data:
./load_data.sh

# Check cores health from browser: http://localhost:8983/solr
#-----


#-----
# Cassandra:
#  Copy and unrachive distr e.g. in this folder:
CASSANDRA_HOME=~/progs/java/apache-cassandra-2.0.4
chmod 700 $CASSANDRA_HOME/bin/*

# Secure it:
cd $CASSANDRA_HOME/conf
cp cassandra.yaml cassandra.yaml.back1
vim cassandra.yaml
 
#....Now set these values:
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer
#....

# Start server:
cd your_path_to_etr/etr/runtimes/cassandra/linux
chmod 700 *.sh
sudo ./start_cass.sh
# TODO: avoid using sudo.

# In case it stalled:
ps -ef | grep cassandra
sudo kill -9 <CASS_PROCESS_ID>

# Start client as a root:
cd $CASSANDRA_HOME/bin 
./cqlsh -u cassandra -p cassandra

# Perform DDL/DML commands as you see in e.g. 
# your_path_to_etr/etr/configs/no_sql_db_samples/cassandra/cql
#  - create_db.sql, create_user.sql 
# Exit from root client

# Start client as etracker:
cd  your_path_to_etr/etr/runtimes/cassandra/linux
./client.sh -u etracker -p shmetracker
# - Or:
cd $CASSANDRA_HOME/bin 
./cqlsh -u etracker -p shmetracker

# Perform remaining DDL/DML commands as you see in e.g. 
# your_path_to_etr/etr/configs/no_sql_db_samples/cassandra/cql
#  - create_tables.sql, import_test_data.sql
# Comamnds from cass_eventium_sql_cmds.sql can help in checking how things went.
#-----

#-----
# Check installed pkgs:
dpkg --get-selections | grep -v deinstall > ~/Documents/packages.txt
dpkg-query -W -f='${PackageSpec} ${Status}\n' | grep installed |  sort -u | cut -f1 -d \ > ~/Documents/installed-pkgs.txt
less /var/lib/apt/extended_states
