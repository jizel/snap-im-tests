#!/bin/sh

cat <<EOF>application-production.properties
spring.activemq.broker-url=${ACTIVEMQ_URL}
spring.activemq.user=${ACTIVEMQ_User}
spring.activemq.password=${ACTIVEMQ_PASSWORD}
EOF
