#!/usr/bin/env bash

DB_URL=$1
DB_USER=$2
DB_PW=$3
ES_URIS=$4

cat > Dockerfile <<END_TEXT
FROM amazoncorretto:17-alpine3.18-jdk

COPY *.jar /hygge-blog-back-end.jar

ENV database.url="${DB_URL}"
ENV database.userName="${DB_USER}"
ENV database.password="${DB_PW}"
ENV esUris="${ES_URIS}"

ENV JVM_OPTS="-Xmx256M -Xms256M -Dspring.profiles.active=prod -Duser.language=zh -Duser.timezone=GMT+8"

ENTRYPOINT ["/bin/sh","-c","java \$JVM_OPTS -jar hygge-blog-back-end.jar"]
END_TEXT