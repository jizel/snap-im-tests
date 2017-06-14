#!/bin/sh

mysql_install_db --user root > /dev/null

echo "Starting temporary MariaDB instance for the initialization"

# Turn on mysqld for 10 secs to update permissions
timeout 10 mysqld $MYSQLD_OPTIONS &

# wait few secs to be sure

sleep 2

echo "Executing permissions script against temporary MariaDB instance"

mysql -u root -h 127.0.0.1 -P 3306 < /root/mariadb.sql

sleep 8
