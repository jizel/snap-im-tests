#!/bin/sh

cat <<EOF>application-production.properties
spring.activemq.broker-url=${ACTIVEMQ_URL}
spring.activemq.user=${ACTIVEMQ_USER}
spring.activemq.password=${ACTIVEMQ_PASSWORD}
integration.manual.properties=${PROPERTY_ID}
integration.manual.integration.date=${INTEGRATION_DATE}
notification.integration_failure.topic=VirtualTopic.Notifications.integration_failure
notification.etl.topic=VirtualTopic.Notifications.etl
notification.set.pub.domain=true
EOF
