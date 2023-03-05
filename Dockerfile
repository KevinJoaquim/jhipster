# DOCKER-VERSION 1.8.2

#FROM maven:3.6.0-jdk-11-slim AS build
#RUN mkdir -p /home/jhipster/app
#COPY . /home/jhipster/app

#
# Package stage
#
FROM openjdk:11-jre-slim
RUN mkdir -p /home/jhipster/app
COPY target/myapp-0.0.1-SNAPSHOT.jar /home/jhipster/app/target/myapp-0.0.1-SNAPSHOT.jar
#COPY --from=build /home/app/target/demo-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 8080 9000 3001
#ENTRYPOINT ["java","-jar","/home/jhipster/app/target/myapp-0.0.1-SNAPSHOT.jar"]
CMD ["java","-jar","/home/jhipster/app/target/myapp-0.0.1-SNAPSHOT.jar"]

# configure the "jhipster" user
#RUN groupadd jhipster && useradd jhipster -s /bin/bash -m -g jhipster -G jhipster && adduser jhipster sudo
#RUN echo 'jhipster:jhipster' |chpasswd
#RUN mkdir -p /home/jhipster/app
#ADD banner.txt /home/jhipster/banner.txt
#COPY . /home/jhipster/app
#RUN cd /home && chown -R jhipster:jhipster /home/jhipster


# expose the working directory, the Tomcat port, the BrowserSync ports
#VOLUME ["/home/jhipster/app"]
#EXPOSE 8080 9000 3001
#CMD    ["java", "-jar", "/home/jhipster/app/target/myapp-0.0.1-SNAPSHOT.jar"]
