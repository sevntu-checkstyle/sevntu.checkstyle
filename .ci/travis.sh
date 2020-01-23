#!/bin/bash
# Attention, there is no "-x" to avoid problems on Travis
#set -e

case $1 in

pr-description)
  .ci/xtr_pr-description.sh
  ;;

eclipse-cs)
  cd eclipsecs-sevntu-plugin
  ECLIPSECS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${eclipsecs.version}' \
                    --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  ECLIPSECS_TAG_NAME=$(echo $ECLIPSECS_POM_VERSION | sed "s/-SNAPSHOT//")
  cd ../
  cd sevntu-checks
  mvn -B -e clean install -Dmaven.test.skip=true -Pno-validations
  cd ..
  git clone https://github.com/checkstyle/eclipse-cs.git
  cd eclipse-cs/
  git checkout $ECLIPSECS_TAG_NAME
  mkdir net.sf.eclipsecs.doc/docs
  mvn -B -e install
  cd ../
  cd eclipsecs-sevntu-plugin
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

idea-extension)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Pno-validations
  cd ..
  cd sevntu-checkstyle-idea-extension
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

sonar-plugin)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Pno-validations
  cd ..
  cd sevntu-checkstyle-sonar-plugin
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

sevntu-checks)
  cd sevntu-checks
  mvn -e -Pcoverall install
  mvn -e verify -Pno-validations,selftesting
  mvn -e javadoc:javadoc
  if [[ $TRAVIS == 'true' ]]; then
    mvn -e -Pcoverall jacoco:report coveralls:report
  fi
  ;;

all-sevntu-checks-contribution)
  wget -q \
    https://raw.githubusercontent.com/checkstyle/contribution/master/checkstyle-tester/checks-sevntu-error.xml
  xmlstarlet sel --net --template -m .//module -v "@name" -n checks-sevntu-error.xml \
    | grep -vE "Checker|TreeWalker|Filter|Holder" | grep -v "^$" \
    | sed "s/com\.github\.sevntu\.checkstyle\.checks\..*\.//" \
    | sort | uniq | sed "s/Check$//" > web.txt
  xmlstarlet sel --net --template -m .//module -v "@name" -n sevntu-checks/sevntu-checks.xml \
    | grep -vE "Checker|TreeWalker|Filter|Holder" | grep -v "^$" \
    | sed "s/com\.github\.sevntu\.checkstyle\.checks\..*\.//" \
    | sort | uniq | sed "s/Check$//" > file.txt
  diff -u web.txt file.txt
  ;;

checkstyle-regression)
  git clone https://github.com/checkstyle/checkstyle
  # update checkstyle_sevntu_checks.xml file in checkstyle for new modules
  cd sevntu-checks
  SEVNTU_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                   --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo sevntu version:$SEVNTU_VERSION
  ECLIPSE_CS_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                   -Dexec.args='${checkstyle.eclipse-cs.version}' \
                   --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo eclipse-cs version:$ECLIPSE_CS_VERSION
  mvn -e install -Pno-validations
  mvn -e test -Dtest=CheckstyleRegressionTest#setupFiles -Dregression-path=../
  cd ../
  # execute checkstyle validation on updated config file
  cd checkstyle
  mvn -e clean verify -e -DskipTests -DskipITs -Dpmd.skip=true \
      -Dfindbugs.skip=true -Dcobertura.skip=true \
      -Dmaven.sevntu-checkstyle-check.checkstyle.version=$ECLIPSE_CS_VERSION \
      -Dmaven.sevntu.checkstyle.plugin.version=$SEVNTU_VERSION
  ;;

eclipse-analysis)
  cd sevntu-checks
  mvn -e clean compile exec:exec -Peclipse-compiler
  ;;

sonarqube)
  # token could be generated at https://sonarcloud.io/account/security/
  # executon on local: SONAR_TOKEN=xxxxxxxxxx ./.ci/travis.sh sonarqube
  if [[ -v TRAVIS_PULL_REQUEST && $TRAVIS_PULL_REQUEST && $TRAVIS_PULL_REQUEST =~ ^([0-9]*)$ ]];
    then
      exit 0;
  fi
  if [[ -z $SONAR_TOKEN ]]; then echo "SONAR_TOKEN is not set"; sleep 5s; exit 1; fi
  export MAVEN_OPTS='-Xmx2000m'
  cd sevntu-checks
  mvn -e clean package sonar:sonar \
       -Dsonar.organization=checkstyle \
       -Dsonar.host.url=https://sonarcloud.io \
       -Dsonar.login=$SONAR_TOKEN \
       -Dmaven.test.failure.ignore=true \
       -Dcheckstyle.skip=true -Dpmd.skip=true
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
