FROM docker.snapshot.travel:5000/snapshot/java:8u121
MAINTAINER oleg.fayans@snapshot.travel

VOLUME /tmp
VOLUME /data
VOLUME /opt/integrations/config
WORKDIR /opt/integrations
ADD runner.sh /opt/integrations/runner.sh
RUN chmod 755 /opt/integrations/runner.sh
CMD /opt/integrations/runner.sh
