Title: SQL DB samples for eventium database
Inception: 2014-01-15

This is snapshot of DDL/DML commands for SQL DBs DAOs will deal with.
Before copy these scripts here they were tested for: 
 - Java 7 (45) hotspot, internal java DB (derby) v 10.8 
 - MySQL (mysql-5.6.14-winx64)
 - Postgres (postgresql-9.3.2-1-windows-x64)

Folders:
  javadb - Derby commands
  mysql - MySQL commands
  pgsql - PgSQL commands

Notes:  
  NOTE: If you have only Java 7 setup, it will be enough to test all JDBC code because derby is embedded right there.
 
  NOTE: Intention was not to automate everything on this side(it's anyways it performed very rarely)
      , but to provide guidelines how DBs and tables can be created.
      Files readme.txt contain start info. The most interest files are in sql folders.
	 
	 
TODO: 
 1. For now win7 cmd line is embraced. *nix will be added later.
