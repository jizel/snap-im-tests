# Tomcat manager

Tomcat manager is a very simple tool which wraps whole Tomcat container lifecycle execution and deployment into one-liners.

## Starting of a container

Starting of a container is done as follows. You can omit configuration parameter for TomcatStarter task
if you are satisfied by defaults. Roughly speaking it starts Tomcat container located in CATALINA_HOME. You can 
override this and many other options by provided configuration but you are normally good to go without it.

Default admin username and password is admin/admin. You can override this by respective setters on a configration object. This has to be set correctly.

    TomcatManagerConfiguration configuration = new TomcatManagerConfiguration();
    TomcatManager tomcatManager = Spacelift.task(configuration, TomcatStarter.class).execute().await();

## Stopping of a container

Stopping of a container is also just a one-liner. It is Spacelift task which takes TomcatManager 
returned by TomcatStarter as a parameter. After this is executed, underlying Tomcat container is effectively stopped.

    Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();

## Interacting with remote container

It is possible to interact with remote container which is running e.g. in Docker. In that case, `CATALINA_HOME` 
and `CATALINA_BASE` environment properties do not need to be set locally so the respective check for their existence 
is not needed. In that case, it is needed to call `remote()` method on a configuration builder like this:

    TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().remote().build()

Such built configuration has to be passed to `TomcatManager` instance directly as it is not needed to start container 
with the `TomcatStarter` task.

## Chaining starting and stopping

Due to chaining capabilities of Spacelift, you can do this as well. What it does is that it starts Tomcat 
and when it detects it is started, it is in turn stopped.

    Spacelift.task(TomcatStarter.class).then(TomcatStopper.class).execute().await().

## Deployment of an archive

Deployment is done on TomcatManager object returned by starter. You could instantiate whole new TomcatManager without getting it from
TomcatStarter (having the same underlying configuration) and you can deploy and undeploy the same way.

    manager.deploy(archive);

## Undeployment of an archive

    manager.undeploy(archive);

## Deployment of  wars

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

## Starting, stopping and reloading of a deployment

Deployed archive can be stopped and started again. This is done as follows:

    manager.stopDeployment("deploymentName");

    // here deployment is stopped

    manager.startDeployment("deploymentName");

    // here deployment is started.

    manager.reload("deploymentName");

    // here deployment is reloaded

You do not need to enter deployment name which starts with a slash. It will be automatically added. Deployment name 
is simply a name of archive without file type suffix.

## Container running check

It is possible to ask if a container which is backed by some configuration is running or not. In the background it
just tries to list all deployments and if it fails to connect to container, it is considered stopped.

    boolean isRunning = manager.isRunning();

## Deployment listing

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

## TCP and UDP server connection checking

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

## How to start tests?

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