-- Cassandra DB DMLs for eventium DB
-- [2014-02-10]
--  In cassandra client(cqlsh):
USE eventium;

--=============
-- Some selects:

select * from event_categories;
select * from events;
--
select code, name
from event_categories;
--
select id, title, category_code , severity , source , process_id, created
from events;
-- shorter:
select id, category_code , severity , source , process_id, created
from events;
-- TODO: make it work later
-- select id, category_code , severity , source , process_id, created from events where title = 'sample_title__982433708';
--
SELECT code,name FROM eventium.event_categories WHERE code = 'MESSAGE';
--
select * from events WHERE id = 1000001;
select * from events WHERE id = 8326098653025532731;
select * from events WHERE severity = 'INFO';

select * from events WHERE created = 1391669022854001;

SELECT id, title, category_code , severity , source , process_id, created FROM eventium.events 
	WHERE id IN (3942904931258264117,8326098653025532731,993840280701655400,1117781649277071314,1391669022854005);

SELECT id, title, category_code , severity , source , process_id, created FROM eventium.events 
    WHERE id IN (1391669022854005,1391669022854004,1391669022854003,1391669022854002,1391669022854001);

-- ERR: Bad Request: No indexed columns present in by-columns clause with Equal operator
--
-----
-- You cannot delete by range in Cassandra. You have to add the columns for deletion individually.
DELETE FROM event_by_created_slices where year = 2014 AND id = 1117781649277071314;
DELETE FROM event_by_created_slices where year = 2014;
-----
-- Bad Request: ORDER BY is only supported when the partition key is restricted by an EQ or an IN.
select * from events order by created desc limit 3 ALLOW FILTERING;
-----
SELECT * from system.schema_keyspaces;
--
select id, writetime(title) from events;
--
select year, created, event_id FROM event_by_created_slices;
select year, created, event_id FROM event_by_created_slices WHERE year = 2014 ORDER BY created DESC LIMIT 15;
-- ranges:
-- For events doesnt work:
SELECT id FROM events 
 	WHERE created > 1391669022854001 AND created <= 1391669022854004;
-- Bad Request: No indexed columns present in by-columns clause with Equal operator
SELECT year, created, event_id FROM event_by_created_slices 
 	WHERE created > 1391669022854001 AND created <= 1391669022854004;
-- Bad Request: Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this que
--   ry despite the performance unpredictability, use ALLOW FILTERING

-- Working, but can be slow:
SELECT year, created, event_id FROM event_by_created_slices 
 	WHERE created > 1391669022854001 AND created <= 1391669022854004 LIMIT 3 ALLOW FILTERING;

-- Error: 	
SELECT year, created, event_id FROM event_by_created_slices 
 	WHERE created > 1391669022854001 AND created <= 1391669022854004 ORDER BY created DESC LIMIT 3 ALLOW FILTERING;
-- Bad Request: ORDER BY is only supported when the partition key is restricted by an EQ or an IN.

-- Working:
SELECT year, created, event_id FROM event_by_created_slices 
 	WHERE year = 2014 AND created > 1391669022854001 AND created <= 1391669022854004 ORDER BY created DESC LIMIT 3;

-- Working:
SELECT year, created, event_id FROM event_by_created_slices 
 	WHERE year = 2014 AND created > 1391669022854001 AND created <= 1391669022854004 ORDER BY created DESC LIMIT 3;
--

-- Notice error:
select year, created, event_id FROM event_by_created_slices ORDER BY created DESC LIMIT 3;
-- Bad Request: ORDER BY is only supported when the partition key is restricted by an EQ or an IN.






--=============
-- Common commands:
DESCRIBE CLUSTER;
DESCRIBE FULL SCHEMA;
DESCRIBE SCHEMA;
DESCRIBE KEYSPACES;
DESCRIBE KEYSPACE eventium;
DESCRIBE TABLES;
DESCRIBE TABLE events;
--
SHOW VERSION;
SHOW HOST;
SHOW ASSUMPTIONS;
SHOW SESSION ( tracing session id )

--=============
-- NOTE: index sh be created for king field if it's not part of PK.
SELECT * FROM ruling_stewards
  WHERE king = 'Brego'
  AND reign_start >= 2450
  AND reign_start < 2500 ALLOW FILTERING;

--=============
-- If you want to order inserted values e.g. desc, use: 
create table timeseries (
  event_type text,
  insertion_time timestamp,
  event blob,
  PRIMARY KEY (event_type, insertion_time)
)
WITH CLUSTERING ORDER BY (insertion_time DESC);
--=============
-- security:
CREATE USER boone WITH PASSWORD 'Niner75' NOSUPERUSER;
--=============
-- Some docs:
--   http://www.slideshare.net/planetcassandra/nyc-tech-day-new-cassandra-drivers-in-depth-17867623
--=============
-- security:
list users;
list all permissions of etracker;

--=============
