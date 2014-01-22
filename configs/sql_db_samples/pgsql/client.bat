@REM ---------------------------------------------------------
@REM -- Starts PgSQL CLI
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

@echo To exit from CLI, enter \q

set DB=eventium
set USER=etracker
set PWD=shmetracker

@echo PgSQL CLI session for user %USER% (%PWD%)
call "%PGSQL_HOME%\bin\psql" -d %DB% -U %USER% -W


endLocal
