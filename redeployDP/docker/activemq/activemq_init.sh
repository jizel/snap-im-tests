#!/bin/bash

# ActiveMQ need to hook to external IP address of the host 
# in order to do JMX propperly so we need to resolve this.

MY_IP_ADDRESS=$(grep "$(hostname)" /etc/hosts | awk '{print $1}' | head -n 1)

echo "External IP address of the ActiveMQ host: $MY_IP_ADDRESS"

# function which gets called when this script received SIGTERM signal
# from Docker upon the container shutting down.
function stop_activemq_sigterm()
{
    echo "ActiveMQ is about to stop"

    $ACTIVEMQ_HOME/bin/activemq stop

    EXIT_CODE=$?

    echo "ActiveMQ has stopped with the exit code $EXIT_CODE"

    exit $EXIT_CODE
}

trap stop_activemq_sigterm SIGTERM

# The fact that jmxremote.port and rmi.port are same is curcial here.
# It is important to set java.rmi.server.hostname to the external address
# otherwise, in our case, termination process will fail to connect to the 
# ActiveMQ broker via JMX to stop it.

# The fact that jmxremote.port and rmi.port are same is curcial here.
# It is important to set java.rmi.server.hostname to the external address 

# JAVA_RMI_SERVER_HOSTNAME variable sets java.rmi.server.hostname property in CATALINA_OPTS
# This property has to be set to external IP address of the host a container runs in.
# In case this container is running in Docker machine and we want to connect to JMX from
# our local host where Docker machine is running, we have to set it to external IP of the 
# Docker machine. In case this container is running directly at local host, it has to 
# be set to external IP address of the container itself.

if [ -z $JAVA_RMI_SERVER_HOSTNAME ]; then
    JAVA_RMI_SERVER_HOSTNAME=$MY_IP_ADDRESS
fi

ACTIVEMQ_SUNJMX_START="-Dcom.sun.management.jmxremote.port=$ACTIVEMQ_JMX_PORT \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.rmi.port=$ACTIVEMQ_JMX_PORT \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Djava.rmi.server.hostname=$JAVA_RMI_SERVER_HOSTNAME \
    -Dactivemq.jmx.url=service:jmx:rmi:///jndi/rmi://$JAVA_RMI_SERVER_HOSTNAME:$ACTIVEMQ_JMX_PORT/jmxrmi \
    -Dactivemq.jmx.user=admin \
    -Dactivemq.jmx.password=admin"

export ACTIVEMQ_SUNJMX_START

echo "ActiveMQ is about to start"

$ACTIVEMQ_HOME/bin/activemq start

echo "ActiveMQ has started"

touch $ACTIVEMQ_HOME/data/activemq.log

# not putting this into the background will cause 
# that when Docker sends SIGTERM to this script,
# it will not be caught by trap function and exit code 
# will not be 0

tail -f $ACTIVEMQ_HOME/data/activemq.log &

while :
do
    sleep 10
done

