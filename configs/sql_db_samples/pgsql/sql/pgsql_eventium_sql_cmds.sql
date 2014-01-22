-- PgSQL DMLs for eventium DB
-- [2014-01-14]
 
-- Connect in PgSQL:
client
-- commands
\q

--=============
-- Some selects:

select * from event_categories;
select * from events;
delete from events where id = 1000005;
--
select code, name
from event_categories;
--
select id, title, category_code , severity , source , process_id, created
from events;
--
select id, category_code , severity , source , process_id, created
from events;
--
--=============
SHOW CREATE TABLE events;
SHOW CREATE TABLE event_categories;
--=============
-- Serials:
--  <table_name>_<column_name>_seq
select * from events_id_seq;
--=============
