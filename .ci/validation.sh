#!/bin/bash
set -e

source ./.ci/util.sh

case $1 in

eclipse-cs)
  cd eclipsecs-sevntu-plugin
  # we can not use here 'exec-maven-plugin' as requies all dependencies resolved
  ECLIPSECS_TAG_NAME=$(grep "eclipsecs.version" pom.xml \
                           | head -n 1 \
                           | sed -E "s/<(\w|\.|\/)*>//g" \
                           | tr -d '[:space:]' \
                           | sed "s/-SNAPSHOT//")
  cd ../
  cd sevntu-checks
  mvn -e --no-transfer-progress clean install -Pno-validations
  cd ..
  checkout_from "https://github.com/checkstyle/eclipse-cs.git"
  cd .ci-temp/eclipse-cs/
  echo "Eclipse-cs tag: "$ECLIPSECS_TAG_NAME
  git checkout $ECLIPSECS_TAG_NAME
  mvn -e --no-transfer-progress install
  cd ../../
  cd eclipsecs-sevntu-plugin
  mvn -e --no-transfer-progress verify
  mvn -e --no-transfer-progress javadoc:javadoc
  ;;

idea-extension)
  cd sevntu-checks
  mvn -e --no-transfer-progress clean install -Pno-validations
  cd ..
  cd sevntu-checkstyle-idea-extension
  mvn -e --no-transfer-progress verify
  ;;

sonar-plugin)
  cd sevntu-checks
  mvn -e --no-transfer-progress clean install -Pno-validations
  cd ..
  cd sevntu-checkstyle-sonar-plugin
  mvn -e --no-transfer-progress verify
  ;;

sevntu-checks)
  cd sevntu-checks
  mvn -e --no-transfer-progress -Pcoverall install
  mvn -e --no-transfer-progress verify -Pno-validations,selftesting
  # until https://github.com/sevntu-checkstyle/sevntu.checkstyle/issues/999
  # if [[ $CI == 'true' ]]; then
  #   mvn -e --no-transfer-progress -Pcoverall jacoco:report coveralls:report
  # fi
  ;;

all-sevntu-checks-contribution)
  mkdir -p .ci-temp
  wget -q \
    https://raw.githubusercontent.com/checkstyle/contribution/master/checkstyle-tester/checks-sevntu-error.xml
  xmlstarlet sel --net --template -m .//module -v "@name" -n checks-sevntu-error.xml \
    | grep -vE "Checker|TreeWalker|Filter|Holder" | grep -v "^$" \
    | sed "s/com\.github\.sevntu\.checkstyle\.checks\..*\.//" \
    | sort | uniq | sed "s/Check$//" > .ci-temp/web.txt
  xmlstarlet sel --net --template -m .//module -v "@name" \
      -n sevntu-checks/config/sevntu-checks.xml \
    | grep -vE "Checker|TreeWalker|Filter|Holder" | grep -v "^$" \
    | sed "s/com\.github\.sevntu\.checkstyle\.checks\..*\.//" \
    | sort | uniq | sed "s/Check$//" > .ci-temp/file.txt
  DIFF_TEXT=$(diff -u .ci-temp/web.txt .ci-temp/file.txt | cat)
  fail=0
  if [[ $DIFF_TEXT != "" ]]; then
    echo "Diff is detected."
    diff -u .ci-temp/web.txt .ci-temp/file.txt | cat
    echo 'file sevntu-checks/sevntu-checks.xml contains Check that is not present at:'
    echo 'https://raw.githubusercontent.com/checkstyle/contribution/master/checkstyle-tester/checks-sevntu-error.xml'
    echo 'Please add new Check to one of such files to let Check participate in auto testing'
    fail=1;
  fi
  rm checks-sevntu-error.xml
  sleep 5
  exit $fail
  ;;

checkstyle-regression)
  checkout_from "https://github.com/checkstyle/checkstyle"
  # update checkstyle_sevntu_checks.xml file in checkstyle for new modules
  cd sevntu-checks
  SEVNTU_VERSION=$(mvn -e --no-transfer-progress -q -Dexec.executable='echo' \
                   -Dexec.args='${project.version}' --non-recursive \
                   org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo sevntu version:$SEVNTU_VERSION
  ECLIPSE_CS_VERSION=$(mvn -e --no-transfer-progress -q -Dexec.executable='echo' \
                   -Dexec.args='${checkstyle.eclipse-cs.version}' \
                   --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo eclipse-cs version:$ECLIPSE_CS_VERSION
  mvn -e --no-transfer-progress install -Pno-validations
  mvn -e --no-transfer-progress test -Dtest=CheckstyleRegressionTest#setupFiles \
    -Dregression-path=../.ci-temp
  cd ../
  # execute checkstyle validation on updated config file
  cd .ci-temp/checkstyle
  mvn -e --no-transfer-progress clean verify -DskipTests -DskipITs -Dpmd.skip=true \
      -Dspotbugs.skip=true -Dfindbugs.skip=true -Djacoco.skip=true -Dxml.skip=true \
      -Dmaven.sevntu-checkstyle-check.checkstyle.version=$ECLIPSE_CS_VERSION \
      -Dmaven.sevntu.checkstyle.plugin.version=$SEVNTU_VERSION
  ;;

eclipse-analysis)
  cd sevntu-checks
  mvn -e --no-transfer-progress clean compile exec:exec -Peclipse-compiler
  rm org.eclipse.jdt.core.prefs
  ;;

sonarqube)
  # token could be generated at https://sonarcloud.io/account/security/
  # execution on local for master:
  # SONAR_TOKEN=xxxxxx ./.ci/validation.sh sonarqube
  # execution on local for non-master:
  # SONAR_TOKEN=xxxxxx PR_NUMBER=xxxxxx PR_BRANCH_NAME=xxxxxx ./.ci/validation.sh sonarqube
  if [ -z "$SONAR_TOKEN" ]; then
    echo "SONAR_TOKEN is not set."
    exit 1
  fi

  if [[ $PR_NUMBER =~ ^([0-9]+)$ ]]; then
      SONAR_PR_VARIABLES="-Dsonar.pullrequest.key=$PR_NUMBER"
      SONAR_PR_VARIABLES+=" -Dsonar.pullrequest.branch=$PR_BRANCH_NAME"
      SONAR_PR_VARIABLES+=" -Dsonar.pullrequest.base=master"
      echo "SONAR_PR_VARIABLES: ""$SONAR_PR_VARIABLES"
  fi

  cd sevntu-checks
  export MAVEN_OPTS='-Xmx2000m'
  # until https://github.com/checkstyle/checkstyle/issues/11637
  # shellcheck disable=SC2086
  mvn -e --no-transfer-progress clean package sonar:sonar \
       $SONAR_PR_VARIABLES \
       -Dsonar.host.url=https://sonarcloud.io \
       -Dsonar.login="$SONAR_TOKEN" \
       -Dsonar.organization=checkstyle \
       -Dmaven.test.failure.ignore=true \
       -Dcheckstyle.ant.skip=true -Dpmd.skip=true
  echo "report-task.txt:"
  cat target/sonar/report-task.txt
  echo "Verification of sonar gate status"
  export SONAR_API_TOKEN=$SONAR_TOKEN
  ../.ci/sonar-break-build.sh
  ;;

git-diff)
  if [ "$(git status | grep 'Changes not staged\|Untracked files')" ]; then
    printf "Please clean up or update .gitattributes file.\nGit status output:\n"
    printf "Top 300 lines of diff:\n"
    git status
    git diff | head -n 300
    false
  fi
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
