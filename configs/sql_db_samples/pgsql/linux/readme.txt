PgSQL DB commands for eventium schema(event tracking DB).
Started: [2014-01-14]

Admin commands:
  - startdb
CLI commands (and some admin too):
  - client, stopdb

0. Init server DB
initserverdb

Set your OS user password in pgsql CLI:
ALTER USER osuser WITH PASSWORD 'some_pwd';

1. Manage DB
NOTE: Replace full path to your version.

1.1. Create:
createvdb

1.2. Drop:
dropevdb

NOTE: If you set OS user password(some_pwd), it will be prompted.

2. Create user
root
CREATE USER etracker PASSWORD 'shmetracker';
REVOKE ALL PRIVILEGES ON SCHEMA PUBLIC FROM etracker;
\q


3. SQL base lifecycle:
client
-- commands
\q

4. SQL scripts:

-----
Can be called from cmd line:
psql -U username -d myDataBase -a -f myInsertFile
-----

4.1. Create tables: 
\i enter_full_path_to_sql/sql/create_tables.sql;

4.2. Fill them out with test data:
\i enter_full_path_to_sql/sql/import_test_data.sql;

4.3. Clean all tables:
\i enter_full_path_to_sql/sql/clean_tables.sql;

4.4. Drop tables:
\i enter_full_path_to_sql/sql/drop_tables.sql;



