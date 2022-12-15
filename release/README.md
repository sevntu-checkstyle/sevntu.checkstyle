ATTENTION: before release process, verify that 
[ALL CIs](https://github.com/sevntu-checkstyle/sevntu.checkstyle) are finished and green.
Regression testing on New Checks is activated only after merge to master.


Prerequisites) Install xmlstarlet.
```
sudo apt install xmlstarlet
```

1) do clone
```
git clone https://github.com/sevntu-checkstyle/sevntu.checkstyle.git
```

2) do compilation of eclipse-CS plugin (see [SevNTU plugin for EclipseCS plugin compilation](https://github.com/sevntu-checkstyle/sevntu.checkstyle/wiki/SevNTU-plugin-for-EclipseCS-plugin-compilation))

2.1) 
Recheck if that is required to update version of eclipse-cs (make sure tag exists in eclpse-cs repo)
- in [eclipsecs-sevntu-plugin/pom.xml](https://github.com/sevntu-checkstyle/sevntu.checkstyle/blob/master/eclipsecs-sevntu-plugin/pom.xml#L29).
Latest version of elcipse-cs is at https://github.com/checkstyle/eclipse-cs.
https://github.com/sevntu-checkstyle/sevntu.checkstyle/blob/master/sevntu-checks/pom.xml#L19 .

If there was change in eclipse-cs, please send PR for eclipse-cs update first, see [SevNTU plugin for EclipseCS plugin compilation](https://github.com/sevntu-checkstyle/sevntu.checkstyle/wiki/SevNTU-plugin-for-EclipseCS-plugin-compilation)

3.1) Setup version variables
```
OLD_VERSION=$(git describe --abbrev=0)
echo $OLD_VERSION
NEW_VERSION=1.21.0
```

3.2) update version in all files 
Usage: 
```
./pom-version-bump.sh $OLD_VERSION $NEW_VERSION
```


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

4 ) launch `./deploy-all.sh`, make sure that no "[ERROR]" in logs

4.1) If no errors
```
git add .
git commit -m "config: version bump to $NEW_VERSION"
git push origin
```
do tagging for [master branch](https://github.com/sevntu-checkstyle/sevntu.checkstyle/releases): 
```
git tag -a $NEW_VERSION -m 'release $NEW_VERSION'
git push --tags
```

5) 
```
cd /tmp/sevntu.checkstyle.gh/sevntu.checkstyle
# change in .git/config origin to ssh protocol git@github.com:sevntu-checkstyle/sevntu.checkstyle.git
git checkout gh-pages
#git config user.email "email@mail.ru"
git add .
git commit -m "binaries for $NEW_VERSION release"
git push origin gh-pages
```

Expected amount of changes(✚ 11…168), expected updates for binaries:
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

5.2) upload to maven central (partly manual)
Make sure that settings.xml have sonatype credentials for 'sevntu-checkstyle' folder on maven central (backup user is 'sevntu_checkstyle')
```
<settings>
    <servers>
        <server>
            <id>sonatype-nexus-staging</id>
            <username>romanivanov</username>
            <password>xxxxxxxxxxxx</password>
        </server>
   </servers>
    <profiles>
      <profile>
          <!-- "mvn deploy .... -Pgpg" to skip question for passphrase in build time
               Use "gpg \-\-list-keys", "pub   1024D/C6EED5AA 2010-01-13"
                    to get "gpg.keyname" in example it is  value "C6EED5AA"
          -->
          <id>gpg</id>
          <properties>
              <gpg.passphrase>xxxxxxxxx</gpg.passphrase>
              <gpg.keyname>C6EED5AA</gpg.keyname>
          </properties>
      </profile>
    </profiles>
</settings>

```

Come back to source folder. Run deployment to central: 
```
cd -
./deploy.sh --maven-central
```

open https://oss.sonatype.org/#stagingRepositories
login as sevntu_checkstyle

WAIT for deploy completion, find in table smth like "comgithubsevntu-checkstyle-1012"
Select and press "Close", if no error happen then press "Release" when button become enabled (could take few minutes).

5.3) Do update to HTML page to describe changes - [Update for release description in gh pages branch](https://github.com/sevntu-checkstyle/sevntu.checkstyle/wiki/Update-for-release-description-in-gh-pages-branch)

5.4) close milestone, create new - https://github.com/sevntu-checkstyle/sevntu.checkstyle/milestones

5.5) create release from tag - https://github.com/sevntu-checkstyle/sevntu.checkstyle/tags. Put a link to HTML release notes at github release notes - http://sevntu-checkstyle.github.io/sevntu.checkstyle/#1.XX.0

5.6)
recheck that jar appeared at [maven central](https://repo1.maven.org/maven2/com/github/sevntu-checkstyle/sevntu-checks/)

6) update to latest versions and make sure each of them works:

6.1) update projects that use sevntu:

https://github.com/checkstyle/checkstyle/edit/master/pom.xml#L204

https://github.com/checkstyle/contribution/edit/master/checkstyle-tester/pom.xml#L16

https://github.com/checkstyle/eclipse-cs/edit/master/pom.xml#28

6.3) Update other configs for new version:

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/ant-project/ivy.xml#L6 , 

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/gradle-project/build.gradle#L16 , 

https://github.com/sevntu-checkstyle/checkstyle-samples/edit/master/maven-project/pom.xml#L16 

6.4)
update checkstyle version of sevntu in 

https://github.com/checkstyle/checkstyle/edit/master/pom.xml#L206
it should be the same as at 

https://github.com/sevntu-checkstyle/sevntu.checkstyle/edit/master/sevntu-checks/pom.xml#L19


7) ~update https://github.com/sevntu-checkstyle/sevntu.checkstyle/blob/gh-pages/sevntu-checkstyle-default-configuration.xml to contains default configuration WITH ALL PROPERTIES WITH DEFAULT VALUES for new Checks (it is used for Sonar and Ant configuration samples).~ **Obsolete.**


8) create PR to Checkstyle project to use new Checks at https://github.com/checkstyle/checkstyle/blob/master/config/checkstyle_sevntu_checks.xml


9) spread news in social networks, [twitter](https://twitter.com/checkstyle_java)