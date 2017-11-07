FROM docker.snapshot.travel:5000/snapshot/java:8u131
MAINTAINER oleg.fayans@snapshot.travel

ENV NONPMS_REPO_NAME data-platform-nonpms-integrations
ENV NONPMS_REPO_URL git@bitbucket.org:bbox/$NONPMS_REPO_NAME.git

WORKDIR /root

ADD gradle /root/.gradle

ADD ssh /root/.ssh
RUN chmod 700 /root/.ssh -R
RUN chmod 600 /root/.ssh/id_rsa

ADD db_migrate.sh /root/db_migrate.sh
RUN chmod 755 /root/db_migrate.sh

RUN git clone $NONPMS_REPO_URL
WORKDIR /root/$NONPMS_REPO_NAME

# Now let's clean up extra 98M
RUN dnf remove iptables-libs -y

CMD /root/db_migrate.sh
