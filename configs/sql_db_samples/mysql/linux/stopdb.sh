#!/bin/sh
# ---------------------------------------------------------
# -- Stops MySQL server
# ---------------------------------------------------------

#/usr/bin/mysqladmin -u root -p shutdown
sudo service mysql stop

echo "o MySQL Server maybe stopped."

# Check if stopped:
# sudo netstat -tap | grep mysql


