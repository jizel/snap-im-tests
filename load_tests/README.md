load test for DP API
=========================


To test identity/customers endpoint 
     

    mvn clean gatling:execute -Dgatling.simulationClass=travel.snapshot.dp.qa.BasicIdentityCustomersSimulation -DstartUsers=10 -DendUsers=60 -Dramp=120


To test configuration endpoint:

    mvn clean gatling:execute -Dgatling.simulationClass=travel.snapshot.dp.qa.BasicConfigurationSimulation -DstartUsers=10 -DendUsers=60 -Dramp=120



-DstartUsers= initial number of users used
-DendUsers= final number of users used for load
-Dramp= time in second how long to run test and increase number of  user from start to end number


