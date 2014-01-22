@REM ---------------------------------------------------------
@REM -- Inits env-t variables
@REM ---------------------------------------------------------
@echo off

set JAVA_HOME=C:/Java/jdk1.7

set DERBY_HOME=%JAVA_HOME%/db

@echo o (_env) Inited env:
@echo o (_env)  JAVA_HOME = %JAVA_HOME%
@echo o (_env)  DERBY_HOME = %DERBY_HOME%

set PATH=%PATH%
set PATH=%DERBY_HOME%/bin;%JAVA_HOME%/bin;%PATH%
