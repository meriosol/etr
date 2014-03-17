@echo off

if "%OS%" == "Windows_NT" setlocal

call _env.bat

set EXAMPLE_SOLR_DIR=%SOLR_HOME%/example
set START_JAR_NAME=start.jar
set START_JAR_PATH=%EXAMPLE_SOLR_DIR%/%START_JAR_NAME%


@echo o About to start solr example dir

set JAVA_OPTS=-Dsolr.solr.home=%SOLR_ETR_DATA_HOME%

cd %EXAMPLE_SOLR_DIR%

java %JAVA_OPTS% -jar %START_JAR_NAME%

@rem endLocal
