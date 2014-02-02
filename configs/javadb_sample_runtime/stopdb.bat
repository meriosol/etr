@REM ---------------------------------------------------------
@REM -- Stops JavaDB (apache derby) server
@REM ---------------------------------------------------------
@echo off

setLocal
call _env

%JAVA_HOME%\bin\java -cp %SERVER_CPATH% %SERVER_JAVA_OPTS% org.apache.derby.drda.NetworkServerControl shutdown -h %DERBY_HOST% -p %DERBY_PORT%
@echo o NetworkServer maybe stopped. See derby.log for details.

endLocal
