Cassandra commands for eventium keyspace(event tracking DB).
Started: [2014-02-01]

To enable security, set in conf/cassandra.yaml:
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer

1. To create DB:
1.1. Login as root
cqlsh -u cassandra -p cassandra

If all OK, you'll see smth like this:
Connected to Test Cluster at localhost:9160.
[cqlsh 4.1.0 | Cassandra 2.0.4 | CQL spec 3.1.1 | Thrift protocol 19.39.0]
Use HELP for help.
cqlsh>

CAUTION: cql files have Unix EOL, so for win env they'll likely need unix2dos conversion.

1.2. Create keyspace:
SOURCE 'full_path_cql/cql/create_db.sql'

1.3. Create sample user and grant permissions:
SOURCE 'full_path_cql/cql/create_user.sql'

1.4. (Later when needed) Drop sample user:
SOURCE 'full_path_cql/cql/drop_user.sql'

1.5. (Later when needed) Drop keyspace:
SOURCE 'full_path_cql/cql/drop_db.sql'


2. To create column families:

2.1. Login as sample user:
cqlsh -u etracker -p shmetracker

2.2. Create tables:
SOURCE 'full_path_cql/cql/create_tables.sql'

2.3. Load tables with test data:
SOURCE 'full_path_cql/cql/import_test_data.sql'

2.4. Clean up tables:
SOURCE 'full_path_cql/cql/clean_tables.sql'
   - CAUTION: cannot delete all events (with composite key). But workaround found..

2.5. Drop tables:
SOURCE 'full_path_cql/cql/drop_tables.sql'


3. Sample CQL:
 - See cql/cass_eventium_sql_cmds.sql
 - Don't forget to set keyspace:
USE eventium; 

SELECT * from event_categories;

For exit, use quit or exit.
exit;
