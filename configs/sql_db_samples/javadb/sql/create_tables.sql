-- Create tables for eventium DB.
--  In derby ij client:

-- 1. Event categories
CREATE TABLE event_categories (code VARCHAR(16) PRIMARY KEY
, name VARCHAR(100) NOT NULL
, CONSTRAINT unq_event_categories_name UNIQUE(name));

-- TODO: add LOB-like field: 
--ALTER TABLE event_categories ADD COLUMN description longvarchar;


-- 2. Events
CREATE TABLE events (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000001, INCREMENT BY 1) PRIMARY KEY
, category_code VARCHAR(16)
, title VARCHAR(100) NOT NULL
, severity varchar(5)
, source varchar(100)
, process_id varchar(100)
, created TIMESTAMP DEFAULT current_timestamp
, CONSTRAINT fk_event_categories_code FOREIGN KEY (category_code) REFERENCES event_categories (code)
, CONSTRAINT severity_vals CHECK (severity IN ('INFO','WARN','ERROR','FATAL'))
);

CREATE INDEX events_severity_index ON events(severity);
-- TODO: add LOB-like field: 
--ALTER TABLE events ADD COLUMN details clob;
