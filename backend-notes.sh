#### All this is for local environment testing.

docker network create jjcsa


# ------- POSTGRES on Docker------- #
docker stop postgres && docker rm postgres

docker run --name postgres \
    -p 5432:5432 \
    -e POSTGRES_DB=jjcsa -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=jjcsa \
    -d postgres

docker logs -f postgres

    -e POSTGRES_DB=jjcsa -e POSTGRES_USER=jjcsa -e POSTGRES_PASSWORD=jjcsajjcsa \
ssh -i "dubaria.pem" ec2-user@ec2-3-92-200-151.compute-1.amazonaws.com



docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e DEBUG=true -e DEBUG_PORT='*:8787' -p '8787:8787' \
    -e KEYCLOAK_LOGLEVEL=DEBUG -e ROOT_LOGLEVEL=DEBUG \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e DB_ADDR=jjcsa.cxkjgvexle7c.us-east-1.rds.amazonaws.com -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=jjcsa -e DB_PASSWORD=jjcsajjcsa \
    -d jboss/keycloak 


docker logs -f keycloak

########## REAL 
docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e DB_VENDOR=POSTGRES -e DB_ADDR=$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}') -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=admin -e DB_PASSWORD=jjcsa -e DB_SCHEMA=public \
    -d quay.io/keycloak/keycloak:latest 


docker logs -f keycloak


##########






docker run --name postgres \
    -p 5432:5432 \
    -e POSTGRES_PASSWORD=ddsecretpassword \
    -d postgres

# https://www.optimadata.nl/blogs/1/n8dyr5-how-to-run-postgres-on-docker-part-1

docker exec -it postgres bash
su postgres
psql
CREATE ROLE admin WITH SUPERUSER CREATEDB CREATEROLE LOGIN PASSWORD 'jjcsa';
CREATE DATABASE jjcsa WITH OWNER admin;

\conninfo
\du
\l

\q

# https://kb.objectrocket.com/postgresql/how-to-create-a-role-in-postgres-1454

# -------------------------- #



# -------- pgadmin on Docker --------- #

docker stop pgadmin && docker rm pgadmin

docker run --name pgadmin \
    -p 5050:80 \
    -e "PGADMIN_DEFAULT_EMAIL=dd@dd.com" \
    -e "PGADMIN_DEFAULT_PASSWORD=ddSuperSecret" \
    -d dpage/pgadmin4

# https://www.pgadmin.org/docs/pgadmin4/latest/container_deployment.html
http://localhost:5050

ifconfig | grep "inet "


# -------------------------- #


# -------- Keycloak on Docker -------- #
$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}')

docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e DB_ADDR=$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}') -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=admin -e DB_PASSWORD=jjcsa \
    -d jboss/keycloak 


docker logs -f keycloak

http://localhost:8080



docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -d jboss/keycloak 

docker exec -it keycloak bash
docker logs -f keycloak


# https://hub.docker.com/r/jboss/keycloak




docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -d quay.io/keycloak/keycloak:12.0.2


http://localhost:8080





# -------- backend -------- #

docker stop backend && docker rm backend

# docker build -t backend:latest --build-arg server_port=9080 --no-cache .
docker build -t backend:latest --build-arg server_port=9080 .


docker run --name backend \
    -p 9080:9080 \
    -d backend:latest


docker logs -f backend

http://localhost:9080

# https://spring.io/guides/gs/spring-boot-docker/





# -------- docker system -------- #
docker system df
docker system prune --all





docker stop postgres && docker rm postgres
docker stop keycloak && docker rm keycloak
docker stop backend && docker rm backend








#####################################################################
####################### Quick Reference START #######################
#####################################################################


# ------- POSTGRES on Docker------- #
docker stop postgres && docker rm postgres

docker run --name postgres \
    -p 5432:5432 \
    -e POSTGRES_DB=jjcsa -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=jjcsa \
    -d postgres

docker logs -f postgres










# -------- Keycloak on Docker -------- #
docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e DB_ADDR=$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}') -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=admin -e DB_PASSWORD=jjcsa \
    -d jboss/keycloak 


docker logs -f keycloak

open http://localhost:8080




# -------- backend -------- #


# docker build -t backend:latest --build-arg server_port=9080 --no-cache .
docker build -t backend:latest --build-arg server_port=9080 .

docker stop backend && docker rm backend

docker run --name backend \
    -p 9080:9080 \
    -d backend:latest

docker logs -f backend


open http://localhost:9080/api/users/login


###################################################################
####################### Quick Reference END #######################
###################################################################




docker stop postgres && docker rm postgres

docker run --name postgres \
    -p 3306:3306 \
    -e MYSQL_DB=jjcsa -e MYSQL_USER=admin -e MYSQL_ROOT_PASSWORD=jjcsa \
    -d mysql

docker logs -f postgres


docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_LOGLEVEL=DEBUG -e ROOT_LOGLEVEL=DEBUG \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e DB_ADDR=$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}') -e DB_PORT=3306 -e DB_DATABASE=jjcsa -e DB_USER=admin -e DB_PASSWORD=jjcsa \
    -d quay.io/keycloak/keycloak 

docker logs -f keycloak




aws ecs update-service \
    --cluster manual-jjcsa \
    --service manual-postgres \
    --task-definition postgres:1 \
    --network-configuration 'awsvpcConfiguration={subnets=[subnet-05e7e3de70c4ef4c1,subnet-035d750dd23ac1d75],securityGroups=[sg-082351b3920bccac4],assignPublicIp=DISABLED}' \
    --desired-count 1 \
    --force-new-deployment



#### PRIVATE ####
aws ecs update-service \
    --cluster manual-jjcsa \
    --service manual-keycloak \
    --task-definition keycloak:1 \
    --network-configuration 'awsvpcConfiguration={subnets=[subnet-05e7e3de70c4ef4c1,subnet-035d750dd23ac1d75],securityGroups=[sg-082351b3920bccac4],assignPublicIp=DISABLED}' \
    --desired-count 1 \
    --force-new-deployment
#### PRIVATE ####



#### PUBLIC ####
aws ecs update-service \
    --cluster manual-jjcsa \
    --service manual-keycloak \
    --task-definition keycloak:1 \
    --network-configuration 'awsvpcConfiguration={subnets=[subnet-0e75ab36b00703990,subnet-0759204fde24c3c47],securityGroups=[sg-082351b3920bccac4],assignPublicIp=ENABLED}' \
    --desired-count 1 \
    --force-new-deployment
#### PUBLIC ####






docker build -t backend:aws --build-arg server_port=9080 .

aws ecs update-service \
    --cluster manual-jjcsa \
    --service manual-jjcsa-backend \
    --task-definition jjcsa-backend:1 \
    --network-configuration 'awsvpcConfiguration={subnets=[subnet-0e75ab36b00703990,subnet-0759204fde24c3c47],securityGroups=[sg-029a831e24b111802],assignPublicIp=ENABLED}' \
    --desired-count 1 \
    --force-new-deployment






docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password-password \
    -e DB_ADDR=jk14wf3xwflwgxa.cnhx8kwsk7q2.us-east-2.rds.amazonaws.com -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=jjcsa -e DB_PASSWORD=jjcsa-jjcsa -e DB_VENDOR=POSTGRES -e DB_SCHEMA=public \
    -d quay.io/keycloak/keycloak 

docker logs -f keycloak






========

docker stop postgres && docker rm postgres

docker run --name postgres \
    -p 5432:5432 \
    -e POSTGRES_DB=jjcsa -e POSTGRES_USER=jjcsa -e POSTGRES_PASSWORD=jjcsa-jjcsa \
    -d postgres

docker logs -f postgres


docker stop keycloak && docker rm keycloak

docker run --name keycloak \
    -p 8080:8080 \
    -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password-password \
    -e DB_ADDR=$(docker inspect postgres --format '{{.NetworkSettings.IPAddress}}') -e DB_PORT=5432 -e DB_DATABASE=jjcsa -e DB_USER=jjcsa -e DB_PASSWORD=jjcsa-jjcsa -e DB_VENDOR=POSTGRES -e DB_SCHEMA=public \
    -d quay.io/keycloak/keycloak 

docker logs -f keycloak


02:39:58,450 INFO  [org.keycloak.connections.jpa.DefaultJpaConnectionProviderFactory] (ServerService Thread Pool -- 63) Database info: {databaseUrl=jdbc:postgresql://172.17.0.2:5432/jjcsa, databaseUser=jjcsa, databaseProduct=PostgreSQL 13.2 (Debian 13.2-1.pgdg100+1), databaseDriver=PostgreSQL JDBC Driver 42.2.5}
02:45:34,890 INFO  [org.keycloak.connections.jpa.DefaultJpaConnectionProviderFactory] (ServerService Thread Pool -- 67) Database info: {databaseUrl=jdbc:postgresql://jk14wf3xwflwgxa.cnhx8kwsk7q2.us-east-2.rds.amazonaws.com:5432/jjcsa, databaseUser=jjcsa, databaseProduct=PostgreSQL 9.6.5, databaseDriver=PostgreSQL JDBC Driver 42.2.5}


version: '3'

volumes:
  postgres_data:
      driver: local

services:
  postgres:
      image: postgres
      volumes:
        - postgres_data:/var/lib/postgresql/data
      environment:
        POSTGRES_DB: keycloak
        POSTGRES_USER: keycloak
        POSTGRES_PASSWORD: password
  keycloak:
      image: quay.io/keycloak/keycloak:latest
      environment:
        DB_VENDOR: POSTGRES
        DB_ADDR: postgres
        DB_DATABASE: keycloak
        DB_USER: keycloak
        DB_SCHEMA: public
        DB_PASSWORD: password
        KEYCLOAK_USER: admin
        KEYCLOAK_PASSWORD: Pa55w0rd
        # Uncomment the line below if you want to specify JDBC parameters. The parameter below is just an example, and it shouldn't be used in production without knowledge. It is highly recommended that you read the PostgreSQL JDBC driver documentation in order to use it.
        #JDBC_PARAMS: "ssl=true"
      ports:
        - 8080:8080
      depends_on:
        - postgres


docker build -t backend:aws-cfn-jjcsa --build-arg server_port=9080 .

docker stop backend && docker rm backend

docker run --name backend \
    -p 9080:9080 \
    -d backend:aws-cfn-jjcsa

docker logs -f backend
