#!/usr/bin/bash
NEW_VERSION=$1
POM_FILES=(eclipse-pom.xml
eclipsecs-sevntu-plugin-feature/pom.xml
eclipsecs-sevntu-plugin/pom.xml
sevntu-checks/pom.xml
sevntu-checkstyle-idea-extension/pom.xml
sevntu-checkstyle-maven-plugin/pom.xml
sevntu-checkstyle-sonar-plugin/pom.xml
update-site/pom.xml)
for i in "${POM_FILES[@]}"
do
	echo $i
	xmlstarlet ed --ps --pf -N pom="http://maven.apache.org/POM/4.0.0" -u "/pom:project/pom:version" -v $NEW_VERSION $i > $i.new
	mv $i.new $i
done


#https://github.com/sevntu-checkstyle/sevntu.checkstyle/commit/9d90193ba0e47de11b536f3744bc14d28dfb21f7

#aditional version reference
#sevntu-checkstyle-maven-plugin/pom.xml

#special
sed -i "s/Bundle-Version: 1.19.2/Bundle-Version: $NEW_VERSION/" eclipsecs-sevntu-plugin/META-INF/MANIFEST.MF
sed -i "s/1.19.2/$NEW_VERSION/" eclipsecs-sevntu-plugin-feature/feature.xml
