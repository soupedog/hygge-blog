#!/usr/bin/env bash

cat > Dockerfile <<END_TEXT
FROM amazoncorretto:17-alpine3.18-jdk

COPY *.jar /hygge-blog-back-end.jar

ENV JVM_OPTS="-Xms256m -Xmx256m -XX:MaxMetaspaceSize=100m -XX:+UseG1GC -Dspring.profiles.active=prod -Duser.language=zh -Duser.timezone=GMT+8"

ENTRYPOINT ["/bin/sh","-c","java \$JVM_OPTS -jar hygge-blog-back-end.jar"]
END_TEXT