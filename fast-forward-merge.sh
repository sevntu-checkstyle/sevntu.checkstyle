#!/usr/bin/env bash 

if [ $# -eq 0 ]
  then
    echo "$(basename "$0") GIT_REPO FORK_USER_NAME USER_BRANCH
example:

    ./$(basename "$0") sevntu.checkstyle baratali issue73
"
fi

GIT_REPO=$1
FORK_USER_NAME=$2
USER_BRANCH=$3
REPO='$FORK_USER'-fork

git remote add $REPO https://github.com/$FORK_USER_NAME/$GIT_REPO.git
git fetch $REPO
git checkout -b $USER_BRANCH $REPO/$USER_BRANCH
git rebase master
git checkout master
git merge $USER_BRANCH