# ActiveMQ Docker image

This image is based on:

* CentOS 7 - latest
* Java 8
* ActiveMQ 5.12.3

In order to get web console, you have to detect on which IP address this Docker is running.
You get this information once you execute this:

`docker inspect --format '{{ .NetworkSettings.IPAddress }}' activemq`

which returns you something like `172.17.0.2`

Enter this to your browser to get web console: `http://172.17.0.2:8161`. User name and password is admin/admin.

This Docker image exposes ports 1098, 5672, 8161, 61612, 61613 and 61616.

This container is started by remote debugging possibility.
In order to connect to the running ActiveMQ e.g. by jconsole, you have to enter this connection string to it:

`service:jmx:rmi:///jndi/rmi://(hostname):1098/jmxrmi`

`hostname` can be `127.0.0.1` if you did port forwarding, otherwise you have to connect to Docker's IP like `172.17.0.2`.
