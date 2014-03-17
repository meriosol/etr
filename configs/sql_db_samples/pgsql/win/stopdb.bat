@REM ---------------------------------------------------------
@REM -- Stops PgSQL server
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
call "%PGSQL_HOME%\bin\pg_ctl" -D %PGDATA% -m fast stop

@echo o PgSQL Server maybe stopped.
endLocal

