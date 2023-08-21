#!/usr/bin/env bash

DB_URL=$1
DB_USER=$2
DB_PW=$3
ES_URIS=$4

cat > Dockerfile <<END_TEXT
FROM openjdk:17-jdk-alpine
MAINTAINER Xavier xavierpe@qq.com
COPY *.jar /hygge-blog-back-end.jar

ENV JVM_OPTS="-Xmx256M -Xms256M"

ENTRYPOINT ["/bin/sh","-c","java \$JVM_OPTS -jar -Ddatabase.url=${DB_URL} -Ddatabase.userName=${DB_USER} -Ddatabase.password=${DB_PW} -DesUris=${ES_URIS} -Dspring.profiles.active=prod -Duser.timezone=GMT+8 hygge-blog-back-end.jar"]
END_TEXT