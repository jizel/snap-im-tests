#!/bin/bash

echo "Preparing the database"
git pull

cd /root/$NONPMS_REPO_NAME/DB
for dir in `ls`
do
    if [ -d $dir ]
    then
        cd $dir
        ../../gradlew flywayMigrate -Pflyway.url=jdbc:mariadb://mariadb:3306 \
        -Pflyway.user=root -Pflyway.password=$DB_PASSWORD
        cd ..
    fi
done
