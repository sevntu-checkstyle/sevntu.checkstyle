#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-sonar-plugin.sh <version>"
  exit 1
fi

VERSION=$1
cd sevntu-checkstyle-sonar-plugin

mvn -e --no-transfer-progress versions:set-property -DgenerateBackupPoms=false -Dproperty=checkstyle.version -DnewVersion="$VERSION"

echo "Version updated to $VERSION at sevntu-checkstyle-sonar-plugin/pom.xml"
