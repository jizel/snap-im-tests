#!/bin/bash

function fetch() {
    cd /root/$1
    pwd
    git fetch origin
    if [ "$2" ]
    then
      echo "switching to branch $2"
      git checkout $2
    fi
    git pull
}

# First fetch and build all modules except identity
fetch $NONPMS_REPO_NAME
./gradlew build -x test
find . -iname *.jar -exec mv {} /data/modules/ \;
cp -r DB /data

# Now do the same for identity module that as of Mon March 20 2017 resides in a
# separate repo
fetch $IDENTITY_REPO_NAME $DP_BRANCH_NAME
./gradlew build -x test
cp IdentityModule/build/libs/IdentityModule-1.0.jar /data/modules

# We do not need to copy over migration scripts: spring boot will execute
# migration automatically
