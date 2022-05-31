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
* Deploy JARs `mvn deploy`

## Prepare next version
* Bump all projects version to next version `mvn release:prepare`

## Publish
* Update website with this new version content
* Send email to the discussion group with the new version content
