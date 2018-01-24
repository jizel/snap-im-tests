#!/bin/bash
set -x

#sleep 600 # Give IM time to fully start
export BASE_FOLDER=`pwd`
export COREQA_BASE=$BASE_FOLDER/CoreQA

cd $COREQA_BASE/load_tests
mvn clean gatling:execute -Dusername=${bamboo.username} \
                          -DuserPassword=${bamboo.user_password} \
                          -DclientSecret=${bamboo.client_secret} \
                          -DclientId=${bamboo.client_id} \
                          -Denvironment=dev2 \
                          -Dgatling.simulationClass=travel.snapshot.dp.qa.identity.getOnly.GetAll \
                          -DstartUsers=8 \
                          -DinjectionMethod=atOnce

mkdir -p TestData/${bamboo.buildNumber}
cp target/gatling/results/getall-*/js/global_stats.json TestData/${bamboo.buildNumber}/
zip -r "${bamboo.planRepository.branchName}-${bamboo.buildNumber}.zip" target/gatling/results/getall-*/*
