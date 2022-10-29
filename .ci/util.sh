#!/bin/bash
# This script contains common bash CI functions
set -e

function checkout_from {
  CLONE_URL=$1
  PROJECT=$(echo "$CLONE_URL" | sed -nE 's/.*\/(.*).git/\1/p')
  mkdir -p .ci-temp
  cd .ci-temp
  if [ -d "$PROJECT" ]; then
    echo "Target project $PROJECT is already cloned, latest changes will be fetched and reset"
    cd "$PROJECT"
    git fetch
    git reset --hard HEAD
    git clean -f -d
    cd ../
  else
    for i in 1 2 3 4 5; do git clone "$CLONE_URL" && break || sleep 15; done
  fi
  cd ../
}
