load test for DP API
=========================

Load tests exercising Data Platform REST API.

## Configuration
 
 Common configuration is encapsulated in 
 [AbstractSimulation](src/test/scala/travel/snapshot/dp/qa/AbstractSimulation.scala) class.
 
 Resolution of testing environment is done in
 [BaseUrlResolver](src/test/scala/travel/snapshot/dp/qa/utils/BaseUrlResolver) class.
  
### Configuration properties
  Each configuration property can be overriden via System property.
  
  * -Dprotocol=<protocol to be used for communication with testing server> , default is "http" 
  * -Dhost=<testing server hostname> , default is "localhost"
  * -Dport=<port on testing server where the rest api is running> , default is 8080
  * -DstartUsers=<initial number of users used> , default is 10
  * -DendUsers=<final number of users used for load> , default is 30
  * -Dramp=<time in second how long to run test and increase number of  user from start to end number> , default is 60
  * -Dgatling_log_level=<ERROR|WARN|INFO|DEBUG>
  * -Daccess_token=<access token for requests>
  
  You have to specify environment against which load tests will be executed. This is specified by system property
  
  * -Denvironment=<local|development|testing|production>
  
  * development - will execute tests against machine 'rg01we-dp-dev-tomcat1.westeurope.cloudapp.azure.com'
  * testing - will execute tests against machine 'rg01we-dp-test-tomcat1.westeurope.cloudapp.azure.com'
  * production - will execute tests against 'de.api.snapshot.technology'
  * local - will execute tests against 'localhost'
  
  When this property is not set, local is picked by default.
  
  Resolved environment have all above properties automatically resolved (protocol, host, port) and it is not needed
  to specify them once again on the command line unless you want to override some property for some environment.

## Examples

To test identity/user endpoints against local environment


    mvn clean gatling:execute -Denvironment=local -Dgatling.simulationClass=travel.snapshot.dp.qa.identity.IdentityUserSimulation -DstartUsers=10 -DendUsers=60 -Dramp=120


To test Facebook endpoints against development environment:

    mvn clean gatling:execute -Denvironment=development -Dgatling.simulationClass=travel.snapshot.dp.qa.social.facebook.FacebookSimulation -DstartUsers=10 -DendUsers=60 -Dramp=120



