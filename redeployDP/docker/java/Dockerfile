FROM docker.snapshot.travel:5000/snapshot/base:2.0
MAINTAINER stefan.miklosovic@snapshot.travel

ENV JAVA_VERSION=8u131
ENV JAVA_BUILD_VERSION=b11
ENV JAVA_DOWNLOAD=/tmp/jdk-8-linux-x64.rpm
ENV JAVA_HOME=/usr/java/latest
ENV JAVA_TOKEN=d54c1d3a095b4ff2b6607d096fa80163

# Install Java 8

RUN wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" \
    "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$JAVA_BUILD_VERSION/$JAVA_TOKEN/jdk-$JAVA_VERSION-linux-x64.rpm" -O $JAVA_DOWNLOAD && \
    dnf -y install $JAVA_DOWNLOAD && \
    rm $JAVA_DOWNLOAD && \
    alternatives --install /usr/bin/java java /usr/java/latest/bin/java 1
RUN rm $JAVA_DOWNLOAD
