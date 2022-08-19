#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-sevntu-checks.sh <version>"
  exit 1
fi

VERSION=$1
cd sevntu-checks
mvn versions:set-property -Dproperty=checkstyle.version -DnewVersion="$VERSION"
rm pom.xml.versionsBackup

echo "Version updated to $VERSION at sevntu-checks/pom.xml"
