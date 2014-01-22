Derby DB commands for eventium schema(event tracking DB).
Started: [2014-01-14]

NOTE: replace path to sql scripts to your version.

1. To create DB:
clij
run 'F:\alkr\configs\for_event_tracker\javadb\sql\create_db.sql';
exit;

2. SQL base lifecycle:
clij
connect 'jdbc:derby://localhost:1527/eventium;user=etracker;password=shmetracker';
run 'some_sql_scripts_or_commands';
exit;

3. SQL scripts:

3.1. Create tables: 
run 'F:\alkr\configs\for_event_tracker\javadb\sql\create_tables.sql';

3.2. Fill them out with test data:
run 'F:\alkr\configs\for_event_tracker\javadb\sql\import_test_data.sql';

3.3. Clean all tables:
run 'F:\alkr\configs\for_event_tracker\javadb\sql\clean_tables.sql';

3.4. Drop tables:
run 'F:\alkr\configs\for_event_tracker\javadb\sql\drop_tables.sql';



