## Release Instructions ##
https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-7a.2.PublishSnapshots

### To deploy a snapshot ###
```
mvn clean deploy
```

The snapshot can then be found here: https://oss.sonatype.org/content/repositories/snapshots/com/googlecode/genericdao/

### To deploy a release ###

#### 1. Build JavaDocs ####

#### 2. Do a release to staging repo ####
Maven Release Plugin http://maven.apache.org/plugins/maven-release-plugin/
```
mvn release:clean
mvn release:prepare -DautoVersionSubmodules=true
mvn release:perform
```

NOTE: if prepare fails to authenticate to subversion (for Mac), manually call a svn commit from the command line and then indicate to always allow keychain access.

  1. Login to the Nexus UI (https://oss.sonatype.org)
  1. Go to Staging Repositories page.
  1. Select a staging repository.
  1. Click the **Close** button.

#### 3. Test this release ####

#### 4. Make it so ####
Then click **Release** button. There's no going back at this point.

#### 5. Change version number for sample projects to the new version SNAPSHOT ####
Maven Versions Plugin http://mojo.codehaus.org/versions-maven-plugin/set-mojo.html
```
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.0.0
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.1.0-SNAPSHOT
```

The release plugin takes care of this automatically for the main project.

#### 6. Update wiki ####

#### 7. Blog Post ####