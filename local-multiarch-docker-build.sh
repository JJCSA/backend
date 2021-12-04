#!/bin/bash -xe

set -x


COMMIT_HASH=git rev-parse --short HEAD
echo $COMMIT_HASH


## AMD64
# docker build -t jjcsa:1.0-amd64 --build-arg ARCH=amd64 --build-arg server_port=9080 --build-arg profiles_active=local,docker --no-cache .

docker build -t jjcsa:1.0-amd64 --build-arg ARCH=amd64 --build-arg server_port=9080 --build-arg profiles_active=local,docker .

docker tag jjcsa:1.0-amd64 dushyant8858/jjcsa-backend:$COMMIT_HASH-amd64
docker push dushyant8858/jjcsa-backend:$COMMIT_HASH-amd64

docker tag jjcsa:1.0-amd64 dushyant8858/jjcsa-backend:latest-amd64
# docker push dushyant8858/jjcsa-backend:latest-amd64


## ARM64V8
# docker build -t jjcsa:1.0-arm64v8 --build-arg ARCH=arm64v8 --build-arg server_port=9080 --build-arg profiles_active=local,docker --no-cache .

docker build -t jjcsa:1.0-arm64v8 --build-arg ARCH=arm64v8 --build-arg server_port=9080 --build-arg profiles_active=local,docker .

docker tag jjcsa:1.0-arm64v8 dushyant8858/jjcsa-backend:$COMMIT_HASH-arm64v8
docker push dushyant8858/jjcsa-backend:$COMMIT_HASH-arm64v8

docker tag jjcsa:1.0-arm64v8 dushyant8858/jjcsa-backend:latest-arm64v8
# docker push dushyant8858/jjcsa-backend:latest-arm64v8


#####
docker manifest create \
    dushyant8858/jjcsa-backend:$COMMIT_HASH \
    --amend dushyant8858/jjcsa-backend:$COMMIT_HASH-amd64 \
    --amend dushyant8858/jjcsa-backend:$COMMIT_HASH-arm64v8

docker manifest push dushyant8858/jjcsa-backend:$COMMIT_HASH

##
docker manifest create \
    dushyant8858/jjcsa-backend:latest \
    --amend dushyant8858/jjcsa-backend:latest-amd64 \
    --amend dushyant8858/jjcsa-backend:latest-arm64v8

# docker manifest push dushyant8858/jjcsa-backend:latest



