# Snapshot Data Platform testing project

## Why should I care?

The main reason why this project exist is to encapsulate whole Snapshot Data Platform runtime and deployment into 
 easily repeatable, fully reproducible and self-contained process so everybody across the team is able to 
deploy Snapshot Data Platform on his / her own in a simple one liner. We chose Docker engine to achieve this.

Because this execution is self-contained, we can deploy Snapshot Data Platform to wherever we want - local host,
Virtual machine in VirtualBox, cloud and so on.

The goal we are trying to achieve is to run tests at some CI environment either at Bamboo upon every PR and commit 
so we want to achieve continuous integration and test driven development. 

Bamboo slaves are said to be quite slow for our purposes so we are investigating other options, e.g. Jenkins 
slaves or some Azure slaves. It does not matter in the end where we will run it - the only thing we need is Docker 
installed there so we are totally independent of the underlying infrastructure and pretty flexible too.

Another reason to have this kind of project is to be able to "snapshot" some possible problem a developer has so 
it can be easilly reproducible by creating Docker image from running container so other developer can debug it.

Last but not least, QA people can easilly test whatever branch from the data-platform repository they want by 
simple one-liner so the burden of the manual deployment and start of all services is not necessary anymore 
and hence it saves a lot of time too.

There is a lot of requests from developers to testers to run tests and see if developed changes do not break anything.
From now on, developers are welcome to test their implementations themselves so QA people do not have to 
deal with their requests anymore. Everybody should be able to test their implementations before it even reaches QA 
level so QA people do not need to deal with obviously unfinished and broken stuff or stuff which even does not compile 
and its functionality is obviously broken.

This project is not meant to be used only for testing purposes. It also serves as a simple way how to get Snapshot 
Data Platform up and running in a matter of couple of seconds / minutes.

This project is still work in progress and e.g. Snapshot integrations are not covered by it as of now. Only whatever 
goes to Tomcat is covered and execution of API tests and loading tests will be done soon too.

This document describes in detail how internals of this project works. In order to know concrete commands how to 
use this project, consult [CHEATSHEET.md](CHEATSHEET.md) file in this directory.

## Where do I find this project?

In _dataplatformqa_ repository which you clone from [here](https://bitbucket.org/bbox/dataplatformqa) (https://bitbucket.org/bbox/dataplatformqa). 
Navigate to `tests` diretory afterwards.

## Principles

We are using Docker for the isolation of the services Data Plaform is using. There are, as of now, these four services:

* Tomcat 8.0.32
* MariaDB 10.1.11
* MongoDB 3.2.1
* ActiveMQ 5.12.3

Version of Java is 1.8.0.66 and version of underlying operating system is CentOS 7.2.1511.

When all Docker containers of these services are started, they see each other by their domain names and they are in 
their own private network.

In case you are running Docker services in VirtualBox, all services are behind external IP address of that VM. This 
can vary but in most cases it will run at `192.168.99.100` so after services are started, you can 
navigate e.g. to `192.168.99.100:8080/manager` to get Tomcat admin console. You get MariaDB connection from 
`192.168.99.100:3306` and you get MongoDB connection from `192.168.99.100:27017`. ActiveMQ manager is running at 
`192.168.99.100:8161/admin/`.

Credentials for Tomcat console are admin:admin, for MySQL root:root, for ActiveMQ admin:admin as well.

When you are running natively (without VirtualBox), your services are not running in virtual machine but 
directly at your computer. In that case you can inspect IP of the container by executing:

`docker inspect --format '{{ .NetworkSettings.IPAddress }}' <container id>`

Container ID can be obtained from `docker ps`.

In case running via `docker-machine`, it is as easy as `docker-machine ip <machine name>.` Name of machine is got from 
`docker-machine ls`.

*When you run in Windows or Mac, you _have to_ run via Docker machine.*

## Preliminaries

Firstly, you have to set up Docker installation at your computer. This is done differently when running 
on Linux, Mac or Windows environment.

Be sure that you are running at version 1.10.x. Download and install Docker from [here](https://github.com/docker/docker/releases/tag/v1.10.1).
(bit.ly/1U6rHUz) and follow [these steps](https://docs.docker.com/engine/installation/).(bit.ly/1mpoVwk). In case you are on Windows / Mac, 
you want [Docker toolbox](https://www.docker.com/products/docker-toolbox). In case you have VirtualBox already installed, it will be upgraded 
by that installer. In case you are on Linux, the installation of VirtualBox have to be made on your own.

You have to install _docker-machine_ as well from [here](https://github.com/docker/machine/releases/tag/v0.6.0) (bit.ly/1Xw8NX5) 
and follow [these steps](https://docs.docker.com/machine/install-machine/) (bit.ly/1WqfGYE). Docker machine is already provided when you are using 
Docker toolbox installer.

Having bash completion installed for `docker` and `docker-machine` binaries is very handy as well. This can 
be found in the documentation and can save you lot of time afterwards.

If you want to use Docker compose, you have to installed it as well from [here](https://github.com/docker/compose/releases/tag/1.6.0) (bit.ly/1omBk4Z)
and follow [installations steps](https://docs.docker.com/compose/) (bit.ly/1FL2VQ6). Docker compose is already provided when you are using Docker toolbox installer.

In the end you should be able to execute `docker` and `docker-machine` and `docker-compose` executables. Be sure to check this works on Windonws as well.

_You have to use Git-Bash console, not system's cmd.exe console, in order to be able to use this project._

In case you are at Windows, be sure you have added paths to these binaries (docker.exe and docker-machine.exe) to 
`PATH` environment variable so you can execute them in Windows terminal without specifing whole path to it.

In case you want to run in VirtualBox (the must in case you run at Windows), you have to install it as well. Tt can be 
downloaded and installed from [here](https://www.virtualbox.org/) (virtualbox.org). In case 
you are installing Docker engine for Windows, there is VirtualBox already bundled in it and it is installed 
automatically.

Be sure to add the path to binaries in VirtualBox installation (VBoxManage.exe and VBoxManage at Linux) to your `PATH` as well.  

Next you have to set password to Docker registry to your `~/.gradle/gradle.properties` file like this:

`systemProp.dockerRegistryPassword=aqGG86d1Yf3Y`

If you want to use your `data-platform` repository already cloned somewhere else, put this to `gradle.properties` as well:

`systemProp.dataPlatformRepository=</path/to/data-platform>`

In order to be sure that you have installed it right execute it like this:

```
[smikloso@localhost tests]$ docker -H tcp://127.0.0.1:2375 version
Client:
 Version:      1.10.1
 API version:  1.22
 Go version:   go1.5.3
 Git commit:   9e83765
 Built:        Thu Feb 11 19:18:46 2016
 OS/Arch:      linux/amd64

Server:
 Version:      1.10.1
 API version:  1.22
 Go version:   go1.5.3
 Git commit:   9e83765
 Built:        Thu Feb 11 19:18:46 2016
 OS/Arch:      linux/amd64

[smikloso@localhost tests]$ docker-machine version
docker-machine version 0.6.0, build e27fb87

[smikloso@localhost tests]$ docker-compose version
docker-compose version 1.6.0, build d99cad6
docker-py version: 1.7.0
CPython version: 2.7.9
OpenSSL version: OpenSSL 1.0.1e 11 Feb 2013 
```

## Structure of this project

```
[smikloso@localhost tests]$ tree -L 2
.
|-- build.gradle <- core Gradle file we will talk about later in this README file
|-- buildSrc
|   |-- build.gradle
|   `-- src <- source code for whole Snapshot Data Platform testing execution
|-- configuration
|   `-- tomcat <- configuration of Tomcat which is deployed to Tomcat Docker container 
|-- gradle
|   `-- wrapper
|-- gradlew <-- Unix Gradle wrapper in case your local Gradle installation is not of version 2.9
|-- gradlew.bat <-- Windows Gradle wrapper in case your local Gradle installation is not of version 2.9
|-- README.md <-- this file
|-- CHEATSHEET.md <-- handy reference file containing various questions and answers you could ask
`-- snapshot <-- working directory where this project caches artifacts and projects
   |-- cache
   |   `-- gradle <-- downloaded Gradle installation
   `-- workspace
       |-- data-platform <-- default location of cloned Data Platform repository when not specified in gradle.properties
       |-- dataplatformqa <-- location of cloned Data Platform QA repository
       |-- gradle <-- extracted Gradle installation for building Data Platform modules
       `-- reports <-- reports of every Docker container service (Tomcat, ActiveMQ, MariaDB, MongoDB) 
```

## Platform lifecycle execution

There are two lifecycle executions - starting of the whole Snapshot Data Platform and its stopping. By saying "starting", 
we mean that:

* in case running in `MACHINE` mode, Docker machine will be started.
    * in case it is already started, let's re-use it
* when firstly started, let's download all Docker images for containers to be run from private Docker repository either 
to local host in case we run in `HOST` mode or into Docker machine backed by VirtualBox in case we are in `MACHINE` mode.
* start containers one by one from downloaded / cached images

Only images which are missing will be downloaded and they will be reused when already present.

By saying "stopping" we mean that containers are just shutted down. Containers are stateless so once you 
start them again, they are totally clear so you will lose any data you have there previously.

If you are in `MACHINE` mode, stopping the platform does not mean that virtual machine itself will be stoped as well. 
 Only containers are stopped and machine is still running.

In case the versions of your local Gradle installation is different from version 2.9, you have to use Gradle wrapper 
 by executing `./gradlew` or its _bat_ equivalent when running at Windows. Otherwise you can call `gradle` or `gradle.bat` 
 directly.

## How do I start the platform?

`./gradlew test -PplatformStart --info`

or just 

`gradle test -PplatformStart --info`

in case your local Gradle installation is of version 2.9.

## How do I stop running platform?

`./gradlew test -PplatformStop --info`

or just 

`gradle test -PplatformStop --info`

You can omit `--info` switch but you would cut yourself from valuable logging messages which tells you what is 
exactly going on.

## How do I build whole Data platform?



## Snapshot Docker private image repository

Docker images for containers are hosted in private Docker repository at `docker.snapshot.travel:5000`. 
Internally, images are stored in Azure storage. Dockerfiles for images are stored in our _operations_ repository 
[here](https://bitbucket.org/bbox/operations) (https://bitbucket.org/bbox/operations).


## build.gradle walk-through

Testing project is Gradle project as any other. The difference is that it uses [Arquillian Spacelift Gradle](https://github.com/smiklosovic/arquillian-spacelift-gradle)
plugin which modifies default lifecycle of the Gradle test execution and introduces custom domain specific language 
which makes test execution a lot easier. I am the author of that plugin and core developer of the Arquillian platform 
as such on top of which this solution is implemented so in case there is some need to implement or modify existing 
plugin feel free to raise your requests.

The full description of possibilities of that plugin is out of scope of this documentation and only core concepts 
related to Snapshot Data Platform will be explained here in order to be able to implement your own test execution or 
to modify the existing one to your needs.

There are four core closures in top level `spacelift` closure in `build.gradle`:
 
 * configuration - configures whole test execution environment
 * installations - specifies what should be installed by test project when some test is about to be executed
 * tests - describes concrete tests to be executed
 * profiles - glues installations and tests together
 
Let's describe this on a simple test scenario. Let's say we want to test API tests against Configuration and Identity module. 
Firstly, We need to create couple of profiles for it. The final profile will be named `apiTests`.
 
```
profiles {
    apiTests(from: dataPlatformTest) {
        tests 'platformStart', 'apiTests', 'platformStop'
    }
    dataPlatformTest {
        enabledInstallations (dockerInstallations + serviceInstallations 
            + dataPlatformRepositories + 'gradle')
    }
}
```

You see that every profile closure has to consist of `tests` and `installations`. Our target `apiTests` closure 
defines that there will be three _tests_ executed, `platformStart`, `apiTests` and `platformStop`. `apiTests` profile 
inherits from `dataPlatfromTest` profile so everything what is specified in `dataPlatformTest` profile will hold for 
`apiTests` profile when not overridden there. In this situation, `apiTests` profile inherits `enabledInstallations` 
from `dataPlatformTest`. By specifying profile closures this way, we can inherit profiles from already specified ones 
with advantage.

Profile which does not explicitly or by inheritance specify `tests` and `enabledInstallations` is not valid.

What does it mean that some profile has enabled some installations? It means that it will look into top level 
`installations` closure and it will install every installation found in a list provided in `enabledInstallations` closure 
_in that order_. In other words, `enabledInstallations` closure expects list of strings as its values. Values of 
this list has to mach names of installations to be installed. In this example, we are returning `dockerInstallations`, 
`serviceInstallations`, `dataPlatformRepositories` and `gradle` installation. Concatenating them with `+` sign is just 
a Groovy way to concatenate lists or add elements to them.
 
 In turn, variables in `enabledInstallations` closure are defined in `configuration` block like this:
 
```
spacelift {
    configuration {
        dockerInstallations {
            converter BuiltinConfigurationItemConverters.getConverter(String[])
            value { ['docker', 'dockerMachine'] }
        }
        serviceInstallations {
            converter BuiltinConfigurationItemConverters.getConverter(String[])
            value { ['tomcat', 'mariadb', 'mongodb', 'activemq'] }
        }
        dataPlatformRepositories {
            converter BuiltinConfigurationItemConverters.getConverter(String[])
            value { [ "dataPlatformRepository", "dataPlatformQARepository" ] }
        }
    }
}
```
 
Do not mind these coverter fields. It is only the internal way how Spacelift Gradle plugin coverts what is in `value` to 
concrete types defined in converters. You can have confguration of not String or List but also `Class`, `File`, 
`Boolean` and so on.

Hence you see that we will have all these installations to be installed by our `apiTests`.
 
Installations are defined just as profiles are - in their own top level `installations` closure. In this model 
example, `installations` closure looks like this:

```
spacelift {

    final DataPlatformTestOrchestration platform = new DataPlatformTestOrchestration()

    installations {

        docker(from: Docker) {}

        dockerMachine(from: DockerMachine) {}

        tomcat(from: DockerService) { setup { platform.with(tomcat().init()) } }
    
        mariadb(from: DockerService) { setup { platform.with(mariadb().init()) } }
    
        mongodb(from: DockerService) { setup { platform.with(mongodb().init()) } }
    
        activemq(from: DockerService) { setup { platform.with(activemq().init()) } }

        dataPlatformRepository(from: GitBasedInstallation) {
            repository "git@bitbucket.org:bbox/data-platform.git"
            commit dataPlatformRepositoryCommit
            home "data-platform"
            skipFetch repositorySkipFetch
        }
    
        dataPlatformQARepository(from: GitBasedInstallation) {
            repository "git@bitbucket.org:bbox/dataplatformqa.git"
            commit dataPlatformQARepositoryCommit
            home "dataplatformqa"
            skipFetch repositorySkipFetch
        }
        
        gradle(from: GradleInstallation) {}
    }
}
```

What is going on here ... You see that installation definitions have the very same name as it is specified in 
`enabledInstallations` for respective `apiTests` profile so you can construct your test case how you want with 
services you want and with other _installations_ you want.

When it comes to Docker, there are two _installations_. `docker` and `dockerMachine`. `docker` installation 
prepares your environment for the test execution specific for Docker environment as such. On the other hand, `dockerMachine` 
installation is installed after `docker` one and it configures your environment to be fully prepared for the 
testing on Docker containers not running locally but in VirtualBox via `docker-machine`. 
This abstraction was already discussed previously in this README. It is important to say that when you do not set 
`dockerMode` to be `HOST`, `MACHINE` is taken as default so it means that your tests will be executed in Docker 
containers in VirtualBox via `docker-machine` instead of executing them locally against your containers running 
at your host. _In case you run on Windows or Mac OS X you have to run in `MACHINE` mode (which is default) because 
these plaforms do not have native Docker containers available as Linux has._ In case you are at Linux, you can choose 
whatever approach you like. From the test execution perspective both approaches are just same and it is completely 
transparent to use whatever you want. When `dockerMode` is `HOST`, `dockerMachine` installation is just skipped.

Docker-specific installations are followed by service installations. These installations are describing what 
services your test will need - e.g in our case, `DataPlatformTestOrchestration` platform is set up with `tomcat` container 
(or in other words with all services named equally to what is specified in `enabledInstallations` in `apiTests` profile). It 
almost reads as plain English. Where is `tomcat()` method from? It is defined as static import from `DockerServiceFactory` 
from `docker-manager` project in this repository. By _installing_ these services, we populate underlying 
`DataPlatformTestOrchestration` with services which are eventually started underneath by extracted lifecycle 
of Arquillian Cube extension which provides abstraction for Docker containers and it was specially extracted and incorporated 
into this project for Snapshot Data Platform needs. This whole implementation is done in `docker-manager` project.

Next installations deals with repositories to be cloned. When you want to test Data Platform, you have to obviously 
provide sources to be used so wars can be built from them. When you want to execute tests against these built wars, 
you have to get these test sources as well.

For these purposes, `GitBasedInstallation` is used. It is just abstraction which automatically clones repositories from 
BitBucket into `workspace` into `home` directory specific for every repository. Once repository is already cloned, it is 
not cloned again but it is reused instead of it. It will automatically fetch all changes in upstream repositories 
so you can be pretty much sure that you are testing against the very latest version of Data Platform. Default branch to 
checkout is `origin/master` so if you are developing some feature and you have pushed it to BitBucket to branch  
`my-feature`, you have to specify `commit` to checkout to `origin/my-feature` - `GitBasedInstallation` makes sure that 
it is fetched and checkout out before the test execution is about to be carried out. The ultimate way how to specify what 
will be checked out is to use Git SHA commit hash instead of branches.

The very last installation is Gradle installation. Gradle of version 2.9 is downloaded from the Internet and it is 
 cached in `workspace/cache` for subsequent usage so we do not depend on any other, possibly locally installed, Gradle 
 installation but we are using pristine clear Gradle installation which builds modules of Data Platform on its own so 
 we are effectively executing Gradle process inside already running Gradle of test project itself.

The first _test_ is `platformStart`. This just setups whole environment - starts virtual machine and containers if not 
already started. It is done by this test closure:

```
spacelift {
  tests {
    platformStart(from: PlatformLifecycle) {
      with platform
      init {
        new TomcatConfigurationDeployer(dockerMachine).deploy()
      }
      teardown false
    }
  }
}
```

`PlatformLifecycle` has all logic embedded in it so it knows how to start containers. `init` closure copies configuration 
file `applicationCommon.properties` to Tomcat container so once some wars are deployed, properties from that 
file are used during deployment. It is located in `configuration/tomcat` directory. Additionally, we just want to 
start that platform and keep it running so `teardown` is set to false. 

`platformStop` has it other way around - its `setup` closure resolves to `false` and `teardown` is set to `true`.

_When execution enters following apiTests closure, Docker containers, either running natively or in VirtualBox, are fully up and running and 
you can already connect to them._

Finally, we are going to desribe tests itself.

```
spacelift {
  
  tests {

    reportableTest(from: DataPlatformTest) {
      report {
        new DataPlatformLogReporter(platform, serviceInstallations).report()
      }
    }

    apiTests(inherits: reportableTest) {

      beforeTest {

        new DataPlatformBuildExecutor(workspace)
          .build(DataPlatformModule.CONFIGURATION, DataPlatformModule.IDENTITY)
          .execute(forceDataPlatformBuild)

        new MariaDBInitializer(workspace, platform)
          .init(DataPlatformModule.IDENTITY)
          .execute()

        new TomcatModuleDeployer(workspace, platform)
          .deploy(DataPlatformModule.CONFIGURATION, DataPlatformModule.IDENTITY)
          .execute()
      }

      execute {

        Spacelift.task("gradle")
          .parameters("--project-dir", new File(workspace, "dataplatformqa/dp-api-tests").absolutePath)
          .parameter("-Ddp.properties=${PropertyResolver.resolveApiTestsDpProperties()}")
          .parameter("-Dtest.single=RunConfigurationTests")
          .parameter("test")
          .execute().await()

        Spacelift.task("gradle")
          .parameters("--project-dir", new File(workspace, "dataplatformqa/dp-api-tests").absolutePath)
          .parameter("aggregate")
          .execute().await()
      }
    }
  }
}
```

The very first test is `reportableTest` which tells you it inherits from `DataPlatformTest` and it does not do 
anything at all but when your test is finished, it gathers logs from Docker containers to `spacelift/workspace/reports` 
directory. This directory contains all logs from every respective Docker container (service) your tests run against. In 
our scenario, `logs` directory from Tomcat will be fetched from Docker container, ActiveMQ log file will be fetched as well,
MariaDB logs and MongoDB logs will be saved to workspace too. This is what you get:

```
[smikloso@localhost tests]$ tree snapshot/workspace/reports/
snapshot/workspace/reports/
`-- machine
    `-- default
        |-- activemq
        |   |-- activemq.log
        |   `-- audit.log
        |-- mariadb
        |   |-- localhost.localdomain.err
        |   `-- localhost.localdomain.log
        |-- mongodb
        |   `-- server.log
        `-- tomcat
            `-- logs
                |-- catalina.2016-02-29.log
                |-- catalina.out
                |-- host-manager.2016-02-29.log
                |-- localhost.2016-02-29.log
                |-- localhost_access_log.2016-02-29.txt
                `-- manager.2016-02-29.log

```

In case your `dockerMode` is `HOST` instead of `MACHINE`, it would be placed under `reports/host/$container` directory. 

`apiTests` inherits from `reportableTest` so our concrete tests will be reported as well automatically. In general, test
has these lifecycle closures:

```
myTest {
    data {
        // This closure has to return List
        // if this List is empty, beforeTest, execute and afterTest
        // closures will be resolved only once
    }
    beforeSuite {
        // This closure resolves only once in the whole test execution
        // regardless of what is in data closure
    }
    beforeTest { data -> 
        // This closure resolves for every element of data list from data closure
        // data variable is present in this beforeTest closure for further usage
    }
    execute { data ->
        // This closure resolves for every element of data or once if no data are specified
    }
    afterTest { data ->
        // same as beforeTest closure
    }
    afterSuite {
        // same as beforeSuite closure
    }
    report {
        // by default resolved in afterSuite closure when not overriden
    }
}
```

By specifying `data` closure, it is able to repeatedly execute tests with some variables which change. By this 
way it is possible to execute tests which will run in some kind of matrix so you can simulate matrix test jobs, e.g.
against various combinations of operating systems and browsers if you ever hit that requirement. You can specify 
not only List but Map as well - in general whatever is iterable so you would get tuple instead of element to use in your 
closures.

In our simple scenario, the above _iterable_ feature is not used. `apiTests` inherits from `DataPlatformTest` so 
`beforeSuite` is dummy and `afterSuite` makes reports by `report` closure.
 
`beforeTest` closure does couple of things. The very first thing to be done is to build wars to be deployed to already 
started container where Tomcat instance is running. This is done like this:

```
new DataPlatformBuildExecutor(workspace)
    .build(DataPlatformModule.CONFIGURATION, DataPlatformModule.IDENTITY)
    .execute(forceDataPlatformBuild)
```

`DataPlatformBuildExecutor` builds modules which get evenutally deployed to Tomcat. In this case we are building 
`CONFIGURATION` and `IDENTITY` modules. This module abstraction encapsulates all information needed for build executor 
to build wars from sources which are located in a directory where Data Platform repository was cloned and checked out to 
some branch - as you remember, we have specified `GitBasedInstallation` for it.

Build executor is smart about what to build and what not. When `forceDataPlatformBuild` resolves to `false` (this is just 
configuration property which can be overriden on the command line when executing tests), wars which are already built - 
they are located in `build/libs` directory in Data Platform repository - _are not built again_ so this makes deployment 
and test execution _way more faster_. On the other hand if you want to force the building, even wars are already present 
(but e.g. your sources could change in the meanwhile), call `execute` method with `true` argument.

Building of modules is done by Gradle which has been installed by our `gradle` installation. Build executor builds 
only specified modules and nothing more. Gradle is smart enough to automatically build modules which module under 
build is depending on.

You could build whole Data Platform project by using build executor in this way:

```
new DataPlatformBuildExecutor(workspace)
    .build(DataPlatformModules.ALL).execute(forceDataPlatformBuild)
```

or you could build only modules which are intended to be deployed to Tomcat like this:

```
new DataPlatformBuildExecutor(workspace)
    .build(DataPlatformModules.TOMCAT_MODULES).execute(forceDataPlatformBuild)
```

Check [DataPlatformModules](buildSrc/src/main/groovy/travel/snapshot/qa/test/execution/dataplatform/DataPlatformModules.groovy) for further information.

It is obvious that modules to-be-deployed to Tomcat have to have fully initialized database before. This is done by next 
execution in `beforeTest` closure. In our scenario, Configuration module uses MongoDB so no initialization is needed but when it 
comes to Identity module, we have to set it up like this:

```
new MariaDBInitializer(workspace, platform)
    .init(DataPlatformModule.IDENTITY)
    .execute()
```

Internally, it executes Flyway migration against already started MariaDB container. MariaDBInitializer is smart - 
it knows what database initialization is used for every DataPlatform module so right Flyway migrations are executed 
against the database for every Data Platform module. Flyway migrations are executed everytime in `beforeTest`, it is not 
a problem to execute them mutiple times because the seconds run will just not migrate anything.

When modules are built and databases are initialized, we can approach to module deployment. That is done like this in 
`beforeTest` closure:

```
new TomcatModuleDeployer(workspace, platform)
    .deploy(DataPlatformModule.CONFIGURATION, DataPlatformModule.IDENTITY)
    .execute()
```

Tomcat module deployer just deploys modules previously built by build executor which are located in 
cloned Data Platform repository in `snapshot/workspace/data-platform` directory.

Finally, test excution is carried out. In this example, we are using `gradle` task which internally 
spawns Gradle binary previously installed by `gradle` installation and we are executing some API tests against 
services deployed in Docker. It is easy to guess what it does so it does not need any further explanation.

## Test re-execution

Let's imagine that you are going to execute these tests again. By default, no module will be built because they 
are already present, Flyway migrations are effectively skipped as well. The only thing which can slows your tests 
is the redeployemnt of the wars to already running Tomcat.

For this reason there are so called _Tomcat deployment strategies_. There are three Tomcat deployment strategies:

* `DEPLOYORFAIL` - deploy or fail - If module is already deployed, whole deployment fails.
* `DEPLOYORSKIP` - deploy or skip - If such module is already deployed, that deployment is skipped.
* `DEPLOYORREDEPLOY` -deploy or redeploy - If such module is already deployed - it is undeployed and deployed again - otherwise it is just deployed.

Default deployment strategy is `DEPLOYORREDEPLOY` so all already deployed modules to be deployed are undeloyed and deployed again.
You can override this strategy by setting `tomcatDeploymentStrategy` variable with value of strategy you want.

When it comes to test reexecution on Docker container level, you have various options as well. These options are dealing with 
`connectionMode` and are like follows:

* `STARTANDSTOP` - start and stop - This is default connection mode if not set otherwise. Simply starts and stops all Docker Containers. 
If a container is already running, e.g. from previous test run, an exception is thrown and whole test fails.
* `STARTORCONNECT` - start or connect - Tries to bypass the creation of a container with the same name as some already running container 
and if it is the case it does not stop it at the end. But if container is not already running, it will be started and stopped at the end of the test execution.
* `STARTORCONNECTANDLEAVE` - start or connect and leave - the same as _start or connect_ but it will not be stopped at the end of the execution.

Default `connectionMode` is `STARTANDSTOP`.

It is up to you what connection mode you prefer. I am fan of the last one because it will just keep containers running 
and they are not started every time again. The starting of 4 containers takes about 15-20 seconds so you can save some 
time here. On the other hand your containers are not _clean_ so when you need absolutely clean environment, be sure to 
use _start and stop_ connection mode.

I am also using `DEPLOYORSKIP` deployment strategy because I do not need to redeploy modules again and again when 
they have not changed at all so I am saving significant amout of time as well.

From just said, you see that the execution takes about 1-2-3 minutes when you are deploying and starting everything 
from scratch and building it as well but it takes couple of seconds to get to test execution in case all is skipped 
and reused. It is only the matter of what you prefer in some particular case.

## Test execution modes

It is possible to deploy artifacts directly to Docker containers running in Docker machine by remote Tomcat run configuration.

In order to do this, you have to start Gradle script in so called _test execution mode._ There are two modes:

* `TEST` - this execution mode is default. When you use this mode, you will not be able to deploy directly from IDEA.
* `DEVELOPMENT` - this execution mode enables you to use remote Tomcat run configuration so you can deploy there artifacts 
directly from IDE. Under the hood, this mode mounts directory `/opt/tomcat/webapps` from Docker machine to `/opt/tomcat/webapps` 
on Tomcat container so when some wAR is copied from local host `/opt/tomcat/webapps` in Docker machine, it will be seen by Tomcat 
container which starts to deploy that WAR. If you use this test execution mode, it means that you will not have `/manager` deployment 
in Tomcat so you will not be able to deploy manually via web console because when you mount these directories, its content will be overlayed 
by directory in Docker machine which is empty so Tomcat will see it as empty as well - hence no `/manager` will be deployed.

You set this on the command line like this: `-DtestExecutionMode=DEVELOPMENT\TEST`. `TEST` mode is default.
