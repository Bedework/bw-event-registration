# Release Notes

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased (6.1.0-SNAPSHOT)

## [6.0.0] - 2025-07-14
* First jakarta release
* Fixed up appinfo to correct targets.
* Updated handling of popups to use native dialog element.
* No form validation on cancel
* Upgrade jquery

## [5.0.6] - 2025-02-06
* Upgrade library versions
* Changes for updated deployment
* Dependency needs meta-inf (for spring)
* Very many changes as this is reimplemented to drop Spring and use the servlet support in bw-util-network. See git commits for details.
* Pre-jakarta

## [5.0.5] - 2024-09-18
* Upgrade library versions
* Fix bad hsql
* Remove unused dependency
* New module to reinstate common resources
* Make EventregException subclass of RuntimeException.

## [5.0.4] - 2024-06-06
* Upgrade library versions
* Fix needed to deal with util.hibernate bug relating to static sessionFactory variable.

## [5.0.3] - 2024-03-22
* Omitted to remove bw-xml as a dependency

## [5.0.2] - 2024-03-22
* Upgrade library versions
* Simplify the configuration utilities.
* Remove dependency on bw-xml

## [5.0.1] - 2024-02-13
* Redo release

## [5.0.0] - 2022-02-12
* Use bedework-parent for builds
*  Upgrade library versions

## [4.0.9] - 2020-03-20
* Update library versions
* Refactor of bedework project

## [4.0.8] - 2019-10-16
* Update library versions
* Lowercase account unless mixed case environment variable BEDEWORK_MIXEDCASE_ACCOUNTS is set to true

## [4.0.7] - 2019-08-27
* Update library versions

## [4.0.6] - 2019-06-27
* Update library versions

## [4.0.5] - 2019-04-15
* Update library versions

## [4.0.4] - 2019-01-07
* Update library versions
* Logging changes

## [4.0.3] - 2018-12-13
* Update library versions
* Logging changes

## [4.0.2] - 2018-11-27
* Update library versions
* Try to fix the comparison for properties. They are supposed to be in a fixed order by name - however the name of wrapped x-properties is a parameter. Use that parameter for the comparison.

## [4.0.1] - 2018-08-04
* Issues after problems with release of 4.0.0

## [4.0.0] - 2018-08-04
* First github/maven release
* Use SOAP for event retrieval
* Much refactoring
* Very many other changes in UI and processing. 
