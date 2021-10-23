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
















# cd /Users/dushyantdubaria/Desktop/DD_Desktop/JJCSA/JJCSA
# cd GitHub-repos/backend/
# git pull 

# aws ecr get-login-password --profile jjcsaDD --region us-east-2 | docker login --username AWS --password-stdin 2479XXXX9035.dkr.ecr.us-east-2.amazonaws.com

# docker build -t backend:aws-cfn-jjcsa --build-arg server_port=9080 --build-arg profiles_active=stage --no-cache .
# # docker build -t backend:aws-cfn-jjcsa --build-arg server_port=9080 --build-arg profiles_active=stage .
# docker tag backend:aws-cfn-jjcsa 2479XXXX9035.dkr.ecr.us-east-2.amazonaws.com/jjcsa-backend:aws-cfn-jjcsa
# docker push 2479XXXX9035.dkr.ecr.us-east-2.amazonaws.com/jjcsa-backend:aws-cfn-jjcsa
# docker tag backend:aws-cfn-jjcsa 2479XXXX9035.dkr.ecr.us-east-2.amazonaws.com/jjcsa-backend:98ffd59
# docker push 2479XXXX9035.dkr.ecr.us-east-2.amazonaws.com/jjcsa-backend:98ffd59

# ## Update Backend
# aws ecs update-service \
#     --profile jjcsaDD --region us-east-2 \
#     --cluster JJCSA-ECS-ECSCluster-jqOzzc6Iso4e \
#     --service JJCSA-ECS-BackendECSService-5G929Sk2o8tC \
#     --task-definition Backend \
#     --desired-count 1 \
#     --force-new-deployment

# ## Update Keycloak
# aws ecs update-service \
#     --profile jjcsaDD --region us-east-2 \
#     --cluster JJCSA-ECS-ECSCluster-jqOzzc6Iso4e \
#     --service JJCSA-ECS-KeycloakECSService-Kb38wSFDqD1e \
#     --task-definition Keycloak \
#     --desired-count 1 \
#     --force-new-deployment

