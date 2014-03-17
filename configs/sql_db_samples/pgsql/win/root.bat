@REM ---------------------------------------------------------
@REM -- Starts PgSQL CLI for root
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

@echo To exit from PgSQL CLI, enter \q

set DB=eventium

@echo PgSQL CLI session for OS user (configured as a root)
"%PGSQL_HOME%\bin\psql" %DB%

endLocal
