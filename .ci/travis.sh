#!/bin/bash
# Attention, there is no "-x" to avoid problems on Travis
set -e

export RUN_JOB=1

case $1 in

init-m2-repo)
  if [[ $RUN_JOB == 1 ]]; then
    MVN_SETTINGS=${TRAVIS_HOME}/.m2/settings.xml

    if [[ -f ${MVN_SETTINGS} ]]; then
      if [[ $TRAVIS_OS_NAME == 'osx' ]]; then
        sed -i'' -e "/<mirrors>/,/<\/mirrors>/ d" "$MVN_SETTINGS"
      else
        xmlstarlet ed --inplace -d "//mirrors" "$MVN_SETTINGS"
      fi
    fi
    if [[ $USE_MAVEN_REPO == 'true' && ! -d "~/.m2" ]]; then
     echo "Maven local repo cache is not found, initializing it ..."
   cd sevntu-checks
     mvn -e --no-transfer-progress -B install -Pno-validations;
     mvn -e --no-transfer-progress clean;
    fi
  else
    echo "$1 is skipped";
  fi
  ;;

run-command)
  if [[ $RUN_JOB == 1 ]]; then
    echo "eval of CMD is starting";
    echo "CMD=$2";
    eval "$2";
    echo "eval of CMD is completed";
  fi
  ;;

quarterly-cache-cleanup)
  find ~/.m2 -maxdepth 4 -type d -mtime +90 -exec rm -rf {} \;
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
