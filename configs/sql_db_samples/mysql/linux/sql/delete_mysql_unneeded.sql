-- Delete unneeded stuff in very beginning of work with hmysql:
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host != 'localhost';
DROP DATABASE test;
DELETE FROM mysql.db WHERE Db = 'test' OR Db = 'test\\_%';
FLUSH PRIVILEGES;
