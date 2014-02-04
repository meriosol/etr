@REM ---------------------------------------------------------
@REM -- Set root password
@REM ---------------------------------------------------------
@echo off

setLocal

call _env
@echo o Setting root password

set MYSQL_ROOT_PWD=root-password
call "%MYSQL_HOME%\bin\mysqladmin" -u root password %MYSQL_ROOT_PWD%

endLocal
