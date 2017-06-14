FROM docker.snapshot.travel:5000/snapshot/base:2.0
MAINTAINER grzegorz.abram@snapshot.travel

ENV MONGO_PORT=27017
ENV LOG_PATH=/dev/stdout
ENV DOWNLOAD_PATH=https://fastdl.mongodb.org/linux
ENV MONGO_VERSION=mongodb-linux-x86_64-3.4.2

RUN dnf -y update && \
    dnf clean all && \
    wget -O /root/mongodb.tgz $DOWNLOAD_PATH/$MONGO_VERSION.tgz && \
    tar -xf /root/mongodb.tgz -C /opt && \
    ln -s /opt/$MONGO_VERSION /opt/mongodb && \
    mkdir -p /data/db

EXPOSE $MONGO_PORT

ENTRYPOINT /opt/mongodb/bin/mongod --port $MONGO_PORT --logpath $LOG_PATH
