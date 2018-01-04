#!/bin/bash
# Attention, there is no "-x" to avoid problems on Travis
set -e

case $1 in

pr-description)
  .ci/xtr_pr-description.sh
  ;;

eclipse-cs)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true
  cd ..
  git clone git://git.code.sf.net/p/eclipse-cs/git eclipse-cs-git
  cd eclipse-cs-git/
  git checkout 8.5.1
  cd net.sf.eclipsecs.parent/
  mvn -e install
  cd ../../
  cd eclipsecs-sevntu-plugin
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

maven-plugin)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true
  cd .. 
  cd sevntu-checkstyle-maven-plugin
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

idea-extension)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true
  cd ..
  cd sevntu-checkstyle-idea-extension
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

sonar-plugin)
  cd sevntu-checks
  mvn -e clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true
  cd ..
  cd sevntu-checkstyle-sonar-plugin
  mvn -e verify
  mvn -e javadoc:javadoc
  ;;

checks)
  cd sevntu-checks
  mvn -e install
  mvn -e verify -Pselftesting
  mvn -e javadoc:javadoc
  mvn -e jacoco:report coveralls:jacoco
  ;;

checkstyle-regression)
  git clone https://github.com/checkstyle/checkstyle
  cd sevntu-checks
  mvn -e install -DskipTests -Dcheckstyle.skip=true -Dcobertura.skip=true
  mvn -e test -Dtest=CheckstyleRegressionTest#setupFiles -Dregression-path=../
  cd ../
  cd checkstyle
  mvn -e clean verify -e -DskipTests -DskipITs -Dpmd.skip=true -Dfindbugs.skip=true -Dcobertura.skip=true
  ;;

eclipse-analysis)
  cd sevntu-checks
  mvn -e clean compile exec:exec -Peclipse-compiler
  ;;


*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
