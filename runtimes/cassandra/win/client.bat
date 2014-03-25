@echo off
@rem Cassandra client shell launch:

@rem --=========
@rem [2014-03-24]

set USER=etracker
set PWD=shmetracker
"_cass_shell.bat" -u %USER% -p %PWD%
