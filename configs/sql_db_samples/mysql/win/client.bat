@REM ---------------------------------------------------------
@REM -- Starts MySQL CLI
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

@echo To exit from mysql CLI, enter \q

set USER=etracker
set PWD=shmetracker

"%MYSQL_HOME%\bin\mysql" -h localhost -p%PWD% -u %USER%

endLocal
