@echo off

@rem NOTE: start solr server before loading data, make sure cores are in good shape and happy to accept new data.
if "%OS%" == "Windows_NT" setlocal

call _env.bat

set EXAMPLE_DOCS_SOLR_DIR=%SOLR_HOME%/example/exampledocs
set POST_JAR_NAME=post.jar

@ rem DML files:
set DML_DIR=F:\alkr\configs\for_event_tracker\solr\dml
set EVENT_CATEGORIES_DATA_PATH=%DML_DIR%\add_event_categories.xml
set EVENT_DATA_PATH=%DML_DIR%\add_events.xml

@rem Core names:
set EVENT_CATEGORIES_CORE=event_categories
set EVENTS_CORE=events

@rem Base UPDATE URL parts:
set URL_PREFIX=http://localhost:8983/solr
set URL_SUFFIX=update?commit=true&wt=xml
@rem NOTE: UPDATE URL is: %URL_PREFIX%/<CORE_NAME>/%URL_SUFFIX% 


@echo o About to load data to ETR solr cores..

set JAVA_OPTS=-Ddata=files 

cd %EXAMPLE_DOCS_SOLR_DIR%


@echo o Loading data to %EVENT_CATEGORIES_CORE% core..
java -Ddata=files -Durl=%URL_PREFIX%/%EVENT_CATEGORIES_CORE%/%URL_SUFFIX% -jar %POST_JAR_NAME% %EVENT_CATEGORIES_DATA_PATH%

@echo o Loading data to %EVENTS_CORE% core..
java -Ddata=files -Durl=%URL_PREFIX%/%EVENTS_CORE%/%URL_SUFFIX% -jar %POST_JAR_NAME% %EVENT_DATA_PATH%

@rem endLocal
