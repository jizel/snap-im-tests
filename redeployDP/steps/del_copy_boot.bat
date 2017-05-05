SET WEBAPPS_DIR=%1
SET DP_DIR=%2
SET DP_IDENTITY_DIR=%3

echo Deleting webapps..
pushd %WEBAPPS_DIR%
call del * /s /q
call rmdir . /s /q

echo Copying war files to webapps - omiting OTAIntegration!
copy %DP_DIR%\Integration\Twitter\ServiceApi\build\libs\TwitterAnalyticsApi-1.0-SNAPSHOT.war .
copy %DP_DIR%\Integration\Instagram\ServiceApi\build\libs\InstagramAnalyticsApi-1.0-SNAPSHOT.war .
copy %DP_DIR%\Integration\GoogleAnalytics\ServiceApi\build\libs\GoogleAnalyticsApi-1.0-SNAPSHOT.war .
copy %DP_DIR%\Integration\Facebook\ServiceApi\build\libs\FacebookAnalyticsApi-1.0-SNAPSHOT.war .
copy %DP_DIR%\Integration\SocialMediaApi\build\libs\SocialMediaAnalyticsApi-1.0-SNAPSHOT.war .
copy %DP_DIR%\Review\build\libs\Review-1.0.war
copy %DP_DIR%\RateShopper\build\libs\RateShopper-1.0.war .
copy %DP_DIR%\ConfigurationModule\build\libs\ConfigurationModule-1.0.war .
copy %DP_DIR%\Integration\GoogleAnalytics\ServiceApi\build\libs\WebPerformance-1.0.war .

echo Running Identity module using SpringBoot
pushd %DP_IDENTITY_DIR%\IdentityModule\build\libs\
call java -jar IdentityModule-1.0.jar