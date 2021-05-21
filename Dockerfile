# FROM maven:3.6.3-openjdk-17 as BUILDER
# FROM maven:3.6.3-openjdk-11 as BUILDER
FROM maven:3.6.3-openjdk-8 as BUILDER

COPY src /home/app/src
COPY pom.xml /home/app
# RUN mvn -f /home/app/pom.xml clean package
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# FROM openjdk:17-jdk-alpine as PRODUCTION
# FROM openjdk:11-jre-slim
FROM openjdk:8-jdk-alpine as PRODUCTION

ARG server_port=9080

ENV SERVER_PORT=${server_port}

COPY --from=BUILDER /home/app/target/jjcsa-0.0.1-SNAPSHOT.jar /usr/local/lib/jjcsa.jar
EXPOSE ${server_port}

# ENTRYPOINT mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
ENTRYPOINT java -Dserver.port=${SERVER_PORT} -jar /usr/local/lib/jjcsa.jar
















# docker build -t backend:latest --build-arg server_port=8083 --no-cache .
# docker build -t backend:latest --build-arg server_port=8083 .




# docker build -t backend:latest --build-arg server_port=8083 --build-arg rest_hostname=greeting --build-arg rest_port=8084 --no-cache .
# docker rm backend && docker run -itd --network=java-mvn -p 8083:8083 --name backend backend
# docker tag backend:latest 275xxxxxx462.dkr.ecr.us-east-1.amazonaws.com/backend:latest
# docker push 275xxxxxx462.dkr.ecr.us-east-1.amazonaws.com/backend:latest

# aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 275xxxxxx462.dkr.ecr.us-east-1.amazonaws.com/pt-portal
