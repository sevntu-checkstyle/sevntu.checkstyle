# Sevntu Release Process

## Prerequisites

* Install the necessary software
  * Java
  * Maven
  * `sudo apt install git`
  * `sudo apt install xmlstarlet`
  * `sudo apt install jq`
* Set up your user name and email in Git.
  * `git config user.email`
  * `git config user.name`
* [Follow Sonatype setup](http://central.sonatype.org/pages/ossrh-guide.html), this includes :
  * Setup JIRA account, [link](https://issues.sonatype.org/secure/Signup!default.jspa),
    and request write access to repository
    ([example](https://issues.sonatype.org/browse/OSSRH-11819)).
  * Create a GPG key set and upload
    ([example](https://github.com/sevntu-checkstyle/dsm-maven-plugin/wiki/How-to-config-GPG-and-sign-artifact-with-it))
  * Setup ~/.m2/settings.xml to include the servers
    ([instructions](http://central.sonatype.org/pages/apache-maven.html)).
    Follow Maven guide on how to encrypt passwords
* verify that all CI are green, all CI badges should be green -
  [https://github.com/sevntu-checkstyle/sevntu.checkstyle](https://github.com/sevntu-checkstyle/sevntu.checkstyle)
* Identify next version of Sevntu.

## Manual Release Activities

0)
Prep contribution.
(TODO number)

0.1)
Clone contribution using SSSH and enter it.

```
git clone git@github.com:checkstyle/contribution.git
cd contribution
```

0.2)
Reset it.

```
git reset --hard HEAD
git clean -f -d
git checkout master
git fetch origin
git reset --hard origin/master
```

0.3)
Package releasenotes-builder.

```
cd releasenotes-builder
mvn package
cd ../..
```

**Note:** Ensure there are no errors.

1)
Prep sevntu.

1.1)
Clone Sevntu using SSH and enter it.

```
git clone git@github.com:sevntu-checkstyle/sevntu.checkstyle.git
cd sevntu.checkstyle
```

1.2)
Reset it.

```
git reset --hard HEAD
git clean -f -d
git checkout master
git fetch origin
git reset --hard origin/master
```

2)
Identify, clone and compile the necessary version of Eclipse-CS plugin
(taken from [SevNTU plugin for EclipseCS plugin compilation](https://github.com/sevntu-checkstyle/sevntu.checkstyle/wiki/SevNTU-plugin-for-EclipseCS-plugin-compilation))

2.1)
Set necessary Eclipse-CS version.

```
cd sevntu-checks
ECLIPSE_CS_VERSION=$(mvn -e --no-transfer-progress -q -Dexec.executable='echo' \
  -Dexec.args='${checkstyle.eclipse-cs.version}' --non-recursive \
  org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
echo "Necessary Eclipse-CS Version:"$ECLIPSE_CS_VERSION
```

2.2)
Leave sevntu.

```
cd ../..
```

2.3)
Prep Eclipse-CS.

```
git clone git@github.com:checkstyle/eclipse-cs.git
cd eclipse-cs
```

2.4)
Clone, checkout, compile, and install Eclipse-CS.

```
git checkout $ECLIPSE_CS_VERSION
mvn -e --no-transfer-progress clean install
```

**Note:** Ensure there are no errors.

2.5)
Back to sevntu.

```
cd ../sevntu.checkstyle
```

3)
Start updating all versions in Sevntu.

3.1)
Setup version variables.

```
OLD_VERSION=$(git describe --abbrev=0)
echo $OLD_VERSION
NEW_VERSION=1.21.0
```

**Note**: The value for `NEW_VERSION` should be swapped with the actual new release value.

3.2)
Update version in all files.

```
./pom-version-bump.sh $OLD_VERSION $NEW_VERSION
```

**Note**:
Expected to update - 9 files:

```
eclipse-pom.xml
eclipsecs-sevntu-plugin-feature/feature.xml
eclipsecs-sevntu-plugin-feature/pom.xml
eclipsecs-sevntu-plugin/META-INF/MANIFEST.MF
eclipsecs-sevntu-plugin/pom.xml
sevntu-checks/pom.xml
sevntu-checkstyle-idea-extension/pom.xml
sevntu-checkstyle-sonar-plugin/pom.xml
update-site/pom.xml 
```

3.3)
Manually update `README.textile` with the new Compatibility Matrix.

4)
Deploy all projects.

```
./deploy-all.sh
```

**Note:** Make sure that there are no "[ERROR]" in logs and everything completes successfully.

**Note:** This process will create artifacts for later deployment in another directory.

5)
Start Uploading to Maven Central.

5.1)
Deploy.

```
./deploy.sh --maven-central
```

5.2)
Open https://oss.sonatype.org/#stagingRepositories and login using sevntu credentials.

WAIT for deploy completion, find in table smth like "comgithubsevntu-checkstyle-1012"
(should be nearly instant) and ensure there are no errors.

5.3)
Select and press "Close".

**Note:** Make sure that there are no errors by selecting the sevntu release, and clicking on
the Activity sub-tab. The list of activities should end with "Repository closed" with a green
check. There should also be no error counter next to the sevntu release.

5.4)
Press "Release" when button become enabled (could take few minutes).

6)
Complete release process in Git.

6.1)
Do version bump to Git.

```
git add .
git commit -m "config: version bump to $NEW_VERSION"
git push origin
```

6.2)
Do tagging for [master branch](https://github.com/sevntu-checkstyle/sevntu.checkstyle/releases): 

```
git tag -a $NEW_VERSION -m 'release $NEW_VERSION'
git push --tags
```

6.3)
Swap to the directory which contains all artifacts created by `deploy-all.sh`.

 
```
cd /tmp/sevntu.checkstyle.gh/sevntu.checkstyle
```

6.4)
Change config protocol to SSH.

```
sed -i 's/url = https:\/\/github.com\/sevntu-checkstyle\/sevntu.checkstyle.git/url = git@github.com:sevntu-checkstyle\/sevntu.checkstyle.git/g' .git/config
```

6.5)
Do gh-pages to Git.

```
git checkout gh-pages
git add .
git commit -m "binaries for $NEW_VERSION release"
git push origin gh-pages
```

**Note**:
Expected amount of changes(+11...168), expected updates for binaries:

```
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.jar
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.jar.md5
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.jar.sha1
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.pom
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.pom.md5
maven2/com/github/sevntu/checkstyle/sevntu-checks/1.11.0/sevntu-checks-1.11.0.pom.sha1
maven2/com/github/sevntu/checkstyle/sevntu-checks/maven-metadata.xml
maven2/com/github/sevntu/checkstyle/sevntu-checks/maven-metadata.xml.md5
maven2/com/github/sevntu/checkstyle/sevntu-checks/maven-metadata.xml.sha1
..ithub/sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.jar
...b/sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.jar.md5
.../sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.jar.sha1
...ithub/sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.pom
...b/sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.pom.md5
.../sevntu/checkstyle/sevntu-checkstyle-idea-extension/1.11.0/sevntu-checkstyle-idea-extension-1.11.0.pom.sha1
maven2/com/github/sevntu/checkstyle/sevntu-checkstyle-idea-extension/maven-metadata.xml
maven2/com/github/sevntu/checkstyle/sevntu-checkstyle-idea-extension/maven-metadata.xml.md5
maven2/com/github/sevntu/checkstyle/sevntu-checkstyle-idea-extension/maven-metadata.xml.sha1
sonar/sevntu-checkstyle-sonar-plugin-1.11.0.jar
update-site/artifacts.jar
update-site/content.jar
update-site/features/com.github.sevntu.checkstyle.checks.feature_1.11.0.jar
update-site/plugins/eclipsecs-sevntu-plugin_1.11.0.jar 
```

6.6)
Do release notes to git ([old example](https://github.com/sevntu-checkstyle/sevntu.checkstyle/wiki/Update-for-release-description-in-gh-pages-branch))

```
GITHUB_TOKEN=$(cat ~/.m2/token-checkstyle.txt)

java -jar ~/release/contribution/releasenotes-builder/target/releasenotes-builder-1.0-all.jar \
  -localRepoPath ~/release/sevntu.checkstyle \
  -remoteRepoPath sevntu-checkstyle/sevntu.checkstyle \
  -startRef $OLD_VERSION -releaseNumber $NEW_VERSION \
  -githubAuthToken $GITHUB_TOKEN -generateXdoc -xdocTemplate \
  ~/release/sevntu.checkstyle/sevntu_xdoc_freemarker.template \
  -outputLocation ../

sed -i "/<h2> New and noteworthy<\/h2>/r ../xdoc.xml" index.html

git add index.html
git commit -m "doc: add release notes for $NEW_VERSION"

git push origin gh-pages
```

7)
Complete the remaining manually activities.

7.2) close milestone, create new - https://github.com/sevntu-checkstyle/sevntu.checkstyle/milestones

7.3) create release from tag - https://github.com/sevntu-checkstyle/sevntu.checkstyle/tags. Put a link to HTML release notes at github release notes - http://sevntu-checkstyle.github.io/sevntu.checkstyle/#1.XX.0

7.4)
recheck that jar appeared at [maven central](https://repo1.maven.org/maven2/com/github/sevntu-checkstyle/sevntu-checks/)

8) update to latest versions and make sure each of them works:

8.1) update projects that use sevntu:

Use commit message `config: upgrade sevntu to 1.44.1`

https://github.com/checkstyle/checkstyle/edit/master/pom.xml#L204

https://github.com/checkstyle/contribution/edit/master/checkstyle-tester/pom.xml#L16

https://github.com/checkstyle/eclipse-cs/edit/master/pom.xml#28

https://github.com/checkstyle/sonar-checkstyle/edit/master/pom.xml#L99

8.2) Update other configs for new version:

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/ant-project/ivy.xml#L6 , 

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/gradle-project/build.gradle#L16 , 

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/maven-project/pom.xml#L16 ,

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/maven-ant-project/pom.xml#L16 

8.3)
update checkstyle version of sevntu in 

https://github.com/checkstyle/checkstyle/edit/master/pom.xml#L206
it should be the same as at 

https://github.com/sevntu-checkstyle/sevntu.checkstyle/edit/master/sevntu-checks/pom.xml#L19

9) create PR to Checkstyle project to use new Checks at https://github.com/checkstyle/checkstyle/blob/master/config/checkstyle_sevntu_checks.xml

10) spread news in social networks, [twitter](https://twitter.com/checkstyle_java)