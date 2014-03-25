-- Create new keyspace with name eventium.
--  In cassandra client(cqlsh):

CREATE KEYSPACE eventium WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };