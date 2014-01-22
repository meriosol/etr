@REM ---------------------------------------------------------
@REM -- Stops MySQL server
@REM ---------------------------------------------------------
@echo off

setLocal
call _env
call "%MYSQL_HOME%\bin\mysqladmin" -u root -p shutdown
@echo o MySQL Server maybe stopped.
endLocal

