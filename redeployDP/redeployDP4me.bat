REM !!!Change folowing params according to your settings and wishes!!!
SET DP_DIR=d:\git\data-platform\
SET WEBAPPS_DIR=c:\devtools\Apache\Tomcat8\webapps\
SET git_branch=master

REM ___Don't change this one___
SET SCRIPT_HOME=%cd%

echo Complete redeploy of DP

call steps\pull_buildDP.bat %DP_DIR% %git_branch%
echo %SCRIPT_HOME%
pushd %SCRIPT_HOME%
call %SCRIPT_HOME%\steps\drop_migrateDB.bat %DP_DIR%
pushd %SCRIPT_HOME%
call %SCRIPT_HOME%\steps\del_copy.bat %WEBAPPS_DIR% %DP_DIR%
pushd %SCRIPT_HOME%

echo You're good to go now!
echo _________________________
echo Enjoy your fresh DP build
pause