@REM ---------------------------------------------------------
@REM -- Starts JavaDB (apache derby) server
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

@echo o NetworkServer is about to start. See derby.log for details.
%JAVA_HOME%\bin\java -cp %SERVER_CPATH% %SERVER_JAVA_OPTS% org.apache.derby.drda.NetworkServerControl start -h %DERBY_HOST% -p %DERBY_PORT%

endLocal
