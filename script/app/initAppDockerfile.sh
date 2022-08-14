#!/usr/bin/env bash

DB_HOST=$1
DB_USER=$2
DB_PW=$3
ES_HOST=$4

cat > Dockerfile <<END_TEXT
FROM openjdk:8-jdk-alpine
MAINTAINER Xavier xavierpe@qq.com
COPY *.jar /hygge-blog-back-end.jar

ENV JVM_OPTS="-Xmx640M -Xms640M -Xmn192M -XX:MaxMetaspaceSize=128M -XX:MetaspaceSize=128M -XX:+UseParallelGC -XX:+UseAdaptiveSizePolicy -XX:MaxGCPauseMillis=100 -XX:ErrorFile=/logFile/error/hs_err_pid%p.log   -Xloggc:/logFile/gc/gc.log -XX:HeapDumpPath=/logFile/dump -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:class -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintCommandLineFlags -XX:+PrintHeapAtGC"

ENTRYPOINT ["/bin/sh","-c","java \$JVM_OPTS -jar -Ddatabase.url=${DB_HOST} -Ddatabase.userName=${DB_USER} -Ddatabase.password=${DB_PW} -DesHost=${ES_HOST} -Dspring.profiles.active=prod hygge-blog-back-end.jar"]
END_TEXT