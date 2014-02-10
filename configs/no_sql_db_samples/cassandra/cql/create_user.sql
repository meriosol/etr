-- Create sample user.
--  In cassandra client(cqlsh) as a root:

CREATE USER etracker WITH PASSWORD 'shmetracker';
GRANT ALL PERMISSIONS ON KEYSPACE eventium TO etracker;