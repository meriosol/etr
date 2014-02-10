-- Add test data to eventium.
--  In cassandra client(cqlsh):
USE eventium;

-- 1. Event categories
INSERT INTO event_categories (code,name) VALUES ('START','Start');
INSERT INTO event_categories (code,name) VALUES ('INTERACT','Interact');
INSERT INTO event_categories (code,name) VALUES ('MESSAGE','Message');
INSERT INTO event_categories (code,name) VALUES ('FINISH','Finish');

-- 2. Events
INSERT INTO events (id, title, category_code , severity , source , process_id, created) 
	VALUES (1000001, 'Initial test1 start','START','INFO','local','00001', 1391822114851);

INSERT INTO events (id, title, category_code , severity , source , process_id, created) 
	VALUES (1000002, 'Some warning test1 message','MESSAGE','WARN','local','00001', 1391822114852);

INSERT INTO events (id, title, category_code , severity , source , process_id, created) 
	VALUES (1000003, 'Interaction test1','INTERACT','INFO','local','00001', 1391822114853);

INSERT INTO events (id, title, category_code , severity , source , process_id, created) 
	VALUES (1000004, 'Some error test1 message','MESSAGE','ERROR','local','00001', 1391822114854);

INSERT INTO events (id, title, category_code , severity , source , process_id, created) 
	VALUES (1000005, 'Test1 complete','FINISH','INFO','local','00001', 1391822114855);

-- 3. Events slices by created

INSERT INTO event_by_created_slices (year, created, event_id) 
	VALUES (2014, 1391822114851, 1000001);
INSERT INTO event_by_created_slices (year, created, event_id) 
	VALUES (2014, 1391822114852, 1000002);
INSERT INTO event_by_created_slices (year, created, event_id) 
	VALUES (2014, 1391822114853, 1000003);
INSERT INTO event_by_created_slices (year, created, event_id) 
	VALUES (2014, 1391822114854, 1000004);
INSERT INTO event_by_created_slices (year, created, event_id) 
	VALUES (2014, 1391822114855, 1000005);


