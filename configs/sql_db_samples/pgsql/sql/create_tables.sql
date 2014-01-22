-- Create tables for eventium DB.
--  In pgsql client:

-- 1. Event categories
CREATE TABLE event_categories (code VARCHAR(16) PRIMARY KEY
, name VARCHAR(100) NOT NULL
, CONSTRAINT unq_event_categories_name UNIQUE(name));

COMMENT ON TABLE event_categories IS 'Event categories';

-- 2. Events
CREATE TABLE events (id BIGSERIAL PRIMARY KEY 
, category_code VARCHAR(16) 
, title VARCHAR(100) NOT NULL
, severity varchar(5)
, source varchar(100)
, process_id varchar(100)
, created TIMESTAMP DEFAULT current_timestamp
, CONSTRAINT fk_event_categories_code FOREIGN KEY (category_code) REFERENCES event_categories (code) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
, CONSTRAINT severity_vals CHECK (severity IN ('INFO','WARN','ERROR','FATAL'))
);

COMMENT ON TABLE events IS 'Events';
ALTER SEQUENCE events_id_seq RESTART WITH 1000001; 


CREATE INDEX events_severity_index ON events(severity);

