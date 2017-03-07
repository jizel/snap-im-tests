SET DP_DIRECTORY=%1
SET GIT_BRANCH=%2

echo Build DP project with gradle..
pushd %DP_DIRECTORY%
call git fetch
call git checkout %GIT_BRANCH%
call git pull
call gradle build -x test