#!/usr/bin/env bash

SEVNTU_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GH_SEVNTU_DIR="/tmp/sevntu.checkstyle.gh"
PROJECT_NAME="sevntu.checkstyle"
GH_SEVNTU_HOMR_DIR="$GH_SEVNTU_DIR/$PROJECT_NAME"
GITHUB_PROJECT="sevntu-checkstyle/$PROJECT_NAME"

usage="$(basename "$0") [--help --all --eclipse-cs --sonar --idea]
where:

    --help       show this help text
    --all        deploy all projects;
    --eclipse-cs deploy only 'sevntu-checkstyle-eclipsecs-plugin' project;
    --sonar      deploy only 'sevntu-checkstyle-sonar-plugin' project;
    --idea       deploy only 'sevntu-checkstyle-idea-extension' project;
    --maven-central deploy to maven central;
    --gh-pages   deploy only gh-pages binaries with version argument;

"

manualDeploy="
For new version deploy please do:

    cd gh-pages && git add . && git commit -m \"new version deploy\" && git push origin gh-pages
        "

prepareForDeploy()
    {
        echo "Preparing for Deployment"
        if [ ! -d $GH_SEVNTU_DIR ]; then
            mkdir $GH_SEVNTU_DIR
        fi

        cd $GH_SEVNTU_DIR

        if [ ! -d "$GH_SEVNTU_DIR/$PROJECT_NAME/.git" ]; then
            git clone https://github.com/$GITHUB_PROJECT.git

            cd $GH_SEVNTU_HOMR_DIR
        else
            cd $GH_SEVNTU_HOMR_DIR

            git fetch origin
        fi

        git reset --hard HEAD
        git clean -f -d
        git checkout origin/gh-pages

        return
    }

deployIdea()
    {
        echo "Deploying Idea"
        cd $SEVNTU_DIR/sevntu-checkstyle-idea-extension/
        mvn -e --no-transfer-progress clean javadoc:javadoc deploy -Plocal-deploy \
          -DdeployDir=$GH_SEVNTU_HOMR_DIR
        if [ "$?" != "0" ]
        then
            echo "build for $SEVNTU_DIR/sevntu-checkstyle-idea-extension/"
            exit 1
        fi

        cd $GH_SEVNTU_HOMR_DIR
        echo "$manualDeploy"
        return
    }

deployEclipse()
    {
        echo "Deploying Eclipse"
        cd $SEVNTU_DIR
        #echo -n "Enter version number: "
        #read version
        #mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$version \
        #-f eclipse-pom.xml
        mvn -e --no-transfer-progress clean install -f eclipse-pom.xml -Plocal-deploy
        if [ "$?" != "0" ]
        then
            echo "build for eclipse-pom.xml."
            exit 1
        fi

        cd $SEVNTU_DIR/update-site
        mvn -e --no-transfer-progress wagon:upload -DdeployDir=$GH_SEVNTU_HOMR_DIR
        cd $GH_SEVNTU_HOMR_DIR
        echo "$manualDeploy"
        return
    }

deployMavenLibrary()
    {
        echo "Deploying Sevntu Checks"
        # As we do not use SNAPSHOT qualifier for development in pom.xml
        # we have to deploy library sevntu-checks always even it overrides existing binaries
        # in maven repository for release build - it will not override binaries
        # for test build - it will override as we need to be sure that in repository,
        #                  we have previous release version but compiled with from new code
        cd $SEVNTU_DIR/sevntu-checks
        mvn -e --no-transfer-progress clean javadoc:javadoc deploy -Plocal-deploy \
          -DdeployDir=$GH_SEVNTU_HOMR_DIR
        if [ "$?" != "0" ]
        then
            echo "build for $SEVNTU_DIR/sevntu-checks."
            exit 1
        fi
        # deployment of Javadoc to static site
        cp -rf target/site/apidocs $GH_SEVNTU_HOMR_DIR

        # no need push to repository only library it should be done together with IDE plugins
        # release

        #cd $GH_SEVNTU_HOMR_DIR
        #echo "$manualDeploy"
        return
    }

deployToMavenCentral()
    {
        echo "Deploying All to Maven Central"
        # As we do not use SNAPSHOT qualifier for development in pom.xml
        # we have to deploy library sevntu-checks always even it overrides existing binaries
        # in maven repository for release build - it will not override binaries
        # for test build - it will override as we need to be sure that in repository,
        #                  we have previous release version but compiled with from new code
        cd $SEVNTU_DIR/sevntu-checks
        mvn -e --no-transfer-progress clean deploy -DskipStaging=false -Pgpg
        if [ "$?" != "0" ]
        then
            echo "build for $SEVNTU_DIR/sevntu-checks."
            exit 1
        fi

        cd $SEVNTU_DIR/sevntu-checkstyle-idea-extension/
        mvn -e --no-transfer-progress clean deploy -DskipStaging=false -Pgpg

        cd $SEVNTU_DIR/sevntu-checkstyle-sonar-plugin/
        mvn -e --no-transfer-progress clean deploy -DskipStaging=false -Pgpg

        return
    }

deploySonar()
    {
        echo "Deploying Sonar"
        cd $SEVNTU_DIR/sevntu-checkstyle-sonar-plugin/
        mvn -e --no-transfer-progress clean install wagon:upload-single \
          -DdeployDir=$GH_SEVNTU_HOMR_DIR
        if [ "$?" != "0" ]
        then
            echo "build for $SEVNTU_DIR/sevntu-checkstyle-sonar-plugin/"
            exit 1
        fi
        cd $GH_SEVNTU_HOMR_DIR
        echo "$manualDeploy"
        return
    }

deployToGhPages()
    {
        echo "Deploying GH Pages"
        cd $GH_SEVNTU_HOMR_DIR

        sed -i "/<h2> New and noteworthy<\/h2>/ r $2" index.html

        git add .
        git commit -m "binaries for $1 release"
        CURRENT_COMMIT=$(git rev-parse HEAD)
        git push origin $CURRENT_COMMIT:gh-pages
        return
    }

if [ $# -eq 0 ]
  then
    echo "$usage"
fi

while :
do
    case $1 in
        --help | -\?)
            echo "$usage"
            exit 0
            ;;
        --all)
            prepareForDeploy
            deployMavenLibrary
            deployEclipse
            deployIdea
            deploySonar
            echo "$manualDeploy"
            shift 1
            ;;
        --eclipse-cs)
            prepareForDeploy
            deployMavenLibrary
            deployEclipse
            shift 1
            ;;
        --sonar)
            prepareForDeploy
            deployMavenLibrary
            deploySonar
            shift 1
            ;;
        --idea)
            prepareForDeploy
            deployMavenLibrary
            deployIdea
            shift 1
            ;;
        --gh-pages)
            if [ $# -eq 0 ]
            then
                echo "$usage"
                shift 1
            else
                deployToGhPages $2 $3
                shift 3
            fi
            ;;
        --maven-central)
            deployToMavenCentral
            shift 1
            ;;

        --) # End of all options
            shift
            break
            ;;
        -*)
            echo "WARN: Unknown option : $1"
            echo "$usage"
            shift
            exit 0
            ;;
        *)  # no more options. Stop while loop
            break
            ;;
    esac
done
