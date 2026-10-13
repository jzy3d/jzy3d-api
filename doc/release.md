# Release steps

## Preliminary checks
* Verify no pull request was forgotten
* Sort issues, either done or for next milestone

## Tests
* Run unit test on all platforms with Github Actions
* Run ITTests of the main API (macOS 10.12)
* Run ITTests of the extension APIs (macOS 10.12)

## Deploy
* Generate and upload Javadoc `mvn javadoc:aggregate`
* Deploy JARs `mvn deploy`, configured as below

### Deploy from the command line

Artifacts are uploaded to the FTP server behind https://maven.jzy3d.org. The `distributionManagement` of
the root `pom.xml` is the only place defining it : its URL comes from the `MAVEN_REPO_URL` environment
variable, and its credentials from the `jzy3d-ftp` server of the Maven settings. Releases go to
`$MAVEN_REPO_URL/maven/releases`, SNAPSHOT versions to `$MAVEN_REPO_URL/maven/snapshots`.

Declare the credentials once in `~/.m2/settings.xml` :

```xml
<settings>
  <servers>
    <server>
      <id>jzy3d-ftp</id>
      <username>your-ftp-user</username>
      <password>your-ftp-password</password>
    </server>
  </servers>
</settings>
```

Set the FTP server URL, without trailing `/maven`, in the console used to deploy :

```bash
# Linux, macOS
export MAVEN_REPO_URL=ftp://ftp.example.org
```

```powershell
# Windows PowerShell
$env:MAVEN_REPO_URL = "ftp://ftp.example.org"
```

```bat
:: Windows cmd
set MAVEN_REPO_URL=ftp://ftp.example.org
```

Then deploy, without running the tests again :

```bash
mvn deploy -DskipTests
```

The URL may also be given only for one command : `mvn deploy -DskipTests -Denv.MAVEN_REPO_URL=ftp://ftp.example.org`.
PanamaGL is deployed the same way, with server id `panama-ftp`.

### Deploy from CI

The CI deploys SNAPSHOT versions after all platforms passed, on push to `master`, when started manually
with `deploy` checked, or for pull requests labeled `deploy-snapshot` (to build projects depending on Jzy3d
against a branch). It needs the `FTP_URL`, `FTP_USERNAME` and `FTP_PASSWORD` secrets of the
`jzy3d-ftp-env` environment (Settings > Environments), with the same values as in the `panama-ftp-env`
environment of PanamaGL.

## Prepare next version
* Bump all projects version to next version `mvn release:prepare`

## Publish
* Update website with this new version content
* Send email to the discussion group with the new version content
