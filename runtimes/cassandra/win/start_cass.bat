@echo off
@rem Cassandra server

@rem --=========
@rem [2014-03-24]
 
@rem --=========
call _env.bat

@rem --=========
@rem Start server:
"%CASSANDRA_HOME%\bin\cassandra.bat"
