#!/bin/sh

EXEC_DIR=`dirname $0`
REPO_HOME_DIR=`cd "$EXEC_DIR" ; pwd`

usage="$(basename "$0") [--help --all --eclipse-cs --sonar --maven --idea]
where:

    --help       show this help text
    --all        deploy all projects;
    --eclipse-cs deploy only 'sevntu-checkstyle-eclipsecs-plugin' project;
    --sonar      deploy only 'sevntu-checkstyle-sonar-plugin' project;
    --maven      deploy only 'sevntu-checkstyle-maven-plugin' project;
    --idea       deploy only 'sevntu-checkstyle-idea-extension' project;

"

manualDeploy="
For new version deploy please do:

    cd gh-pages && git add . && git commit -m \"new version deploy\" && git push origin gh-pages
        "

prepareForDeploy()
    {
        #clean
        rm -rf $REPO_HOME_DIR/gh-pages

        #prepare folders for update-site and our release maven repository
        mkdir $REPO_HOME_DIR/gh-pages
        cd $REPO_HOME_DIR/gh-pages

        git init
        git remote add origin https://github.com/sevntu-checkstyle/sevntu.checkstyle.git
        git fetch origin gh-pages:refs/remotes/origin/gh-pages
        git checkout gh-pages

        cd $REPO_HOME_DIR

        return
    }

deployIdea()
    {
        cd $REPO_HOME_DIR/sevntu-checkstyle-idea-extension/
        mvn clean deploy -Plocal-deploy
        if [ "$?" != "0" ]
        then
            echo "build for $REPO_HOME_DIR/sevntu-checkstyle-idea-extension/"
            exit 1
        fi

        cd $REPO_HOME_DIR/gh-pages
        echo "$manualDeploy"
        return
    }

deployEclipse()
    {
        cd $REPO_HOME_DIR
        #echo -n "Enter version number: "
        #read version
        #mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$version -f eclipse-pom.xml
        mvn clean install -f eclipse-pom.xml -Plocal-deploy
        if [ "$?" != "0" ]
        then
            echo "build for eclipse-pom.xml."
            exit 1
        fi

        cd $REPO_HOME_DIR/update-site
        mvn wagon:upload
        cd ../gh-pages
        echo "$manualDeploy"
        return
    }

deployMavenLibrary()
    {
        # As we do not use SNAPSHOT qualifier for development in pom.xml
        # we have to deploy library sevntu-checks always even it overrides existing binaries in maven repository
        # for release build - it will not override binaries
        # for test build - it will override as we need to be sure that in repository,
        #                  we have previous release version but compiled with from new code
        cd $REPO_HOME_DIR/sevntu-checks
        mvn clean javadoc:javadoc deploy -Plocal-deploy
        if [ "$?" != "0" ]
        then
            echo "build for $REPO_HOME_DIR/sevntu-checks."
            exit 1
        fi
        # deployment of Javadoc to static site
        cp -rf target/site/apidocs ../gh-pages

        cd $REPO_HOME_DIR

        # no need push to repository only library it should be done together with IDE plugins release
        #cd ../gh-pages
        #echo "$manualDeploy"
        return
    }

deployToMavenCentral()
    {
        # As we do not use SNAPSHOT qualifier for development in pom.xml
        # we have to deploy library sevntu-checks always even it overrides existing binaries in maven repository
        # for release build - it will not override binaries
        # for test build - it will override as we need to be sure that in repository,
        #                  we have previous release version but compiled with from new code
        cd $REPO_HOME_DIR/sevntu-checks
        mvn clean deploy -DskipStaging=false -Pgpg
        if [ "$?" != "0" ]
        then
            echo "build for $REPO_HOME_DIR/sevntu-checks."
            exit 1
        fi
        cd $REPO_HOME_DIR

        cd $REPO_HOME_DIR/sevntu-checkstyle-maven-plugin/
        mvn clean deploy -DskipStaging=false -Pgpg
        cd $REPO_HOME_DIR

        cd $REPO_HOME_DIR/sevntu-checkstyle-idea-extension/
        mvn clean deploy -DskipStaging=false -Pgpg
        cd $REPO_HOME_DIR

        cd $REPO_HOME_DIR/sevntu-checkstyle-sonar-plugin/
        mvn clean deploy -DskipStaging=false -Pgpg
        cd $REPO_HOME_DIR

        return
    }

deployMavenPlugin()
    {
        cd $REPO_HOME_DIR/sevntu-checkstyle-maven-plugin/
        mvn clean deploy -Plocal-deploy
        if [ "$?" != "0" ]
        then
            echo "build for $REPO_HOME_DIR/sevntu-checkstyle-maven-plugin/."
            exit 1
        fi

        cd $REPO_HOME_DIR/gh-pages
        echo "$manualDeploy"
        return
    }

deploySonar()
    {
        cd $REPO_HOME_DIR/sevntu-checkstyle-sonar-plugin/
        mvn clean install wagon:upload-single
        if [ "$?" != "0" ]
        then
            echo "build for $REPO_HOME_DIR/sevntu-checkstyle-sonar-plugin/"
            exit 1
        fi
        cd $REPO_HOME_DIR/gh-pages
        echo "$manualDeploy"
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
            deployMavenPlugin
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
        --maven)
            prepareForDeploy
            deployMavenLibrary
            deployMavenPlugin
            shift 1
            ;;
        --maven-central)
            deployToMavenCentral
            shift 1
            ;;
        --idea)
            prepareForDeploy
            deployMavenLibrary
            deployIdea
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
