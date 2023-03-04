# DOCKER-VERSION 1.8.2
FROM ubuntu:20.04

ENV JAVA_VERSION 8
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

ENV MAVEN_VERSION 3.3.9
ENV MAVEN_HOME /usr/share/maven
ENV PATH "$PATH:$MAVEN_HOME/bin"

# install utilities
RUN apt-get update \
    && apt-get upgrade

RUN apt-get -y install vim git sudo zip bzip2 fontconfig curl

# install maven
RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN \
      apt-get --no-install-recommends install -y \
        wget \
        curl \
        vim \
        git \
        zip \
        bzip2 \
        fontconfig \
        libpng-dev \
        sudo \
        openjdk-11-jdk && \
      update-java-alternatives -s java-1.11.0-openjdk-amd64 && \
      # install node.js
      wget https://nodejs.org/dist/v14.16.0/node-v14.16.0-linux-x64.tar.gz -O /tmp/node.tar.gz && \
      tar -C /usr/local --strip-components 1 -xzf /tmp/node.tar.gz && \
      # upgrade npm
      npm install -g npm && \
      # install yeoman
      npm install -g yo && \
      # cleanup
      apt-get clean && \
      npm install -g generator-jhipster

# configure the "jhipster" user
RUN groupadd jhipster && useradd jhipster -s /bin/bash -m -g jhipster -G jhipster && adduser jhipster sudo
RUN echo 'jhipster:jhipster' |chpasswd
RUN mkdir -p /home/jhipster/app
ADD banner.txt /home/jhipster/banner.txt
COPY . /home/jhipster/app
RUN cd /home && chown -R jhipster:jhipster /home/jhipster

# clean
RUN apt-get clean

# expose the working directory, the Tomcat port, the BrowserSync ports
VOLUME ["/home/jhipster/app"]
EXPOSE 8080 9000 3001
CMD    ["java", "-jar", "/home/jhipster/app/target/myapp-0.0.1-SNAPSHOT.jar"]
