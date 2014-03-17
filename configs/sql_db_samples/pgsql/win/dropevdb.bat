@REM ---------------------------------------------------------
@REM -- Drop PgSQL DB
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
set DB=eventium

@echo o Dropping DB '%DB%'(don't forget to start server before)..
call  "%PGSQL_HOME%\bin\dropdb" %DB%

endLocal

