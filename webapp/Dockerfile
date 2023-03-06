# Multistage build to build the webapp and move it inside the JBoss container

#########################################
# This is the builder stage, with Maven #
#########################################
FROM maven:3.5-jdk-7-alpine AS builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

############################
# This is the target image #
############################
FROM daggerok/jboss-eap-6.4:6.4.22-alpine

# Script to start JBoss and install the driver and datasource via JBoss-CLI
COPY docker-resources/install-datasource.sh /tmp
RUN /bin/bash /tmp/install-datasource.sh

# Fixes issue with logging rotation (permissions?)
RUN rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history/current

# Copy the previously built webapp
COPY --chown=jboss --from=builder /usr/src/app/target/*.war ${JBOSS_HOME}/standalone/deployments/hello.war