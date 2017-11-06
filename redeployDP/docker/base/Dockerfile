FROM fedora:25
MAINTAINER oleg.fayans@snapshot.travel

RUN dnf -y update && dnf -y install wget tar git && dnf clean all

RUN wget --no-check-certificate https://github.com/Barzahlen/waitforservices/releases/download/v0.3/waitforservices -O /usr/local/bin/waitforservices && \
    chmod +x /usr/local/bin/waitforservices
