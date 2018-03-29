#!/usr/bin/env bash

# Example of commit that should be created base on this script:
# https://github.com/sevntu-checkstyle/sevntu.checkstyle/commit/9d90193ba0e47de11b536f3744bc14d28dfb21f7

OLD_VERSION=$1
NEW_VERSION=$2

if [[ -z "$OLD_VERSION" || -z "$NEW_VERSION" ]]
then
    echo "Old and New version need to be specified"
    exit 1
fi

POM_FILES=(eclipse-pom.xml
sevntu-checks/pom.xml
sevntu-checkstyle-idea-extension/pom.xml
sevntu-checkstyle-maven-plugin/pom.xml
sevntu-checkstyle-sonar-plugin/pom.xml
)
for i in "${POM_FILES[@]}"
do
	echo "Updating: "$i
	xmlstarlet ed --ps -N pom="http://maven.apache.org/POM/4.0.0" \
	    -u "/pom:project/pom:version" -v $NEW_VERSION \
	    $i > $i.new
	mv $i.new $i
done

POM_FILES=(eclipsecs-sevntu-plugin-feature/pom.xml
eclipsecs-sevntu-plugin/pom.xml
update-site/pom.xml
)
for i in "${POM_FILES[@]}"
do
	echo "Updating: "$i
	xmlstarlet ed --ps -N pom="http://maven.apache.org/POM/4.0.0" \
	    -u "/pom:project/pom:parent/pom:version" -v $NEW_VERSION \
	    $i > $i.new
	mv $i.new $i
done

#additional version reference in dependency
FILE=sevntu-checkstyle-maven-plugin/pom.xml
echo "Updating: "$FILE
xmlstarlet ed --ps -N pom="http://maven.apache.org/POM/4.0.0" \
    -u '//pom:project/pom:dependencies/pom:dependency[pom:artifactId="sevntu-checks"]/pom:version' -v $NEW_VERSION \
    $FILE > $FILE.new
mv $FILE.new $FILE

#additional version reference, eclipse file 
FILE=eclipsecs-sevntu-plugin-feature/feature.xml
echo "Updating: "$FILE
xmlstarlet ed --ps \
    -u "/feature[@version='$OLD_VERSION']/@version" -v $NEW_VERSION \
    -u "/feature/plugin[@version='$OLD_VERSION']/@version" -v $NEW_VERSION \
    $FILE > $FILE.new
mv $FILE.new $FILE

#special, non xml file
FILE=eclipsecs-sevntu-plugin/META-INF/MANIFEST.MF
echo "Updating: "$FILE
sed -i "s/Bundle-Version: $OLD_VERSION/Bundle-Version: $NEW_VERSION/" $FILE

