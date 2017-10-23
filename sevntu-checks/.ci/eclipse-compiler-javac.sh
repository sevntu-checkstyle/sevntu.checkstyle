#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "No parameters supplied!"
    echo "      The classpath of the project and it's libraries to compile must be supplied."
    exit 1
fi
if [ -z "$2" ]; then
    echo "Second parameter not supplied!"
    echo "      The checkstyle release name must be supplied as the second parameter."
    exit 1
fi

ECJ_JAR="ecj-4.7.jar"
ECJ_MAVEN_VERSION="R-4.7-201706120950"
ECJ_PATH=~/.m2/repository/$ECJ_MAVEN_VERSION/$ECJ_JAR

if [ ! -f $ECJ_PATH ]; then
    echo "$ECJ_PATH is not found, downloading ..."
    mkdir -p $(dirname "$ECJ_PATH")
    wget http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/eclipse/downloads/drops4/$ECJ_MAVEN_VERSION/$ECJ_JAR -O $ECJ_PATH
fi

wget https://github.com/checkstyle/checkstyle/blob/checkstyle-$2/config/org.eclipse.jdt.core.prefs

mkdir -p target/classes
mkdir -p target/eclipse

RESULT_FILE=target/eclipse/report.txt

echo "Executing eclipse compiler, output is redirected to $RESULT_FILE..."
java -jar $ECJ_PATH -target 1.8 -source 1.8 -cp $1 \
        -d target/eclipse-compile \
        -enableJavadoc src/main/java src/test/java -properties org.eclipse.jdt.core.prefs \
    > $RESULT_FILE 2>&1 | true

echo "Checking for ERROR|WARNING|INFO  in $RESULT_FILE ..."
if [[ $(grep -E "ERROR|WARNING|INFO" $RESULT_FILE | cat | wc -l) > 0 ]]; then
  cat $RESULT_FILE
  false
fi
