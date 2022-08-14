#!/usr/bin/env bash
cd hygge-blog-back-end

mvn -am -Dmaven.test.skip=true clean package

echo "packaged hygge-blog-back-end"

mv target/*.jar script/app/hygge-blog-back-end.jar

echo "move hygge-blog-back-end.jar to script/app/"