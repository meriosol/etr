@REM ---------------------------------------------------------
@REM -- Starts JavaDB (apache derby) CLI ij
@REM ---------------------------------------------------------
@echo off

setLocal
call _env
@echo To exit from derby CLI, enter quit;
%JAVA_HOME%\bin\java -cp %CLIENT_CPATH% %CLIENT_JAVA_OPTS% org.apache.derby.tools.ij

endLocal
