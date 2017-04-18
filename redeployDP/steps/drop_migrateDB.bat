SET DP_DIRECTORY=%1
SET DP_IDENTITY_DIRECTORY=%2

echo Droping all DP tables...
pushd steps
mysql --force -h "127.0.0.1" -u "root" < drop_all.sql

echo DB migration..
pushd %DP_DIRECTORY%\DB\ota_tti\
call gradle flywayMigrate -Pflyway.url=jdbc:mariadb://localhost:3306 -Pflyway.user=root
pushd %DP_DIRECTORY%\DB\dp\
call gradle flywayMigrate -Pflyway.url=jdbc:mariadb://localhost:3306 -Pflyway.user=root
pushd %DP_IDENTITY_DIRECTORY%\
call gradle flywayMigrate -Pflyway.url=jdbc:mariadb://localhost:3306 -Pflyway.user=root