# Data Platform ETL tests

## Setup
You can find all configuration settings in [config.properties](src/main/resources/config.properties).
Basically, the proper database configuration for both source (dma) and target (dwh) needs to be 
provided. Defaults have been set up in configuration file, but you can provide your own values via
system properties - see 'Running tests' section.

## Running tests

```gradle test``` or with `-i` to get more logging ```gradle test -i``` 

You can run the tests in debug mode (and attach remote debugger) if you want: 
```gradle -Dtest.debug=true test```

All configuration properties can be overridden via system properties - either in IDE configuration
 or directly on command line as follows:
```gradle test -Ddwh.username=<dwhuser> -Ddwh.password=<dwhpassword>``` 


