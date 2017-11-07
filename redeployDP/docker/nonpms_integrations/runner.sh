#!/bin/sh

cp /data/modules/$ARTIFACT ./app.jar
java -Dspring.profiles.active=$SPRING_PROFILE -Djava.security.egd=file:/dev/urandom -jar app.jar
