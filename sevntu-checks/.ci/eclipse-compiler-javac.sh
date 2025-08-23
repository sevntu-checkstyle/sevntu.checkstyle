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

JAVA_RELEASE=${3:-21}

ECLIPSE_URL="http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/eclipse/downloads/drops4"
ECJ_MAVEN_VERSION=$(wget --quiet -O- "$ECLIPSE_URL/?C=M;O=D" | grep -o "R-[^/]*" | head -n1)
echo "Latest eclipse release is $ECJ_MAVEN_VERSION"
ECJ_JAR=$(wget --quiet -O- "$ECLIPSE_URL/$ECJ_MAVEN_VERSION/" | grep -o "ecj-[^\"]*" | head -n1)
ECJ_PATH=~/.m2/repository/$ECJ_MAVEN_VERSION/$ECJ_JAR

if [ ! -f "$ECJ_PATH" ]; then
    echo "$ECJ_PATH is not found, downloading ..."
    cd target
    wget $ECLIPSE_URL/"$ECJ_MAVEN_VERSION"/"$ECJ_JAR"
    echo "test jar after download:"
    jar -tvf "$ECJ_JAR" > /dev/null
    mkdir -p "$(dirname "$ECJ_PATH")"
    cp "$ECJ_JAR" "$ECJ_PATH"
    cd ..
fi

wget https://raw.githubusercontent.com/checkstyle/checkstyle/checkstyle-$2/config/org.eclipse.jdt.core.prefs

# Suppress Eclipse compiler false violation. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=353394
sed -i -e "s/reportMethodCanBeStatic=error/reportMethodCanBeStatic=warning/g" \
  org.eclipse.jdt.core.prefs

mkdir -p target/classes target/test-classes target/eclipse

RESULT_FILE=target/eclipse/report.txt

echo "Executing eclipse compiler, output is redirected to $RESULT_FILE..."
echo "java -jar $ECJ_PATH -target ${JAVA_RELEASE} -source ${JAVA_RELEASE} -cp $1  ..."

set +e
java -jar "$ECJ_PATH" -target "${JAVA_RELEASE}" -source "${JAVA_RELEASE}" -encoding UTF-8 -cp "$1" \
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
    java -jar "$ECJ_PATH" -target "${JAVA_RELEASE}" -source "${JAVA_RELEASE}" -cp "$1" \
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

