-- Create tables for eventium DB.
--  In mysql client:

-- 1. Event categories
CREATE TABLE event_categories (code VARCHAR(16) PRIMARY KEY
, name VARCHAR(100) NOT NULL
, CONSTRAINT unq_event_categories_name UNIQUE(name)) COMMENT = 'Event categories';


-- 2. Events
CREATE TABLE events (id BIGINT PRIMARY KEY AUTO_INCREMENT
, category_code VARCHAR(16) REFERENCES event_categories (code) ON DELETE RESTRICT ON UPDATE CASCADE
, title VARCHAR(100) NOT NULL
, severity varchar(5)
, source varchar(100)
, process_id varchar(100)
, created TIMESTAMP DEFAULT current_timestamp
, CONSTRAINT severity_vals CHECK (severity IN ('INFO','WARN','ERROR','FATAL'))
) COMMENT = 'Events', AUTO_INCREMENT = 1000001;

CREATE INDEX events_severity_index ON events(severity);
