#!/bin/sh
# ---------------------------------------------------------
# -- Set root password
# ---------------------------------------------------------

MYSQL_ROOT_PWD=todo-root-password
/usr/bin/mysqladmin -u root password $MYSQL_ROOT_PWD
