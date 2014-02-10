-- Create tables for eventium DB.
--  In cassandra client(cqlsh):
USE eventium;

-- 1. Event categories
CREATE TABLE event_categories (
	code text PRIMARY KEY
  , name text) WITH comment='Event categories';

-- 2. Events
CREATE TABLE events (
  id bigint PRIMARY KEY
, category_code varchar
, title text
, severity varchar
, source varchar
, process_id varchar
, created bigint
) WITH comment='Events';


-- 2. Event PKs
CREATE TABLE event_by_created_slices (
  year int
, created bigint
, event_id bigint 
, PRIMARY KEY(year, created, event_id)
) WITH comment='Event slices by created';

CREATE INDEX eventsCreatedIndex ON events (created);

-- TODO: add LOB-like field: 
--ALTER TABLE events ADD COLUMN details clob;
