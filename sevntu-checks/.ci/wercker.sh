#!/bin/bash
# Attention, there is no "-x" to avoid problems on Wercker
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

no-exception-struts)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i'' 's/^guava/#guava/' projects-for-wercker.properties
  sed -i'' 's/#apache-struts/apache-struts/' projects-for-wercker.properties
  groovy ./launch.groovy --listOfProjects projects-for-wercker.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-guava)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i'' 's/^guava/#guava/' projects-for-wercker.properties
  sed -i'' 's/#guava/guava/' projects-for-wercker.properties
  groovy ./launch.groovy --listOfProjects projects-for-wercker.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-hibernate-orm)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#hibernate-orm/hibernate-orm/' projects-to-test-on.properties
  groovy ./launch.groovy --listOfProjects projects-for-wercker.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-spotbugs)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#spotbugs/spotbugs/' projects-to-test-on.properties
  groovy ./launch.groovy --listOfProjects projects-to-test-on.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-spring-framework)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#spring-framework/spring-framework/' projects-to-test-on.properties
  groovy ./launch.groovy --listOfProjects projects-to-test-on.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-hbase)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#Hbase/Hbase/' projects-to-test-on.properties
  groovy ./launch.groovy --listOfProjects projects-to-test-on.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-Pmd-elasticsearch-lombok-ast)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  SEVNTU_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' -Dexec.args='${project.version}' \
                     --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
  echo CS_version: ${CS_POM_VERSION}
  echo SEVNTU_version: ${SEVNTU_POM_VERSION}
  checkout_from https://github.com/checkstyle/contribution.git
  cd .ci-temp/contribution/checkstyle-tester
  sed -i.'' 's/^guava/#guava/' projects-to-test-on.properties
  sed -i.'' 's/#pmd/pmd/' projects-to-test-on.properties
  sed -i.'' 's/#elasticsearch/elasticsearch/' projects-to-test-on.properties
  sed -i.'' 's/#lombok-ast/lombok-ast/' projects-to-test-on.properties
  groovy ./launch.groovy --listOfProjects projects-to-test-on.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

no-exception-alot-of-projects)
  CS_POM_VERSION=$(mvn -e -q -Dexec.executable='echo' \
                     -Dexec.args='${checkstyle.version}' \
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
  groovy ./launch.groovy --listOfProjects projects-to-test-on.properties \
      --config checks-sevntu-error.xml --checkstyleVersion ${CS_POM_VERSION} \
      --sevntuVersion ${SEVNTU_POM_VERSION}
  cd ../../
  rm -rf contribution
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
