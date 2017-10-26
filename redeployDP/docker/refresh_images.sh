#!/bin/bash

export MIGRATOR_ID=`docker images | awk '/db_migrator/ {print $3}' | head -n 1`
export BUILDER_ID=`docker images | awk '/builder/ {print $3}' | head -n 1`
TAG=docker.snapshot.travel:5000/snapshot
PUSH="docker push"

cd ~/tmp/dataplatformcoreqa/redeployDP/docker
git checkout master
git pull

docker rmi $MIGRATOR_ID -f
docker rmi $BUILDER_ID -f

./build_all.sh
if [ $? -eq 0 ]
then
    $PUSH $TAG/dataplatform_builder:latest
    $PUSH $TAG/db_migrator:latest
    $PUSH $TAG/nonpms_integrations:latest
fi
