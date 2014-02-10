-- Clean eventium DB.
--  In cassandra client(cqlsh):
USE eventium;

--DELETE FROM events;
--DELETE FROM event_by_created_slices WHERE year in (2014,2013);
--DELETE FROM event_categories WHERE code in ();

TRUNCATE events;
TRUNCATE event_by_created_slices;
TRUNCATE event_categories;