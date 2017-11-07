#!/bin/sh

cat <<EOF>application-production.properties
spring.datasource.url=${MARIADB_URL}
spring.datasource.username=${MARIADB_USER}
spring.datasource.password=${MARIADB_PASSWORD}
EOF
