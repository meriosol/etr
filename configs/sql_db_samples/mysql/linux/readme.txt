MySQL DB commands for eventium schema(event tracking DB).
Started: [2014-01-14]

0. Set mysql root password (optional, for stronger security)
0.a. Launch setrootpwd
0.b. Got to mysql client and perform commands:
/usr/bin/mysql -h localhost -u root

UPDATE mysql.user SET Password=PASSWORD('root-password') WHERE User='root';
FLUSH PRIVILEGES;
\q

NOTE: it works only one time when no password set yet.

NOTE: You also can consider deleting not needed stuff. See sql/delete_mysql_unneeded.sql.

1. Manage DB
NOTE: Replace full path to your version.

1.1. Create:
./root.sh
source ~/runtimes/for_etr/mysql/sql/create_db.sql;
\q

1.2. Drop:
./root.sh
source ~/runtimes/for_etr/mysql/sql/drop_db.sql;
\q

2. SQL base lifecycle:
./client.sh
use eventium;
-- commands
\q

3. SQL scripts:

3.1. Create tables: 
source ~/runtimes/for_etr/mysql/sql/create_tables.sql;

3.2. Fill them out with test data:
source ~/runtimes/for_etr/mysql/sql/import_test_data.sql;

3.3. Clean all tables:
source ~/runtimes/for_etr/mysql/sql/clean_tables.sql;

3.4. Drop tables:
source ~/runtimes/for_etr/mysql/sql/drop_tables.sql;

--================================
TODO:
-- TODO: add LOB-like field: 
--ALTER TABLE event_categories ADD COLUMN description longvarchar;

-- ALTER TABLE tbl_name AUTO_INCREMENT = N;

-- TODO: add LOB-like field: 
--ALTER TABLE events ADD COLUMN details clob;
--================================



