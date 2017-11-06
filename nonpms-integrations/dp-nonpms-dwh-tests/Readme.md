# DP NonPMS DWH Tests
The NonPMS integration tests for monitoring DWH. 

The spring application property file ```application.profile``` is used to configure a test for specific environment.
 
## Usage
Run tests locally

```gradle clean test```

Run tests against test environment

```gradle clean test -Pspring.profiles.active=test```

Run suite to run tests against stable environment in IDE

```StableSuite```
