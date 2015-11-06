# Data Platform ETL tests

## Setup
You can find all configuration settings in [config.properties](src/main/resources/config.properties).
Basically, the proper database configuration for both source (dma) and target (dwh) needs to be 
provided. Defaults have been set up in configuration file, but you can provide your own values via
system properties - see 'Running tests' section.

## Build

The project is built by gradle.

### Dependencies
The ETL tool is provided via dependency management.
The temporary solution (until enterprise maven repository is available) is to install
this artifact into the local maven repo. You can publish it via `sbt publish` command.

### Running ETL tool
You can run ETL process explicitly via gradle `etlRun` task.
Don't forget to specify required configuration properties, especially the database credentials!

```
gradle -i runEtl -Ddwh.username=root -Ddwh.password= -Dmetadata.username=root -Dmetadata.password=
```

You can also specify custom startdate:

```
gradle -i runEtl -Ddwh.username=root -Ddwh.password= -Dmetadata.username=root -Dmetadata.password= -Detl.startdate=2015-10-01
```

You can even specify arbitrary arguments for etl tool:

```
gradle -i runEtl -Ddwh.username=root -Ddwh.password= -Dmetadata.username=root -Dmetadata.password= -Detl.args="--startdate 2015-10-01 --dimonly"
```

Check EtlRunner class for more details.

You can also run both `runEtl` and `test` tasks:

```
gradle -i runEtl test -Ddwh.username=root -Ddwh.password= -Dmetadata.username=root -Dmetadata.password=
```

Basic documentation for etlRun task is provided:
```
gradle help --task runEtl
```



### Running tests

```gradle test``` or with `-i` to get more logging ```gradle test -i``` 

You can run the tests in debug mode (and attach remote debugger) if you want: 
```gradle -Dtest.debug=true test```

All configuration properties can be overridden via system properties - either in IDE configuration
 or directly on command line as follows:
```gradle test -Ddwh.username=<dwhuser> -Ddwh.password=<dwhpassword>``` 


