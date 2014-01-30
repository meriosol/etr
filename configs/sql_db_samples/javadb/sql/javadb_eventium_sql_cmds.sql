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
select id, category_code , severity , source , process_id, created from events where title like 'sample_title__982433708';
--
select e.id as "id", ec.code as "category_code", ec.name as "category_name", e.severity as "severity", e.source as "source",e.process_id as "processId", e.created as "created"
 from events e left join event_categories ec on e.category_code = ec.code where e.id = 1000001;
--
select id, category_code , title , severity , source , process_id, created
from events where id = 1000278;
--
select id, category_code , severity , source , process_id, created
from events order by created desc;
--
update event_categories set name = 'Message' where code = 'MESSAGE';
--=============
select
        this_.id as id,
        this_.category_code as cat_cd,
        eventcateg2_.code as cd_code,
        eventcateg2_.name as cd_name 
    from
        events this_ 
    left outer join
        event_categories eventcateg2_ 
            on this_.category_code=eventcateg2_.code 
    order by
        this_.created desc OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
--=============
-- pagination sample:
select * from (select row_number() over() as rownum,  
	table_occurrence.* from table_occurrence) as temp where rownum>=1  
	and rownum<=10;
--=============
-- For events:
select * from (select row_number() over() as rownum,  
	events.* from events) as temp where rownum>=1  
	and rownum<=10;
-- latest 10 events:	
select * from events order by created desc OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
--=============

