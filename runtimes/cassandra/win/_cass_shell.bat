@echo off
@rem Cassandra shell. 
@rem NOTE: Don't forget to start server 1st. 

@rem --=========
@rem [2014-03-24]

@rem --=========
call _env.bat

@echo on
"%CASSANDRA_HOME%\bin\cqlsh.bat" %*
