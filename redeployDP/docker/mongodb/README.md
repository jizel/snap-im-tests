# Mongo Docker image

This image is based on:

* CentOS 7
* Mongo 3.2

You have to detect on which IP address this Docker container is running. 
You get this information once you execute this:

`docker inspect --format '{{ .NetworkSettings.IPAddress }}' mongo`

which returns you something like `172.17.0.2`

In order to connect to Mongo, do this at your host:

`mongo --host 172.17.0.2 --port 27017`

This Docker image exposes ports 27017.
