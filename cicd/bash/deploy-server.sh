#!/usr/bin/env bash

CONTAINER_NAME=$1
IMAGE_NAME=$2
APP_ENV=$3

# read local vars
source ~/config/${CONTAINER_NAME}/vars

docker stop ${CONTAINER_NAME} > /dev/null
docker rm ${CONTAINER_NAME} > /dev/null
docker pull ${IMAGE_NAME}

docker run -d --restart unless-stopped \
    --name=${CONTAINER_NAME} \
    -e SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL}" \
    -e SPRING_DATASOURCE_USER="${SPRING_DATASOURCE_USER}" \
    -e SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}" \
    -e APP_ENV=${APP_ENV} \
    -p 15510:8080 \
    "${IMAGE_NAME}"
