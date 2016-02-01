# Docker manager

This project tries to achieve the encapsulation of [Arquillian Cube](https://github.com/arquillian/arquillian-cube) 
project into very simple wrappers so you can abstract the whole Snapshot DataPlatform to Java oneliners.

Another motivation behind this project is to use internals of Arquillian Cube without the need to use its JUnit runner 
so we can embed the initialization of Snapshot DataPlatform services virtually anywhere where JVM is present.

Our DP API tests are using Cucumber runner with Serenity and it is not possible to use Arquillian JUnit runner with it.
Our performace tests are based on Gatling framework which is written in Scala and it does not use any JUnit runner at 
all so again - we can not use Arquillian Cube as-is hence there is a need for the extraction. Thirdly, it is possible, 
however not planned yet, to incorporate this Docker abstraction into DP tests themselves if we are willing to do so.

This project will be mainly used in dedicated test project which will handle the execution of DP API, performance tests, 
possibly UI tests and so on.

## How it works?

Snapshot Docker manager is based on _services_. For example, Snapshot DataPlatform depends on Tomcat container, 
MariaDB database and Mongo database. In a nutshell, all you would need to do to start these Docker containers is shown 
[here](src/test/java/travel/snapshot/qa/docker/DataPlatformOrchestrationTestCase.java) and it is only the matter 
of the implementation to provide Docker abstractions to other services - e.g. in the future we can containerize 
integrations services to Docker image (already done by Juraj) and manage it from Java as well.

## How do you check if some service is running?

This is done by connection checks. The reason behind this is rather complicated but to put is simply, Arquillian Cube 
is the one who manages these containers in the end. When some container is started, e.g. MariaDB, the fact that 
MariaDB is started, from Arquillian Cube point of view, is deducted from various strategies, e.g from 
the output of the `ss` command which is executed in container. This `ss` command checks if an exposed port 
is in its output and when it is, Arquillian Cube in turn assumes that service is started.

But this technique is quite tricky to use and unreliable because, as it turns out, the mere fact that there is MariaDB 
port 3306 in container's `ss` output does not say anything about the fact that a service which is going to actually bind 
there is really up and fully running and prepared for the subsequent interaction. So to put is shortly, Arquillian Cube 
only checks if exposed port is there. _It does not check whether service is initialized fully_.

The check for Tomcat is done by repeatedly trying to list its deployments. The check for MariaDB is done by 
repeatedly trying to make `java.sql.Connection`. The check for Mongo is done by trying to list collections on a 
configured database.

## Why do you not use connection checking capabilities of Arquillian Cube itself?

See the above paragraph. I have asked [this](https://github.com/arquillian/arquillian-cube/issues/254) in upstream.

## What do I need to use this?

Put this dependency to your project and provide `arquillian.xml` file to test resources. The example of `arquillian.xml` 
file can be seen [here](src/test/resources/arquillian.xml).

## Why do I need to use arquillian.xml file?

Because internals of Arquillian Cube depends on it. In case Docker image of some service is not stored locally, 
it will automatically fetch that image from our private Docker image registry at `docker.snapshot.travel:5000`. Even 
better, it is possible to dynamically build images, providing some `Dockerfile` before container based on that image 
is started so we have a lot of room to improve the testing experience in the long run when there is a need for it.

## Can I use this in connection with Docker machine?

Yes! You can use it with Docker machine as well. You have to start it with config file like [here](src/test/resources/arquillian-docker-machine.xml).
Note there is another `serverUri` address. That `dockerHost` string will be automatically evaluated to the right 
remote Docker daemon, effectively sitting in your VirtualBox machine of name `machineName` (`dev` in out example). So 
you have to have that VirtualBox started beforehand.

## How do I create a VirtualBox machine?

Be sure you have Docker machine toolchain installed. Execute this once done:

    docker-machine create --driver=virtualbox --virtualbox-memory "3072" --virtualbox-cpu-count "2" dev

Adjust to your needs. The above command creates VirtualBox instance with 3GB of memory and 2 CPUs. After it is created, start it like

    docker-machine start dev

Once started, it is recommended to evaluate these settings in your shell by executing

    eval "$(docker-machine env dev)"

From now on, while still in that shell, if you invoke `docker` command, it will in fact talk to the remote Docker daemon 
in started VirtualBox machine instead of the Docker daemon at your localhost.

## Can I use this with my docker-compose.yml?

No because you are not able to use / modify await strategies (seen in `arquillian.xml`) in `docker-compose.yml`. 
Among other waiting strategies, `ss` command output strategy is going to be used and you do not have the 
possibility to change its configuration in `docker-compose.yml` so Arquillian Cube terminates prematurely because of the 
inability to see these ports because the default timeouts are just too low. Remember - these checks are checking that 
ports are indeed exported and bound to the host but it does not mean that the service behind it is really ready.

## How do I set IP address of the Docker host or service IP?

You have three options. The first one is to set it manually via each service configuration builder. The second 
way is to set environment property of name `DOCKER_HOST`. This environment property is set when you evaluate the 
environment of `docker-machine` by `eval "$(docker-machine env dev)"` where `dev` is your machine name. The third 
option is to set system property of name `docker.host` which eventually overrides the environment one when set.

When either of properties are set, you can safely create service configuration without explicitly specifying what 
the binding IP address should be because by default, these properties are resolved underneath.

## How do I run tests?

In case of tests against Docker machine, you have to create Docker machine of name `dev` and start it beforehand and execute:

    gradle clean test -Ddocker.mode=machine -Ddocker.machine=yourMachine

In case you want tests to be executed against Docker containers which runs at your host, execute them like:

    gradle clean test -Ddocker.mode=host -Ddocker.host=127.0.0.1

By default, `docker.mode` has value `host` and `docker.machine` has value `default`.

Be sure that you have Tomcat instance installed locally and `CATALINA_HOME` and `CATALINA_BASE` are set correctly and 
you can login in there with `admin:admin` and there are only two deployments - manager and manager-host.

## ActiveMQ manager

[ActiveMQManager](src/main/java/travel/snapshot/qa/manager/activemq/api/ActiveMQManager.java) gives you the possibility 
to construct and send messages to started ActiveMQ container. You get the instance of ActiveMQManager by instantiating it like:

    ActiveMQManager activeMQManager = new ActiveMQManagerImpl(new ActiveMQManagerConfiguration.Builder().build());

or via orchestration API like:

    DataPlatformOrchestration ORCHESTRATION = new DataPlatformOrchestration();
    ORCHESTRATION.with(activemq()).startServices();
    ActiveMQManager activeMQManager = ORCHESTRATION.getActiveMQDockerManager().getServiceManager();

## MariaDB manager

[MariaDBManager](src/main/java/travel/snapshot/qa/manager/mariadb/api/MariaDBManager.java) gives you the possibility to 
interact with MariaDB database, execute some SQL script or do Flyway migrations against some database. You get the reference to it by:

    final MariaDBManager mariaDBManager = new MariaDBManagerImpl(new MariaDBManagerConfiguration.Builder().build());

or via orchestration API like:

    DataPlatformOrchestration ORCHESTRATION = new DataPlatformOrchestration();
    ORCHESTRATION.with(mariadb()).startServices();
    MariaDBManager mariaDBManager = ORCHESTRATION.getMariaDBDockerManager().getServiceManager();

## MongoDB manager

[MongoDBManager](src/main/java/travel/snapshot/qa/manager/mongodb/api/MongoDBManager.java) gives you the possibility to 
interact with MongoDB database. You can get MongoClient from official MongoDB driver and interact with the database afterwards.

    final MongoDBManager mongoDBManager = new MongoDBManagerImpl(new MongoDBManagerConfiguration.Builder().build());

or via orchestration API like:

    DataPlatformOrchestration ORCHESTRATION = new DataPlatformOrchestration();
    ORCHESTRATION.with(mongodb()).startServices();
    MongoDBManager mongoDBManager = ORCHESTRATION.getMongoDockerManager().getServiceManager();

You get methods you put to `with` method on the orchestration object by importing statically methods in 
[DockerServiceFactory](src/main/java/travel/snapshot/qa/docker/DockerServiceFactory.java)

It is important to say that you can use these managers without dockerization by the first approach. All it takes to 
talk to some existing service, e.g. started at your localhost, is to instantiate managers like it is shown in the 
first examples above. You would only configure ports and hosts where it connects and how.

In case you want to use Docker and orchestration API, you have to have `arquillian.xml` on classpath. Example 
how you can try it with Docker machine is [here](src/test/resources/arquillian-docker-machine.xml) and example 
without Docker machine is [here](src/test/resources/arquillian.xml).

## Tomcat manager

Tomcat manager is a very simple tool which wraps whole Tomcat container lifecycle execution and deployment into one-liners.

### Starting of a container

Starting of a container is done as follows. You can omit configuration parameter for TomcatStarter task
if you are satisfied by defaults. Roughly speaking it starts Tomcat container located in CATALINA_HOME. You can 
override this and many other options by provided configuration but you are normally good to go without it.

Default admin username and password is admin/admin. You can override this by respective setters on a configration object. This has to be set correctly.

    TomcatManagerConfiguration configuration = new TomcatManagerConfiguration();
    TomcatManager tomcatManager = Spacelift.task(configuration, TomcatStarter.class).execute().await();

### Stopping of a container

Stopping of a container is also just a one-liner. It is Spacelift task which takes TomcatManager 
returned by TomcatStarter as a parameter. After this is executed, underlying Tomcat container is effectively stopped.

    Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();

### Interacting with remote container

It is possible to interact with remote container which is running e.g. in Docker. In that case, `CATALINA_HOME` 
and `CATALINA_BASE` environment properties do not need to be set locally so the respective check for their existence 
is not needed. In that case, it is needed to call `remote()` method on a configuration builder like this:

    TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().remote().build()

Such built configuration has to be passed to `TomcatManager` instance directly as it is not needed to start container 
with the `TomcatStarter` task.

### Chaining starting and stopping

Due to chaining capabilities of Spacelift, you can do this as well. What it does is that it starts Tomcat 
and when it detects it is started, it is in turn stopped.

    Spacelift.task(TomcatStarter.class).then(TomcatStopper.class).execute().await().

### Deployment of an archive

Deployment is done on TomcatManager object returned by starter. You could instantiate whole new TomcatManager without getting it from
TomcatStarter (having the same underlying configuration) and you can deploy and undeploy the same way.

    manager.deploy(archive);

### Undeployment of an archive

    manager.undeploy(archive);

### Deployment of  wars

WARs to deploy are created / imported by ShrinkWrap. For example for the deployment of IdentityModule, do this:

    Archive<WebArchive> archive = ShrinkWrap.createFromZipFile(WebArchive.class, new File("IdentityModule-1.0.war"));
    manager.deploy(archive);

    // here you have started tomcat with deployed IdentityModule

    manager.undeploy(archive);

You are not forced to create ShrinkWrap archive yourself. It is possible to put full archive file name to `deploy`
method as well.

    String archivePath = "/some/path/to/archive.war"
    manager.deploy(archivePath);
    manager.undeploy(archivePath);

### Starting, stopping and reloading of a deployment

Deployed archive can be stopped and started again. This is done as follows:

    manager.stopDeployment("deploymentName");

    // here deployment is stopped

    manager.startDeployment("deploymentName");

    // here deployment is started.

    manager.reload("deploymentName");

    // here deployment is reloaded

You do not need to enter deployment name which starts with a slash. It will be automatically added. Deployment name 
is simply a name of archive without file type suffix.

### Container running check

It is possible to ask if a container which is backed by some configuration is running or not. In the background it
just tries to list all deployments and if it fails to connect to container, it is considered stopped.

    boolean isRunning = manager.isRunning();

### Deployment listing

It is possible to query Tomcat container about its deployments like this:

    Deployments deployments = manager.listDeployments();

More to it, you can obtain only deployments with some deployemnt state (running / stopped) like this:

    Deployments runningDeployments = manager.listDeployments(DeploymentState.RUNNING);

Returned `Deployments` object can be queried for other information like this:

    boolean isDeploymentRunning = runningDeployments.contains("someDeployment);

    DeploymentRecord deployment = runningDeployments.getDeployment("someDeployment");
    String context = deployment.getContextPath();
    DeploymentState state = deployment.getDeploymentState();
    int sessions = deployment.getActiveSessions();

### TCP and UDP server connection checking

Imagine you have started Docker containers by `docker-compose` and your services are about to start. Are you 
sure that when you proceed to the interaction with dockerized Tomcat, it is fully up and running, listening to the 
client connections. You have to block your execution logic until you can talk to such container or service successfully.

In order to do so, you can use this API:

    new ConnectionCheck.Builder()
        .host(HOST)
        .port(SERVER_PORT)
        .protocol(Protocol.TCP)
        .timeout(10)
        .build()
        .execute();

When timeout is not specified, it periodically checks up to 60 seconds. Port is by default `8080`, host is by default 
`127.0.0.1`. When this call returns successfully, you can be sure that underlying service is up and running.

You can use UDP protocol as well by `Protocol.UDP`.

### How to start tests?

Tests are executed like you are used to:

    gradle clean test
    
You have to have container installed locally and you have to have it without any deployments (except manager wars).
In case you are going to execute tests locally, you have to set environment properties `CATALINA_HOME` 
(and `CATALINA_BASE` if it differs from `CATALINA_HOME`) which point to the directory where your Tomcat container is located.

It is possible to test [TomcatDeploymentTestCase](src/test/java/travel/snapshot/qa/manager/tomcat/TomcatDeploymentTestCase.java)
against remote instance, for example against Docker. You have to execute that test like this:

    gradle clean build -Dremote=true -DremoteHost=<ip of container> -Dtest.single=TomcatDeploymentTestCase test

Be sure your container is started (locally or in Docker) in such way that it is possible to reach it from your host at `127.0.0.1:8080`.
In case you do not have ports forwarded to your localhost address of `127.0.0.1`, you would need to specify `remoteHost` 
system property.
