#!/bin/bash
set -e

function checkout_from {
  CLONE_URL=$1
  PROJECT=$(echo "$CLONE_URL" | sed -nE 's/.*\/(.*).git/\1/p')
  mkdir -p .ci-temp
  cd .ci-temp
  if [ -d "$PROJECT" ]; then
    echo "Target project $PROJECT is already cloned, latest changes will be fetched"
    cd $PROJECT
    git fetch
    cd ../
  else
    for i in 1 2 3 4 5; do git clone $CLONE_URL && break || sleep 15; done
  fi
  cd ../
}

case $1 in

setup)
  cd sevntu-checks
  mvn -e clean install -Pno-validations
  cd ..
  ;;

checkstyle)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i'' 's/#checkstyle/checkstyle/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

struts)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i'' 's/#apache-struts/apache-struts/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

guava)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i'' 's/#guava/guava/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

hibernate-orm)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#hibernate-orm/hibernate-orm/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

spotbugs)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#spotbugs/spotbugs/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

spring-framework)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#spring-framework/spring-framework/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

hbase)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#Hbase/Hbase/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

pmd-elasticsearch-lombok-ast)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from "-b issue-529 https://github.com/nmancus1/contribution "
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#pmd/pmd/' projects-to-test-on.properties
  sed -i.'' 's/#elasticsearch/elasticsearch/' projects-to-test-on.properties
  sed -i.'' 's/#lombok-ast/lombok-ast/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

alot-of-projects)
  cd sevntu-checks
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.eclipse-cs.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#RxJava/RxJava/' projects-to-test-on.properties
  sed -i.'' 's/#java-design-patterns/java-design-patterns/' projects-to-test-on.properties
  sed -i.'' 's/#MaterialDesignLibrary/MaterialDesignLibrary/' projects-to-test-on.properties
  sed -i.'' 's/#apache-ant/apache-ant/' projects-to-test-on.properties
  sed -i.'' 's/#apache-jsecurity/apache-jsecurity/' projects-to-test-on.properties
  sed -i.'' 's/#android-launcher/android-launcher/' projects-to-test-on.properties
  groovy ./diff.groovy --listOfProjects projects-to-test-on.properties \
      --patchConfig checks-sevntu-error.xml --allowExcludes \
      --mode single --patchBranch "$BRANCH" -r ../../..\
      -xm "-Dcheckstyle.version=${CS_POM_VERSION} -Dsevntu-checkstyle.version=${SEVNTU_POM_VERSION} \
      -Dcheckstyle.failsOnError=false"
  cd ../../
  rm -rf contribution
  cd ..
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
