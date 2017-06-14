FROM docker.snapshot.travel:5000/snapshot/java:8u121
MAINTAINER oleg.fayans@snapshot.travel

ENV ACTIVEMQ_VERSION=5.14.2
ENV ACTIVEMQ_DOWNLOAD=/tmp/apache-activemq-$ACTIVEMQ_VERSION-bin.tar.gz
ENV ACTIVEMQ_HOME=/opt/activemq
ENV ACTIVEMQ_JMX_PORT=1098
ENV JAVA_RMI_SERVER_HOSTNAME=

# Install ActiveMQ

RUN wget https://archive.apache.org/dist/activemq/$ACTIVEMQ_VERSION/apache-activemq-$ACTIVEMQ_VERSION-bin.tar.gz -O $ACTIVEMQ_DOWNLOAD && \
    tar -xvf $ACTIVEMQ_DOWNLOAD && \
    mv apache-activemq-$ACTIVEMQ_VERSION $ACTIVEMQ_HOME && \
    rm -r $ACTIVEMQ_DOWNLOAD $ACTIVEMQ_HOME/{docs,examples,webapps-demo} && \
    groupadd activemq && \
    useradd -g activemq -d ${ACTIVEMQ_HOME} -s /sbin/nologin -c "ActiveMQ user" activemq && \
    chown -R activemq:activemq ${ACTIVEMQ_HOME}

ADD activemq_init.sh $ACTIVEMQ_HOME/activemq_init.sh

USER activemq
WORKDIR $ACTIVEMQ_HOME

EXPOSE $ACTIVEMQ_JMX_PORT 5672 8161 61613 61614 61616

ENTRYPOINT ["/opt/activemq/activemq_init.sh"]
