FROM docker.snapshot.travel:5000/snapshot/java:8u131
MAINTAINER oleg.fayans@snapshot.travel
ENV DP_BRANCH_NAME master
ENV IDENTITY_REPO_NAME data-platform-identity
ENV IDENTITY_REPO_URL git@bitbucket.org:bbox/$IDENTITY_REPO_NAME.git
ENV NONPMS_REPO_NAME data-platform-nonpms-integrations
ENV NONPMS_REPO_URL git@bitbucket.org:bbox/$NONPMS_REPO_NAME.git

WORKDIR /root

ADD gradle /root/.gradle

ADD runner.sh /root/runner.sh
RUN chmod 755 /root/runner.sh

ADD ssh /root/.ssh
RUN chmod 700 /root/.ssh -R
RUN chmod 600 /root/.ssh/id_rsa

RUN git clone $NONPMS_REPO_URL
RUN git clone $IDENTITY_REPO_URL
# Pre-build the articats to cache the dependencies
WORKDIR /root/$IDENTITY_REPO_NAME
RUN ./gradlew build -x test
RUN find . -iname *.war -exec rm -f {} \;
WORKDIR /root/$NONPMS_REPO_NAME
RUN ./gradlew build -x test
RUN find . -iname *.jar -exec rm -f {} \;
RUN git reset --hard
# Now let's clean up extra 98M
RUN dnf remove iptables-libs -y
VOLUME /data
CMD /root/runner.sh
