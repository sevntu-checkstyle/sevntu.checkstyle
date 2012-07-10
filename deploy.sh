#!/bin/sh
rm -r ../maven2
cd ./sevntu-checks/
mvn deploy
cd ../sevntu-checkstyle-maven-plugin/
mvn deploy
cd ..
git checkout gh-pages
cp -r ../maven2 ./
