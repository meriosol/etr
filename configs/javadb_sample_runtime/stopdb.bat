@REM ---------------------------------------------------------
@REM -- Stops JavaDB (apache derby) server
@REM ---------------------------------------------------------
@echo off

setLocal
call _env
call stopNetworkServer

@echo o NetworkServer maybe stopped. See derby.log for details.
endLocal
