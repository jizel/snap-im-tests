# MariaDB Docker image

This image is based on:

* CentOS 7 - latest
* MariaDB 10.1.12

In order to connect to a database from your host, you firstly get container's IP by

`docker inspect --format '{{ .NetworkSettings.IPAddress }}' mariadb`

which returns you something like `172.17.0.2`

After that, you connect by:

`mysql -u root -h 172.17.0.2 -P 3306 -p`

Username / password is root / root.

This Docker image exposes ports 3306.
