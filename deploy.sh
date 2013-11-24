#!/bin/sh
usage="$(basename "$0") [--help --all --eclipse --sonar --maven --idea]
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

    cd gh-pages && git add . && git commit -m \"new version deploy\" && git push
		"
prepareForDeploy()
	{
		#clean
		rm -rf gh-pages

		#compile and install library sevntu-checks
		cd sevntu-checks
		mvn install
		cd ../

		#prepare folders for update-site and our release maven repository
		mkdir gh-pages
		cd ./gh-pages

		git init
		git remote add origin https://github.com/sevntu-checkstyle/sevntu.checkstyle.git
		git fetch origin gh-pages:refs/remotes/origin/gh-pages
		git checkout gh-pages
		return
	}		

deployAll()
	{
		deployIdea
		deployEclipse
		deployMaven
		deploySonar
		echo "$manualDeploy"
		return
	}

deployIdea()
	{
		cd ../sevntu-checks/
		mvn deploy
		cd ../sevntu-checkstyle-idea-extension/
		mvn deploy
		cd ../gh-pages
		echo "$manualDeploy"
		return
	}

deployEclipse()
	{
		cd ..
		#echo -n "Enter version number"
		#read version
		#mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$version -f eclipse-pom.xml
		mvn clean install -f eclipse-pom.xml
		cd update-site
		mvn wagon:upload
		cd ../gh-pages
		echo "$manualDeploy"
		return
	}

deployMaven()
	{
		cd ../sevntu-checks/
		mvn deploy
		cd ../sevntu-checkstyle-maven-plugin/
		mvn deploy
		cd ../gh-pages
		echo "$manualDeploy"
		return
	}

deploySonar()
	{
		cd ../sevntu-checkstyle-sonar-plugin/
		mvn install wagon:upload-single
		cd ../gh-pages
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
			echo "[ERROR] Deploy 'all' is not supported/tested yet"
			# RI: no need to deploy sevntu-checks on each deployXXXX function call
            #prepareForDeploy
            #deployAll
            shift 1
            ;;
        --eclipse-cs)
            prepareForDeploy
            deployEclipse
            shift 1
            ;;
        --sonar)
            prepareForDeploy
            deploySonar
            shift 1
            ;;
        --maven)
            prepareForDeploy
            deployMaven
            shift 1
            ;;
        --idea)
            prepareForDeploy
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
