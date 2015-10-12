API test based on Cucumber, Serenity and Rest-assured frameworks
================================================================

How to run tests:
1/ modify src/test/resources/dp.properties file to point to correct urls for all modules, modules has to be deployed and running
2/ run command from commandline:
    gradle build aggregate

3/ in target/site/serenity/index.html you will find reports

Notes:
======
It may be slow if cloud MySQL account is used from localhost, local mysql is fast enough not to stuck tests
