REM !!!Change folowing params according to your settings and wishes!!!
SET DP_DIR=d:\git\data-platform\
SET DP_IDENTITY_DIR=d:\git\data-platform-identity\
SET WEBAPPS_DIR=c:\devtools\Apache\Tomcat8\webapps\
SET dp_git_branch=master
SET identity_git_branch=master
SET DP_QA_FAKE_DATA_DIR=d:\git\dataplatformcoreqa\fake-tsv-data\

REM ___Don't change this one___
SET SCRIPT_HOME=%cd%

echo Complete redeploy of DP

call steps\pull_buildDP.bat %DP_DIR% %dp_git_branch%
pushd %SCRIPT_HOME%
call steps\pull_buildDP.bat %DP_IDENTITY_DIR% %identity_git_branch%
pushd %SCRIPT_HOME%
call %SCRIPT_HOME%\steps\drop_migrateDB.bat %DP_DIR% %DP_IDENTITY_DIR%
pushd %SCRIPT_HOME%
call %SCRIPT_HOME%\steps\load_fake_data.bat %DP_QA_FAKE_DATA_DIR%
pushd %SCRIPT_HOME%
call %SCRIPT_HOME%\steps\del_copy_boot.bat %WEBAPPS_DIR% %DP_DIR% %DP_IDENTITY_DIR%

echo You're good to go now!
echo _________________________
echo Enjoy your fresh DP build
pause