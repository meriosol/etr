@REM ---------------------------------------------------------
@REM -- Inits PqSQL server
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
@echo o Init PgSQL server..
call "%PGSQL_HOME%\bin\initdb" --no-locale -E UTF-8 -D %PGDATA%

endLocal
