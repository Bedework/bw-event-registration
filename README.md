## bw-event-registration [![Build Status](https://travis-ci.org/Bedework/bw-event-registration.svg)](https://travis-ci.org/Bedework/bw-event-registration)

This project provides an event registration service for
[Bedework](https://www.apereo.org/projects/bedework).

### Requirements

1. JDK 17
2. Maven 3

### Building Locally

> mvn clean install

### Releasing

Releases of this fork are published to Maven Central via Sonatype.

To create a release, you must have:

1. Permissions to publish to the `org.bedework` groupId.
2. `gpg` installed with a published key (release artifacts are signed).

To perform a new release use the release script:

> ./bedework/build/quickstart/linux/util-scripts/release.sh <module-name> "<release-version>" "<new-version>-SNAPSHOT"

When prompted, indicate all updates are committed

For full details, see [Sonatype's documentation for using Maven to publish releases](http://central.sonatype.org/pages/apache-maven.html).

### Release Notes
#### 4.0.0
  * First github/maven release
  * Use SOAP for event retrieval
  * Much refactoring
  * Very many other changes in UI and processing. 

#### 4.0.1
  * Issues after problems with release of 4.0.0

#### 4.0.2
* Update library versions
* Try to fix the comparison for properties. They are supposed to be in a fixed order by name - however the name of wrapped x-properties is a parameter. Use that parameter for the comparison.

#### 4.0.3
* Update library versions
* Logging changes

#### 4.0.4
* Update library versions
* Logging changes

#### 4.0.5
* Update library versions

#### 4.0.6
* Update library versions

#### 4.0.7
* Update library versions

#### 4.0.8
* Update library versions
* Lowercase account unless mixed case environment variable BEDEWORK_MIXEDCASE_ACCOUNTS is set to true

#### 4.0.9
* Update library versions
* Refactor of bedework project

#### 5.0.0
* Use bedework-parent for builds.
* Update library versions

#### 5.0.1
* Redo release

#### 5.0.2
* Update library versions
* Simplify the configuration utilities.
* Remove dependency on bw-xml

#### 5.0.3
* Omitted to remove bw-xml as a dependency

#### 5.0.4
* Upgrade library versions
* Fix needed to deal with util.hibernate bug relating to static sessionFactory variable.

#### 5.0.5
* Upgrade library versions
* Fix bad hsql
* Remove unused dependency
* New module to reinstate common resources
* Make EventregException subclass of RuntimeException.

#### 5.0.6
* Upgrade library versions
* Changes for updated deployment
* Dependency needs meta-inf (for spring)
* Very many changes as this is reimplemented to drop Spring and use the servlet support in bw-util-network. See git commits for details. 
* Pre-jakarta
