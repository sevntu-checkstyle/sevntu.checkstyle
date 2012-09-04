#!/bin/sh

#clean
rm -rf gh-pages

#prepare
mkdir gh-pages
cd ./gh-pages

git init
git remote add origin https://github.com/sevntu-checkstyle/sevntu.checkstyle.git
git fetch origin gh-pages:refs/remotes/origin/gh-pages
git checkout gh-pages

cd ..
#echo -n "Enter version number"
#read version
#mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$version -f eclipse-pom.xml
mvn clean install -f eclipse-pom.xml
cd update-site
mvn wagon:upload

#git add .
#git commit -m"new version deploy"
#git push
