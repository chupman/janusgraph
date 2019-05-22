# JanusGraph Release Process

## Release Checklist

JanusGraph Release checklist

- [ ] Start up new janusgraph-dev thread on release with suggestions on what should be included in the release

- [ ] Make sure all PRs and issues added since last release are associated to the milestone

- [ ] Complete all items associated with milestone or move to a new milestone if necessary

- [ ] Write up a synopsis of changes made in release for release page and vote thread

- [ ] validate all changes have been mreged upstream

- [ ] create janusgraph-dev vote thread and get required votes

- [ ] Tag release

- [ ] Draft release and upload artifacts

- [ ] Upload to sonatype

- [ ] Document lessons learned

## Final source code updates

### Documentation

The changelog needs to be updated with information for the current release. Make sure numbering is by major version, not newest

* docs/changelog.adoc
* docs/doc-versions.adoc

Review these documents also.
Ideally, these should have been updated as pull
requests were merged, but it helps to do another review the POM files (diff
against previous release) to make sure nothing unexpected snuck in.

* NOTICE.txt 
* docs/upgrade.adoc

Commit these changes on your own branch.

### Versions

This command will update the version from `x.y.z-SNAPSHOT` to `x.y.z`.
The
current `pom.xml` files will be backed up with the name `pom.xml.releaseBackup`.
You can also manually update the version on all the pom files.

```
mvn clean release:prepare
```

### Testing
In order to test all of the versions of Cassandra listed create a branch off
of the release commit and mv `.travis.yml.cassandra` to `.travis.yml` and
push it to your fork assuming you having travis testing enabled. If not push
a PR with a do not review title to perform the testing.

### GPG Signing Key

The JanusGraph artifacts are signed with a GPG signature. If you don't already
have a GPG signing key included with the `KEYS` file, you will need to create
one and update the `KEYS` file. The `KEYS` file is posted on the releases page.

### Pull Request

Open a pull request with the doc and version updates. After the updates are
approved and merged, continue on with the release process.

## Final build

### Sonatype account

The release artifacts will be deployed into Sonatype OSS with the Maven deploy
plugin.
You must have an account with Sonatype, and it must be associated
with the JanusGraph org.
https://issues.sonatype.org/browse/OSSRH-28274

### Configure Maven for server passwords

The release artifacts will be uploaded into a staging directory on Sonatype
OSS. If you do not configure Maven with your server passwords, the Maven
deploy plugin will run into a 401 Unauthorized error.

https://maven.apache.org/guides/mini/guide-encryption.html

* Create a master password: `mvn --encrypt-master-password`

* Add the master password to `$HOME/.m2/settings-security.xml` 

```xml
<settingsSecurity>
  <master>{master-passsword}</master>
</settingsSecurity>
```

* Once the master password has been added to `$HOME/.m2/settings-security.xml` , encrypt a server password: `mvn --encrypt-password`

* Create `$HOME/.m2/settings.xml` using your Sonatype username and encrypted server password

```xml
<settings>
    <servers>
        <server>
            <id>sonatype-nexus-snapshots</id>
            <username></username>
            <password></password>
        </server>
        <server>
            <id>sonatype-nexus-staging</id>
            <username></username>
            <password></password>
        </server>
    </servers>
</settings>
```

### GPG passphrase

There must be a better way of doing this, but you can pass it as a command
line parameter to Maven with `-Dgpg.passphrase=$GPG_PASS` otherwise you have to
type it in many times when prompted during the build.   
    
Update: You can also encrypt the gpg key and add it to `$HOME/.m2/settings.xml`
`mvn --encrypt-password`. I still had to enter my GPG pass phrase once.


```xml
<settings>
  <profiles>
    <profile>
      <id>gpg</id>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>{encrypted gpg passphrase}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>gpg</activeProfile>
  </activeProfiles>
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
        <username></username>
        <password></password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
        <username></username>
        <password></password>
    </server>
  </servers>
</settings>
```
### Release build

* Pull down the latest, merged code from GitHub.
* Stash any uncommitted changes.
* Delete untracked files and directories.
* Deploy it.

```
export JG_VER="janusgraph-0.2.3"
git fetch
git pull
git stash save
git clean -fd
cd janusgraph-dist
mvn clean javadoc:jar deploy -Pjanusgraph-release -DskipTests=true
cp janusgraph-dist/janusgraph-dist-hadoop-2/target/${JG_VER}-hadoop2.zip* ~/jg-staging/
cd janusgraph-doc/target/docs/
cd target/docs; mv chunk ${JG_VER}-hadoop2-doc
zip -r ${JG_VER}-hadoop2-doc.zip ${JG_VER}-hadoop2-doc
gpg --armor --detach-sign ${JG_VER}-hadoop2-doc.zip
cp ${JG_VER}-hadoop2-doc.zip* ~/jg-staging/
cd ~/jg-staging
gpg --verify ${JG_VER}-hadoop2.zip.asc ${JG_VER}-hadoop2.zip
```


If it fails due to Inappropriate ioctl for device error, run:
```
export GPG_TTY=$(tty)
```
### Close the staging repository

Log into https://oss.sonatype.org/#welcome and select Staging Repositories under Build Promotion.
If you recently uploaded you can easily find your staged release by doing a descending sort.
Verify that the contents look complete and then check the release before clicking close.

This step will run verification on all the artifacts.
In particular, this will verify that all of the artifacts have a valid GPG signature.
If this step fails, there are changes that must be made before starting the vote.


## Finalize the Release

### Release the staging repository

When the vote is complete and successful, it is time to finalize the release.
This step will publish the release artifacts to the Sonatype release repository.
The staging directory should be automatically deleted after the release is completed.
The release artifacts will be synched over to Maven Central in about 2 hours.

### Update from pre-release to release

Edit the release on GitHub and uncheck the box for pre-release.
Verify that on the release page that the release is now labeled "Latest Release".

### Publish the documentation

Merge the pull request for the release documentation on docs.janusgraph.org.
It takes about a minute for the documentation site to get published.


## Prepare the next snapshot release

```
 mvn versions:set -DnewVersion=0.3.0-SNAPSHOT -DgenerateBackupPoms=false
 ```

Update root `pom.xml` with `janusgraph.compatible.versions` and restore the `<scm>`
to `<tag>HEAD</tag>`

Create an issue to initialize the next SNAPSHOT release.

Open a pull request with the `pom.xml` updates as a fix for that issue.
