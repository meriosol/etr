@REM ---------------------------------------------------------
@REM -- Starts JavaDB (apache derby) server
@REM ---------------------------------------------------------
@echo off

setLocal
call _env
@echo o NetworkServer is about to start. See derby.log for details..
call startNetworkServer
endLocal
