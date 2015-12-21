load test for DP API
=========================

Load tests exercising Data Platform REST API.

## Configuration
 
 Common configuration is encapsulated in 
 [AbstractSimulation](src/test/scala/travel/snapshot/dp/qa/AbstractSimulation.scala) class.
  
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
  

## Endpoints

To test identity/customers endpoint 


    mvn clean gatling:execute -Dgatling.simulationClass=travel.snapshot.dp.qa.identity.BasicIdentitySimulation -DstartUsers=10 -DendUsers=60 -Dramp=120


To test configuration endpoint:

    mvn clean gatling:execute -Dgatling.simulationClass=travel.snapshot.dp.qa.configuration.BasicConfigurationSimulation -DstartUsers=10 -DendUsers=60 -Dramp=120



