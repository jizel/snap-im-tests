FROM java:8-jre-alpine

ENV JBOSS_HOME /opt/jboss/keycloak
ENV KEYCLOAK_VERSION 1.9.8.Final
# Enables signals getting passed from startup script to JVM
# ensuring clean shutdown when container is stopped.
ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV PROXY_ADDRESS_FORWARDING false

USER root

RUN apk update && apk upgrade
RUN apk add curl

RUN apk add --update jq && rm /var/cache/apk/*

# Install and configure SSH/SFTP server
RUN apk update &&\
    apk upgrade && \
    apk add openssh-sftp-server openssh-client dropbear &&\
    rm -rf /var/cache/apk/*
RUN touch /var/log/lastlog

# Keycloak install and configuration
RUN mkdir -p /usr/share/java/saxon
RUN curl -L -o /usr/share/java/saxon/saxon.zip http://downloads.sourceforge.net/project/saxon/Saxon-HE/9.6/SaxonHE9-6-0-7J.zip && \
    unzip /usr/share/java/saxon/saxon.zip -d /usr/share/java/saxon && \
    rm -rf /usr/share/java/saxon/noticies /usr/share/java/saxon/doc \
      /usr/share/java/saxon/saxon9-test.jar /usr/share/java/saxon/saxon9-unpack.jar /usr/share/java/saxon/saxon.zip

RUN mkdir -p /opt/jboss/
RUN cd /opt/jboss/ && curl -L https://downloads.jboss.org/keycloak/$KEYCLOAK_VERSION/keycloak-$KEYCLOAK_VERSION.tar.gz | tar zx && mv /opt/jboss/keycloak-$KEYCLOAK_VERSION /opt/jboss/keycloak

ADD setLogLevel.xsl /opt/jboss/keycloak/
RUN java -jar /usr/share/java/saxon/saxon9he.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone.xml -xsl:/opt/jboss/keycloak/setLogLevel.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone.xml

ADD keycloak-server.json /opt/jboss/keycloak/standalone/configuration/
ADD dynamic-login-forms-provider-1.2.0-dp-1_37-20170316.163630-3.jar /opt/jboss/keycloak/providers/
ADD email-template-provider-1.2.0-dp-1_37-20170322.083930-5.jar /opt/jboss/keycloak/providers/
ADD federation-provider-1.2.0-dp-1_37-20170316.163641-3-jar-with-dependencies.jar /opt/jboss/keycloak/providers/

ADD changeDatabase.xsl /opt/jboss/keycloak/
RUN java -jar /usr/share/java/saxon/saxon9he.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone.xml -xsl:/opt/jboss/keycloak/changeDatabase.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone.xml; java -jar /usr/share/java/saxon/saxon9he.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml -xsl:/opt/jboss/keycloak/changeDatabase.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml; rm /opt/jboss/keycloak/changeDatabase.xsl
RUN mkdir -p /opt/jboss/keycloak/modules/system/layers/base/org/postgresql/jdbc/main; cd /opt/jboss/keycloak/modules/system/layers/base/org/postgresql/jdbc/main; curl -O http://central.maven.org/maven2/org/postgresql/postgresql/9.3-1102-jdbc3/postgresql-9.3-1102-jdbc3.jar
ADD module.xml /opt/jboss/keycloak/modules/system/layers/base/org/postgresql/jdbc/main/

ENV JBOSS_HOME /opt/jboss/keycloak

#Enabling Proxy address forwarding so we can correctly handle SSL termination in front ends
#such as an OpenShift Router or Apache Proxy
RUN sed -i -e 's/<http-listener /& proxy-address-forwarding="${env.PROXY_ADDRESS_FORWARDING}" /' $JBOSS_HOME/standalone/configuration/standalone.xml

# Add admin user to keycloak
#RUN if [ $KEYCLOAK_USER ] && [ $KEYCLOAK_PASSWORD ]; then /opt/jboss/keycloak/bin/add-user-keycloak.sh --user "${KEYCKLOAK_USER}" --password "${KEYCLOAK_PASSWORD}" >/dev/null; fi

ADD docker-entrypoint.sh /opt/jboss/
RUN chmod +x /opt/jboss/docker-entrypoint.sh

# Keycloak running on port 8080
EXPOSE 8080

# Entrypoint and binding parameters
ENTRYPOINT [ "/opt/jboss/docker-entrypoint.sh" ]
CMD ["-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]