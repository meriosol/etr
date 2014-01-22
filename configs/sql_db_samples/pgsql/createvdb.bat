@REM ---------------------------------------------------------
@REM -- Create PgSQL DB
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
set DB=eventium

@echo o Creating new DB '%DB%'(don't forget to start server before)..
call "%PGSQL_HOME%\bin\createdb" --encoding=UTF8 %DB%


endLocal

