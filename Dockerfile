ARG ARCH=

# FROM maven:3.6.3-openjdk-17 as BUILDER
# FROM maven:3.6.3-openjdk-11 as BUILDER
FROM ${ARCH}/maven:3.6.3-openjdk-17 as BUILDER

COPY src /home/app/src
COPY pom.xml /home/app
# RUN mvn -f /home/app/pom.xml clean package
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# FROM openjdk:17-jdk-alpine as PRODUCTION
# FROM openjdk:11-jre-slim
FROM ${ARCH}/openjdk:8-jdk-alpine as PRODUCTION

ARG server_port=9080
ENV SERVER_PORT=${server_port}

ARG profiles_active=stage
ENV PROFILE_ACTIVE=${profiles_active}

ENV JAVA_TOOL_OPTIONS "-Dlog4j2.formatMsgNoLookups=true"

COPY --from=BUILDER /home/app/target/jjcsa-*.jar /usr/local/lib/jjcsa.jar
EXPOSE ${server_port}

# ENTRYPOINT mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
ENTRYPOINT java -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${PROFILE_ACTIVE} -jar /usr/local/lib/jjcsa.jar
