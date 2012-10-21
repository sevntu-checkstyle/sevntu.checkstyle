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

cd ../sevntu-checks/
mvn deploy
cd ../sevntu-checkstyle-maven-plugin/
mvn deploy
cd ../gh-pages

#git add .
#git commit -m"new version deploy"
#git push
