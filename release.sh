#!/usr/bin/env bash
set -e

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Ex: 0.0.0
OLD_VERSION=$1
# Ex: 0.1.0
NEW_VERSION=$2
# Ex: 0.2.0
NEXT_VERSION=$3

ECLIPSE_CS_DIR="/tmp/eclipse-cs"
CONTRIBUTION_DIR="/tmp/checkstyle.contribution"

GITHUB_PROJECT="sevntu-checkstyle/sevntu.checkstyle"

# https://github.com/settings/tokens
# SEVNTU_GITHUB_TOKEN=""

if [[ -z "$OLD_VERSION" || -z "$NEW_VERSION" || -z "$NEXT_VERSION" ]]
then
    echo "Old and New and Next version needs to be specified in the arguments"
    exit 1
fi
if [[ -z "$SEVNTU_GITHUB_TOKEN" ]]
then
    echo "Github token needs to be specified in the script or as an environment variable 'SEVNTU_GITHUB_TOKEN'"
    exit 1
fi

# require git, xmlstarlet (xml parsing) and jq (json parsing)
if ! [ -x "$(command -v git)" ]; then
    echo "Error: git is not installed"
    exit 1
fi
if ! [ -x "$(command -v xmlstarlet)" ]; then
    echo "Error: xmlstarlet is not installed"
    exit 1
fi
if ! [ -x "$(command -v jq)" ]; then
    echo "Error: jq is not installed"
    exit 1
fi

# fetch latest origin from server
echo "Resetting repository to origin"
git fetch origin
git reset --hard HEAD
git clean -f -d
git checkout origin/master

cd sevntu-checks
ECLIPSE_CS_VERSION=$(mvn -e --no-transfer-progress -q -Dexec.executable='echo' -Dexec.args='${checkstyle.eclipse-cs.version}' \
  --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
cd ..

# bring in and run release notes builder
echo "Cloning and installing ReleaseNotes builder"

if [ ! -d $CONTRIBUTION_DIR ]; then
    mkdir $CONTRIBUTION_DIR
fi

cd $CONTRIBUTION_DIR

if [ ! -d "$CONTRIBUTION_DIR/contribution/.git" ]; then
    git clone https://github.com/checkstyle/contribution.git

    cd contribution
else
    cd contribution

    git fetch origin
fi

git reset --hard HEAD
git clean -f -d
git checkout origin/master

cd releasenotes-builder
mvn -e --no-transfer-progress package -DskipTests
cd target

java -jar releasenotes-builder-1.0-all.jar -localRepoPath $CURRENT_DIR -remoteRepoPath $GITHUB_PROJECT -startRef $OLD_VERSION -releaseNumber $NEW_VERSION -githubAuthToken $SEVNTU_GITHUB_TOKEN -generateXdoc -xdocTemplate $CURRENT_DIR/sevntu_xdoc_freemarker.template

# bring in eclipse-cs version in-use
echo "Cloning and installing Eclipse CS version $ECLIPSE_CS_VERSION"

if [ ! -d $ECLIPSE_CS_DIR ]; then
    mkdir $ECLIPSE_CS_DIR
fi

cd $ECLIPSE_CS_DIR

if [ ! -d "$ECLIPSE_CS_DIR/eclipse-cs/.git" ]; then
    git clone https://github.com/checkstyle/eclipse-cs.git

    cd eclipse-cs
else
    cd eclipse-cs

    git fetch origin
fi

git reset --hard HEAD
git clean -f -d
git checkout tags/$ECLIPSE_CS_VERSION.0

mvn -e --no-transfer-progress clean install

# update versions in all files
echo "Bumping all versions from $OLD_VERSION to $NEW_VERSION"
cd $CURRENT_DIR
./pom-version-bump.sh $OLD_VERSION $NEW_VERSION

# test and prepare for deployment
echo "Prepare for deployment"

$CURRENT_DIR/deploy-all.sh
cd $CURRENT_DIR

echo ""
read -p "Press enter to continue before committing"

# commit version bump
echo "Committing version bump"
git add .
git commit -m "config: version bump to $NEW_VERSION"
CURRENT_COMMIT=$(git rev-parse HEAD)
git push origin $CURRENT_COMMIT:master

# create new release tag
echo "Pushing new tag $NEW_VERSION"
git tag -a $NEW_VERSION -m "release $NEW_VERSION"
git push --tags

# deploy gh-pages
echo "Updating gh-pages"

$CURRENT_DIR/deploy.sh --gh-pages $NEW_VERSION $CONTRIBUTION_DIR/contribution/releasenotes-builder/target/xdoc.xml

# deploy to maven central
echo "Deploying to maven central"
$CURRENT_DIR/deploy.sh --maven-central

# dropped support for gh-pages/sevntu-checkstyle-default-configuration.xml

# retrieve old milestone number
# https://developer.github.com/v3/issues/milestones/#list-milestones-for-a-repository
OLD_MILESTONE_NUMBER=$(curl --silent --show-error --fail \
  -H "Authorization: token $SEVNTU_GITHUB_TOKEN" \
  https://api.github.com/repos/$GITHUB_PROJECT/milestones?state=open \
  | jq '.[0].number')

echo ""
read -p "Press enter to continue before closing and creating new milestone"

# close old milestone
# https://developer.github.com/v3/issues/milestones/#update-a-milestone
echo "Closing old milestone $OLD_MILESTONE_NUMBER"
curl --silent --show-error --fail -H "Authorization: token $SEVNTU_GITHUB_TOKEN" \
  -d "{ \"state\": \"closed\" }" \
  -X PATCH https://api.github.com/repos/$GITHUB_PROJECT/milestones/$OLD_MILESTONE_NUMBER

# create new milestone
# https://developer.github.com/v3/issues/milestones/#create-a-milestone
echo "Creating new milestone for $NEXT_VERSION"
curl --silent --show-error --fail -H "Authorization: token $SEVNTU_GITHUB_TOKEN" \
  -d "{ \"title\": \"$NEXT_VERSION\", \
        \"state\": \"open\", \
        \"description\": \"\" }" \
  -X POST https://api.github.com/repos/$GITHUB_PROJECT/milestones

echo "Done"
