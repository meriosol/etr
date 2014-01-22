@REM ---------------------------------------------------------
@REM -- Starts PgSQL server
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
set LOG_PATH=/tmp/logs/pg_logfile.txt

@echo o PgSQL Server is about to start. See logs in '%LOG_PATH%'..
call "%PGSQL_HOME%\bin\pg_ctl" -D %PGDATA% -l %LOG_PATH% start

endLocal

