#!/bin/sh
rm -r ../maven2
cd ./sevntu-checks/
mvn deploy
cd ../sevntu-checkstyle-maven-plugin/
mvn deploy
cd ..
git stash
git checkout gh-pages
git rm maven2/*
cp -r ../maven2 ./
git add maven2/*
echo 'You are on the gh-pages branch! Your changes was stashed, type git stash apply, when you return on your branch."
