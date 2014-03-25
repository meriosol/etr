@echo off
@rem Cassandra root shell launch:

@rem --=========
@rem [2014-03-24]

@rem --=========
@rem Superuser:
set USER=cassandra
set PWD=cassandra
"_cass_shell.bat" -u %USER% -p %PWD%
