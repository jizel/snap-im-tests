load test for DP API
=========================

Load tests exercising Data Platform REST API.

## Configuration
 
 Common configuration is encapsulated in 
 [AbstractSimulation](src/test/scala/travel/snapshot/dp/qa/AbstractSimulation.scala) class.
 
 Resolution of testing environment is done in
 [BaseUrlResolver](src/test/scala/travel/snapshot/dp/qa/utils/BaseUrlResolver) class.
  
### Configuration properties
  Each configuration property can be overridden via System property.
  
  * -Dprotocol=<protocol to be used for communication with testing server> , default is "http" 
  * -Dhost=<testing server hostname> , default is "localhost" (specified by environment used)
  * -Dport=<port on testing server where the rest api is running> , default is 8080
  * -DstartUsers=<initial number of users used> , default is 10
  * -DendUsers=<final number of users used for load> , default is 30
  * -Dramp=<time in second how long to run test and increase number of  user from start to end number> , default is 60
  * -Dgatling_log_level=<ERROR|WARN|INFO|DEBUG>
  * -Daccess_token=<access token for requests>
  
  You have to specify environment against which load tests will be executed. This is specified by system property
  
  * -Denvironment=<local|development|testing|production|staging|stable|VM12>
  
  * development - will execute tests against machine 'rg01we-dp-dev-tomcat1.westeurope.cloudapp.azure.com'
  * testing - will execute tests against machine 'europewest-sso-test1.snapshot.technology'
  * local - will execute tests against 'localhost'
  * staging - will execute tests against - europewest-api-staging.snapshot.technology
  * stable - will execute tests against europewest-api-stable.snapshot.technology
  * VM12 - will execute tests against VM running on 192.168.1.127
  * production - will execute tests against 'de.api.snapshot.technology'
  
  When this property is not set, local is picked by default.
  
  Resolved environment have all above properties automatically resolved (protocol, host, port) and it is not needed
  to specify them once again on the command line unless you want to override some property for some environment.
  
  ### Oauth Credentials
  Specify user and application to request keycloak token by setting the following params:
  
  * **clientId** - Application version id
  * **clientSecret** - Keycloak's secret for the application version above
  * **username** - username from IM
  * **userPassword** - password for the user above
  
  Default values are set for stable environment and could be changed in AbstractIdentitySimulation class.
  
  ### User injection method
  Specifies how Gatling users (threads that fire requests) are injected into the test. Three options are implemented now:
  
  * **atOnce** - works together with startUsers parameter (endUsers and ramp params are omitted). Injects all users at once at the beginning of the test. Each user
  makes all the requests specified in given simulation class and then the test ends. This kind of test has the shortest runtime.
  * **rampSimple** - uses endUsers and ramp parameters(endUsers is omitted). Test begins with one user and during the ramp time the number of users
  grows to endUsers. 
  * **rampPerSec** - **default value**, used if no injection method specified. Ramps users every second from startUsers to endUsers
  over the ramp time. Very aggressive method that generates a lot of requests. Use carefully.

## Examples

To test getAll for all basic IM endpoints endpoints against stable environment injecting all users at once


    mvn clean gatling:execute -Dusername=jiriCustomer -DuserPassword=Pass12345! -DclientSecret=dabe1ef3-29af-417a-933b-ca254e59b1fd -DclientId=cf9966b3-b2c5-43c9-8fe1-17dea13ae4b7 -Denvironment=stable -Dgatling.simulationClass=travel.snapshot.dp.qa.identity.getOnly.GetAll -DstartUsers=5 -DinjectionMethod=atOnce


To test getAll for customers and properties, ramping users per second, against test environment using accessToken directly (getToken step will fail of course):

    mvn clean gatling:execute -Denvironment=test -Dgatling.simulationClass=travel.snapshot.dp.qa.identity.getOnly.GetAll -DstartUsers=10 -DendUsers=30 -Dramp=60  -DaccessToken=SOME_VALID_ACCESS_TOKEN
    
 ## Results
 Test results can be found in
 
    dataplatformcoreqa/load_tests/target/gatling/results/



