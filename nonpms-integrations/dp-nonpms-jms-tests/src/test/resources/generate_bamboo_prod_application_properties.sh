#!/bin/sh

cat <<EOF>application-production.properties
spring.activemq.broker-url=${ACTIVEMQ_URL}
spring.activemq.user=${ACTIVEMQ_USER}
spring.activemq.password=${ACTIVEMQ_PASSWORD}
integration.facebook.affected.properties=208a02ab-08be-4514-af44-932eadc22097
integration.instagram.affected.properties=208a02ab-08be-4514-af44-932eadc22097
integration.twitter.affected.properties=208a02ab-08be-4514-af44-932eadc22097
integration.googleanalytics.affected.properties=208a02ab-08be-4514-af44-932eadc22097
integration.tripadvisor.affected.properties=208a02ab-08be-4514-af44-932eadc22097
notification.integration_failure.topic=VirtualTopic.Notifications.integration_failure
notification.etl.topic=VirtualTopic.Notifications.etl
notification.set.pub.domain=true
EOF
