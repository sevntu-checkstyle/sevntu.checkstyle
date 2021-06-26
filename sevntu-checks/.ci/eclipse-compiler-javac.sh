#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "No parameters supplied!"
    echo "      The classpath of the project and its libraries to compile must be supplied."
    exit 1
fi
if [ -z "$2" ]; then
    echo "Second parameter not supplied!"
    echo "      The checkstyle release name must be supplied as the second parameter."
    exit 1
fi

# Eclipse releases every 13 weeks: https://wiki.eclipse.org/SimRel/Simultaneous_Release_Cycle_FAQ
# After that these variables should be updated.
ECJ_MAVEN_VERSION="R-4.20-202106111600"
ECJ_JAR="ecj-4.20.jar"
ECJ_PATH=~/.m2/repository/$ECJ_MAVEN_VERSION/$ECJ_JAR

if [ ! -f $ECJ_PATH ]; then
    echo "$ECJ_PATH is not found, downloading ..."
    ECLIPSE_URL="http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/eclipse/downloads/drops4"
    cd target
    wget $ECLIPSE_URL/$ECJ_MAVEN_VERSION/$ECJ_JAR
    echo "test jar after download:"
    jar -tvf $ECJ_JAR > /dev/null
    mkdir -p $(dirname "$ECJ_PATH")
    cp $ECJ_JAR $ECJ_PATH
    cd ..
fi

wget https://github.com/checkstyle/checkstyle/blob/checkstyle-$2/config/org.eclipse.jdt.core.prefs

mkdir -p target/classes target/test-classes target/eclipse

RESULT_FILE=target/eclipse/report.txt

echo "Executing eclipse compiler, output is redirected to $RESULT_FILE..."
echo "java -jar $ECJ_PATH -target 1.8 -source 1.8 -cp $1  ..."

set +e
java -jar $ECJ_PATH -target 1.8 -source 1.8 -encoding UTF-8 -cp $1 \
        -d target/eclipse-compile \
        -properties org.eclipse.jdt.core.prefs \
        -enableJavadoc \
        src/main/java \
        src/test/java \
    > $RESULT_FILE 2>&1
EXIT_CODE=$?
set -e

if [[ $EXIT_CODE != 0 ]]; then
  echo "Content of $RESULT_FILE:"
  cat $RESULT_FILE
  false
else
    # check compilation of resources, all WARN and INFO are ignored
    set +e
    java -jar $ECJ_PATH -target 1.8 -source 1.8 -cp $1 \
            -d target/eclipse-compile \
            -nowarn \
            src/main/java \
            src/test/java \
            src/test/resources \
        > $RESULT_FILE 2>&1
    EXIT_CODE=$?
    set -e

    if [[ $EXIT_CODE != 0 ]]; then
      echo "Content of $RESULT_FILE:"
      cat $RESULT_FILE
      false
    fi
fi
