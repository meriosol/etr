-- Add test data to eventium.
--  In derby ij client:

-- 1. Event categories
INSERT INTO event_categories (code,name) VALUES ('START','Start');
INSERT INTO event_categories (code,name) VALUES ('INTERACT','Interact');
INSERT INTO event_categories (code,name) VALUES ('MESSAGE','Message');
INSERT INTO event_categories (code,name) VALUES ('FINISH','Finish');



-- 2. Events
INSERT INTO events (title, category_code , severity , source , process_id, created) 
	VALUES ('Initial test1 start','START','INFO','local','00001','2014-01-10 11:03:09.173');
INSERT INTO events (title, category_code , severity , source , process_id, created) 
	VALUES ('Some warning test1 message','MESSAGE','WARN','local','00001','2014-01-10 11:06:12.904');
INSERT INTO events (title, category_code , severity , source , process_id, created) 
	VALUES ('Interaction test1','INTERACT','INFO','local','00001','2014-01-10 11:10:12.8');
INSERT INTO events (title, category_code , severity , source , process_id, created) 
	VALUES ('Some error test1 message','MESSAGE','ERROR','local','00001','2014-01-10 11:06:16.904');
INSERT INTO events (title, category_code , severity , source , process_id, created) 
	VALUES ('Test1 complete','FINISH','INFO','local','00001','2014-01-10 11:10:17.43');
