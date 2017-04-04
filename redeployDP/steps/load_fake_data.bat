SET DP_QA_FAKE_DATA_DIR=%1

echo lOADING FAKE SQL DATA...
pushd %DP_QA_FAKE_DATA_DIR%
mysql --force --local-infile -h "127.0.0.1" -u "root" < load.sql