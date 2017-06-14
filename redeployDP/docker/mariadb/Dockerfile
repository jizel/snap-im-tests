FROM docker.snapshot.travel:5000/snapshot/base:2.0
MAINTAINER oleg.fayans@snapshot.travel

ENV MARIADB_PORT=3306
ENV GENERAL_LOG_FILE=/dev/stdout
ENV LOG_ERROR=/dev/stderr
ENV MYSQL_HOME=/var/lib/mysql

ENV MYSQLD_OPTIONS=" --port=$MARIADB_PORT \
 --basedir=/usr \
 --datadir=$MYSQL_HOME \
 --plugin-dir=/usr/lib64/mysql/plugin \
 --user=root \
 --general-log-file=$GENERAL_LOG_FILE \
 --log-error=$LOG_ERROR \
 --pid-file=$MYSQL_HOME/localhost.localdomain.pid"

ADD mariadb.sql /root/mariadb.sql
ADD server.cnf /etc/my.cnf.d/server.cnf
ADD *.sh /root/

RUN dnf clean all
RUN wget https://mirror.vpsfree.cz/mariadb/mariadb-10.1.22/yum/fedora25-amd64/rpms/MariaDB-10.1.22-fedora25-x86_64-client.rpm
RUN wget https://mirror.vpsfree.cz/mariadb/mariadb-10.1.22/yum/fedora25-amd64/rpms/MariaDB-10.1.22-fedora25-x86_64-common.rpm
RUN wget https://mirror.vpsfree.cz/mariadb/mariadb-10.1.22/yum/fedora25-amd64/rpms/MariaDB-10.1.22-fedora25-x86_64-server.rpm
RUN wget https://mirror.vpsfree.cz/mariadb/mariadb-10.1.22/yum/fedora25-amd64/rpms/MariaDB-10.1.22-fedora25-x86_64-shared.rpm

RUN dnf -y install ./*.rpm && dnf clean all && /root/mariadb_init.sh

EXPOSE $MARIADB_PORT
ENTRYPOINT ["/root/mariadb.sh"]
