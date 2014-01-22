-- Java DB DMLs for eventium DB
-- [2014-01-14]
 
-- Connect in ij:
connect 'jdbc:derby://localhost:1527/eventium;user=etracker;password=shmetracker';
-- Exit from ij:
exit;

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
--
--=============
