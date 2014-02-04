@REM ---------------------------------------------------------
@REM -- Starts MySQL server
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
@echo o MySQL Server is about to start..

call "%MYSQL_HOME%\bin\mysqld"
@rem "%MYSQL_HOME%\bin\mysqld" --console

endLocal
