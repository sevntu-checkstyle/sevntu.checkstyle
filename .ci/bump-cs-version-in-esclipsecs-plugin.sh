#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-eclipsecs-plugin.sh <version>"
  exit 1
fi

VERSION=$1
cd eclipsecs-sevntu-plugin

sed -i -e "/<properties>/,/<\/properties>/ "`
  `"s|<checkstyle.version>.*</checkstyle.version>"`
  `"|<checkstyle.version>$VERSION</checkstyle.version>|g" pom.xml

echo "Version updated to $VERSION at eclipsecs-sevntu-plugin/pom.xml"
