#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-eclipsecs-plugin.sh <version>"
  exit 1
fi

VERSION=$1
cd eclipsecs-sevntu-plugin

mvn -e --no-transfer-progress versions:set-property -DgenerateBackupPoms=false -Dproperty=checkstyle.version -DnewVersion="$VERSION"

echo "Version updated to $VERSION at eclipsecs-sevntu-plugin/pom.xml"
