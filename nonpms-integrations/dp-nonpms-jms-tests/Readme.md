# DP JMS Tests
The JMS tests for testing NonPMS integrations. 

The test starts an integration and evaluates incomming ETL and integration failure notifications.
The test does not create any test data. The test assumes the affected properties and their integration configurations
already exist.

The spring application property file ```application.profile``` is used to configure a test for specific environment.
 
## Usage
Run tests locally

```gradle clean test```

Run tests against test environment

```gradle clean test -Pspring.profiles.active=test```

Run suite to run tests against stable environment in IDE

```StableSuite```
