#!/bin/bash

DOCKER_REGISTRY=docker.snapshot.travel:5000

function get_image_id()
{
    docker images | grep $1 | tr -s " " | cut -d" " -f3 | head -n 1
}

function tag_image()
{
    image_id=$(get_image_id $1)
    docker tag $image_id $DOCKER_REGISTRY/$1:$2
}

docker build --force-rm=true -t snapshot/base base
tag_image snapshot/base latest
tag_image snapshot/base 2.0

docker build --force-rm=true -t snapshot/java java
tag_image snapshot/java latest
tag_image snapshot/java 8u131

docker build --force-rm=true -t snapshot/db_migrator db_migrator
tag_image snapshot/db_migrator latest
tag_image snapshot/db_migrator 1.37

docker build --force-rm=true -t snapshot/nonpms_integrations nonpms_integrations
tag_image snapshot/nonpms_integrations latest
tag_image snapshot/nonpms_integrations 1.37

# docker build --force-rm=true -t snapshot/dataplatform_builder builder
# tag_image snapshot/dataplatform_builder latest
# tag_image snapshot/dataplatform_builder 1.37
