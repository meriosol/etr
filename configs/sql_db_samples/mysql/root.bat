@REM ---------------------------------------------------------
@REM -- Starts MySQL CLI for root
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

@echo To exit from mysql CLI, enter \q

"%MYSQL_HOME%\bin\mysql" -h localhost -p -u root

endLocal
