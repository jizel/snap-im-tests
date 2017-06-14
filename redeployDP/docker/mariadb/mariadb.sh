#!/bin/sh

echo "Starting MariaDB process"


# clean up Aria engine control/log files, which prevent MariaDB startup 
# after container restart
# see https://mariadb.atlassian.net/browse/MDEV-7218
# TODO: remove it after final fix of the isse
rm -f /var/lib/mysql/aria_*

# this is wrapped in exec in order to be sure that it can receive Unix signals (SIGTERM)
# correctly so the exit status upon docker-compose stop process is correctly returned

mysqld $MYSQLD_OPTIONS &

if [ "$MYSQL_DATABASE" ]
then
    sleep 5
    echo "CREATE DATABASE $MYSQL_DATABASE ;" | mysql
fi

sleep infinity
