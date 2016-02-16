# Snapshot Data Platfrom test project cheat sheet

This serves as a quick reference how to use test project.

### Give me some examples right of the bat!

`gradle test -PplatformStart` - starts containers in VirtualBox named `default`.

`gradle test -PplatformStop` - stops containers but keeps VirtualBox machine running

`gradle test -PplatformBuild` - clones data-platform repository, checkouts to `origin/master` and builds all modules.

`gradle test -PplatformBuild -DdataPlatformRepositoryCommit=origin/someBranch` - builds data-platform of some branch

`gradle test -PplatformBuild -DdataPlatformRepositoryCommit=<SHA COMMIT>` - builds commit of a repository

`gradle test -PapiTests` - as of now, starts VirtualBox of name `default` or creates such VirtualBox machine when not found, 
downloads Docker service images when not present on that machine, copies `applicationCommon.properties` from `configuration/tomcat` 
to Docker machine so Tomcat will be started with it, starts Tomcat, MariaDB, ActiveMQ and MongoDB containers, clones 
data-platform repository when not previously cloned, fetches latest changes in `origin/master` when other branch is not specified, 
builds Configuration and Identity modules when not already built, deploys these wars to running Tomcat and executes tests from cloned API 
tests from dataplatformaqa repository, saves logs from conainers to `snapshot/workspace/reports/machine/default` and stops 
all containers and kees that Docker machine / VirtualBox machine running.

`gradle test -PapiTests -DconnectionMode=STARTORCONNECTANDLEAVE` - the same as above but containers will not be 
stopped - they will be running even test execution as such is finished so you can connect to them again in the 
next test execution run.

`gradle test -PapiTests -DconnectionMode=STARTORCONNECTANDLEAVE -DdockerMode=HOST` - starts these containers 
not at VirtualBox but locally in case you are running at Linux. It is not possible on Windows nor on Mac OS - for that 
reason VirtualBox is chosen as the default Docker mode.

`gradle test -PapiTests -DconnectionMode=STARTORCONNECTANDLEAVE -DtomcatDeploymentStrategy=DEPLOYORSKIP` - connects to 
already running containers or starts them when not already started and keep them running - if they are already started 
and there are deployments on it - skip that deployment process.

```
gradle test -PapiTests \
    -DconnectionMode=STARTORCONNECTANDLEAVE \ 
    -DtomcatDeploymentStrategy=DEPLOYORREDEPLOY
```

same as above but when deployments are already deployed, they will be firstly undeployed and deployed again. 
This deployment strategy is the default one when not specified.

```
gradle test -PapiTests \
    -DconnectionMode=STARTORCONNECTANDLEAVE \
    -DtomcatDeploymentStrategy=DEPLOYORREDEPLOY \
    -DdataPlatformRepositoryCommit=origin/myBranch
```

same as above but modules for other branch will be built.

```
gradle test -PapiTests \
    -DconnectionMode=STARTORCONNECTANDLEAVE \
    -DtomcatDeploymentStrategy=DEPLOYORREDEPLOY \
    -DdataPlatformRepositoryCommit=origin/myBranch \
    -DforceDataPlatformBuild=true
```

Same as above but in case wars are already built, it will be rebuilt.

### I want to see a lot of output in the console so I can see what is going on

You want to add `--info` switch to every Gradle command below. This switch is very helpful and it is adviced to be used.
It e.g. shows you the output of the Tomcat container while modules are being deployed so you can follow it and debug 
it when the deployment is errorneous. You basically want to have `--info` turned on. There are `--stacktrace` and 
`--debug` switches for even more output. Rule of thumb is to use `--info`.

### I want to start all services from scratch and keep them running

`gradle test -PplatformStart`

### Once my containers are running and I started them by above command, after I am done, I want to stop them

`gradle test -PplatformStop`

### How do I build whole Snapshot Data Platform project?

`gradle test -PplatformBuild`

When project is not already downloaded, it is cloned into `snapshot/workspace/data-platform` directory and build aftewards.
In case you do not specify branch to build, `origin/master` is build.

### How do I specify what branch of Snapshot Data platform to use during tests?

By setting `-DdataPlatformRepositoryCommit=myBranch`. When not set, `origin/master` is used.

### I want to keep my containers running when I execute tests.

You have to use so called _connection mode_. There are three options:

* `STARTANDSTOP`

    start and stop - This is default connection mode if not set otherwise. Simply starts and stops all Docker Containers. 
    If a container is already running, e.g. from previous test run, an exception is thrown and whole test fails.
* `STARTORCONNECT`

    start or connect - Tries to bypass the creation of a container with the same name 
    as some already running container and if it is the case it does not stop it at the end. But if container is not already running, 
    it will be started and stopped at the end of the test execution.
* `STARTORCONNECTANDLEAVE`

    start or connect and leave - the same as _start or connect_ but it will not be stopped at the end of the execution.

Default `connectionMode` is `STARTANDSTOP`.

It is up to you what connection mode you prefer. I am fan of the last one because it will just keep containers running 
and they are not started every time again. The starting of 4 containers takes about 15-20 seconds so you can save some 
time here. On the other hand your containers are not _clean_ so when you need absolutely clean environment, be sure to 
use _start and stop_ connection mode.

You set this by `-DconnectionMode=<selected mode>`

### What happens when I have executed some tests and I used `STARTORCONNECTANDLEAVE` connection mode and I am going execute tests again? What about deployments?

Good question. You have three options. It is called `tomcatDeploymentStrategy`.

* `DEPLOYORFAIL`

    deploy or fail - If module is already deployed, whole deployment fails.
* `DEPLOYORSKIP`

    deploy or skip - If such module is already deployed, that deployment is skipped.
* `DEPLOYORREDEPLOY`

    deploy or redeploy - If such module is already deployed - it is undeployed and deployed again - otherwise it is just deployed.

Default deployment strategy is `DEPLOYORREDEPLOY` so all already deployed modules to be deployed are undeloyed and deployed again.
You can override this strategy by setting `tomcatDeploymentStrategy` variable with value of strategy you want.

Example: `-DtomcatDeploymentStrategy=<selected strategy>`

### What if I am testing from the origin/master branch and that branch was modified in the meanwhile in upstream?
 
Everytime GitBasedInstallation is resolved, it automatically fetches all changes from upstream repositories. It 
basically executes `git fetch --all` every time your are totally up-to-date.

In case you do not want to fetch it, you can skip this by specifying `-DrepositorySkipFetch=true`.

### Will be modules built again when they are already built?

No. Every module to be built will be skipped when its war file already exists. In case you want to force this, 
you have to set this switch `-DforceDataPlatformBuild=true`.

### I have started containers and I want to setup my environment e.g. in IDE to connect to them and use them.
 
Tomcat is at IP:8080/manager. MySQL on port 3306. MongoDB at 27017. ActiveMQ console on 8161/admin. IP address of 
containers running in VirtualBox are all same - they are in VirtualBox so they appear as one service 
from your local host. You get IP of machine by `docker-machine ip <machine name>`. In case you run them locally, 
you have to inspect them.

``docker inspect --format '{{ .NetworkSettings.IPAddress }}' <container id>``

Credentials for Tomcat manager are admin:admin. MySQL credentials are root:root, ActiveMQ's credentials are admin:admin.

### Do I have to inspect IPs manually?

No. When you use `--info` switch and you look closely in that output, there are IP addresses of the Docker containers 
and Docker machine written out to you, it looks like this:

```
========================================
docker-machine: 192.168.99.100
You are running in MACHINE mode, IP address of the Docker machine is 192.168.99.100.
Container mariadb has IP 172.17.0.2 
Container activemq has IP 172.17.0.4 
Container tomcat has IP 172.17.0.5 
Container mongodb has IP 172.17.0.3 
========================================
```

### How do I get ID of containers?

Look at the first column of `docker ps -a`.

### I want to run my containers in VirtualBox in Docker machine that I can access them via one IP address.

You want to use `MACHINE` Docker host. _This mode is used by default_ - `gradle test -PplatformStart -DdockerMode=MACHINE`

That command is equivalent to `gradle test -PplatformStart`

### I am running at Windows, is there any hope for me?

You want to run the above command.

### I am running at Windows or Mac and I want to use native Docker containers without VirtualBox.

You can not do that.

### Docker images are not downloaded because it failed to fetch them from the Docker registry.

Be sure you have set password to the repository. Put it to your `gradle.properties` file like this:

```
[smikloso@localhost tests]$ cat ~/.gradle/gradle.properties 
nexusUser=snapshot
nexusPassword=travel.123
systemProp.dockerRegistryPassword=aqGG86d1Yf3Y
```

### Images take too long to download.

Yes it takes a couple of minutes. It is about 2GB in total.

### I am running on Linux and I do not want to use another abstraction layer of VirtualBox. I want to run container at my host directly.

You want to start profile in `HOST` mode - `gradle test -PprofileStart -DdockerMode=HOST`

### I want to run containers both natively and in VirtualBox.

Just execute starting profile for both modes.

`gradle test -PprofileStart -DdockerMode=HOST`

`gradle test -PprofileStart -DdocerMode=MACHINE`

You will have services running at both places. This is because they are completely separated from each other. Native containers 
will be in network like 172.16.0.0, VirtualBox services will be behind one IP address - most probably 192.168.99.100.

### How do I list my running containers from my computer?

It depends how you started them. In case you have started them natively (locally), you can just execute `docker ps -a`.
In case you have started them in VirtualBox, you have to connect to Docker deamon which runs in that VirtualBox. Normally, 
`docker` client is listenting to Docker daemon which runs at your host but in case you want to control / inspect containers 
in VirtualBox, you have to point to that daemon.
 
In the second case you have to do this, lets execute this command firstly:

```
[smikloso@localhost tests]$ docker-machine env default
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.100:2376"
export DOCKER_CERT_PATH="/home/smikloso/.docker/machine/machines/default"
export DOCKER_MACHINE_NAME="default"
# Run this command to configure your shell: 
# eval $(docker-machine env default)
```

It basically says what to do next:

```
[smikloso@localhost tests]$ eval "$(docker-machine env default)"
[smikloso@localhost tests [default]]$ 
```

From now on, while I am in that shell, whenever I execute `docker` command, it will talk to Docker daemon which runs 
in VirtualBox instead of one running at my local host. Once I quit that shell, I am talking to local host daemon again.

### You have got some fancy shell prompt there in the previous command. How do I get that?

You have to install bash completion scripts for `docker-machine`, `docker` and `docker-compose`

https://github.com/docker/docker/tree/master/contrib/completion

https://github.com/docker/compose/tree/master/contrib/completion

https://github.com/docker/machine/tree/master/contrib/completion/bash

https://docs.docker.com/machine/install-machine/

https://docs.docker.com/compose/completion/

### It is possible to control containers from Docker machine / VirtualBox so I do not need to do above voodoo?

Yes, just connect to it like `docker-machine ssh <machine name>`. There is Docker tooling installed in that machine 
so you can control containers just the same way as they would run locally.

### Do I have to specify name of Docker machine every time I am working with it?

No, default name of Docker machine is `default` so you can omit it. e.f. `docker-machine ssh` is the same thing as 
`docker-machine ssh default`.

### Do I have to create my own Docker machine so tests can start and connect to it?

No, when Docker machine of name `default` is not found at your system, it is automatically created for you so 
you do not need to do anything at all. By default, it will have 3072 MB or RAM to use and 2 CPUs. This can be overriden 
by properties on the command line `-DdockerMachineMemorySize=2048 -DdockerMachine=myMachine`. This gives your newly 
created machine just 2BG of memory and it will be named `myMachine`. The disadvantage is that you will have to 
specify that name everytime because when not used, `default` name is taken into account.

### Can I run two virtual machines with two sets of containers?

Sure. You just have to do basically this:

`gradle test -PplatformStart -DdockerMachine=default`

`gradle test -PplatformStart -DdockerMachine=another`

Now you can see you have two of them from this:

```
[smikloso@localhost tests [default]]$ docker-machine ls
NAME      ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER    ERRORS
default   *        virtualbox   Running   tcp://192.168.99.100:2376           v1.10.1
another   *        virtualbox   Running   tcp://192.168.99.101:2376           v1.10.1 
```

Notice that `another` machine has different IP address from the `default` machine.

### How do I get IP address of the container which runs natively?

`docker inspect --format '{{ .NetworkSettings.IPAddress }}' <container id>`

### How do I know what Docker machines / VirtualBox-es are present in my system?

`docker-machine ls`

### What is the IP address of my Docker machine / VirtualBox?

`docker-machine ip <name of your Docker machine>`

### How can I ssh to my Docker machine?

`docker-machine ssh <name of your Docker machine>`

### How do I stop my Docker machine?

`docker-machine stop <name of your Docker machine>`

When you do this, underlying Docker containers are stopped and you will loose everything there.

### I have executed `docker-machine ls`, it takes a lot of time and it says that machine is timeouted.

Just restart it: `docker-machine restart <machine name>`

### Do I have to restart timeouted machine when I execute some Gradle command?

No, timeouted machines will be stopped and started automatically.

### Do I have to start my Docker machine before I will execute my Gradle command?

No. Stopped machine will be started automatically.

### Do I have to manually download images from private Docker repository in case they are not present either in Docker machine or at my local host?

No. Docker images are downloaded automatically and only images which are missing are downloaded.

### Where are Dockerfiles for Docker services?

In "operations" repository. There are also scripts for private Docker registry and Docker compose scripts.