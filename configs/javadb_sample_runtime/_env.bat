@REM ---------------------------------------------------------
@REM -- Inits env-t variables
@REM ---------------------------------------------------------
@echo off

set JAVA_HOME=C:\\Java\jdk1.7

set DERBY_HOME=%JAVA_HOME%\db
set DERBY_LIB=%DERBY_HOME%\lib
SET DERBY_HOST=localhost
SET DERBY_PORT=1527

set SCRIPT_DIR=%~dp0%
@rem set SCRIPT_DIR=%CD%

set BASE_SECURITY_OPTS=-Djava.security.manager -Djava.security.policy=%SCRIPT_DIR%\conf

@rem NOTE: if you need more perm-s, consider adding these params in OPTS:
@rem set DERBY_OPTS=-Dderby.install.url=%DERBY_HOME%\lib\ -Dderby.system.home=%SCRIPT_DIR% -Dderby.drda.traceDirectory=%SCRIPT_DIR%
set DERBY_OPTS=-Dderby.install.url=file:%DERBY_LIB%\

set SERVER_JAVA_OPTS=%BASE_SECURITY_OPTS%\server.policy %DERBY_OPTS%
set CLIENT_JAVA_OPTS=%BASE_SECURITY_OPTS%\client.policy %DERBY_OPTS%

@echo o (_env) Inited env:
@echo o (_env)  JAVA_HOME = %JAVA_HOME%
@echo o (_env)  DERBY_HOME = %DERBY_HOME%

@rem set CPATH=%DERBY_HOME%\lib\derbynet.jar;%DERBY_HOME%\lib\derbytools.jar

set SERVER_CPATH=%DERBY_LIB%\derbynet.jar
set CLIENT_CPATH=%DERBY_LIB%\derbyclient.jar;%DERBY_LIB%\derbytools.jar
