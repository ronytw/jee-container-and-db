FROM maven:3.5-jdk-7-alpine AS builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

FROM daggerok/jboss-eap-6.4:6.4.22-alpine

COPY --chown=jboss --from=builder /usr/src/app/target/*.war ${JBOSS_HOME}/standalone/deployments/hello.war