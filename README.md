# sbt-codacy-coverage
[![Codacy Badge](https://api.codacy.com/project/badge/grade/a3a8d4988a9045d58578b6c844feefbf)](https://www.codacy.com/app/Codacy/sbt-codacy-coverage)
[![Circle CI](https://circleci.com/gh/codacy/sbt-codacy-coverage/tree/master.svg?style=shield)](https://circleci.com/gh/codacy/sbt-codacy-coverage/tree/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/sbt-codacy-coverage/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/sbt-codacy-coverage)

sbt plugin for uploading Scala code coverage to Codacy https://www.codacy.com

`sbt-codacy-coverage` will only work with:
  * sbt 0.13.5 and higher
  * Java JRE 7 and higher

## Setup

Codacy assumes that coverage is previously configured for your project. As an example, we will configure a Scala project with the `scoverage` sbt plugin.

To start, add the `scoverage` and Codacy sbt plugins into your plugins.sbt file:

```sbt
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.7")
```

Coverage should now be enabled for your project.
To run the tests and create coverage files, type in your terminal:

```sbt
sbt clean coverage test
```

This will create coverage reports for all your tests in your project.
In order to export scoverage report files into cobertura compatible files, just type:

```sbt
sbt coverageReport
```

This will create the required report format for the Codacy plugin to read.
If you have sub-projects within your project, you will want to aggregate all reports into one by running:

```sbt
sbt coverageAggregate
```

## Updating Codacy

To update Codacy, you will need your project integration token. You can find the token in Project -> Settings -> Integrations -> Add Integration -> Project API.

Then set it in your terminal, replacing %Project_Token% with your own token:

```sbt
export CODACY_PROJECT_TOKEN=%Project_Token%
```

> Note: You should keep your API token well **protected**, as it grants owner permissions to your projects.

Next, simply run the Codacy sbt plugin. It will find the current commit and send all details to your project dashboard:

```
sbt codacyCoverage
```

> Note: To send coverage in the enterprise version you should:
```
export CODACY_API_BASE_URL=<Codacy_instance_URL>:16006
```

## Configure your build server

After setting up and testing the coverage, you're ready to setup your build server to automate your process.
Simply replace the step used to run your tests with the process done so far, shown here for brevity sake:

```sbt
sbt clean coverage test
sbt coverageReport
sbt coverageAggregate
sbt codacyCoverage
```

## Failing tests

Failing tests can be caused by the usage of macros in Scala 2.10.
Consider upgrading to Scala 2.11 for full macro support.

## Java 6

Due to a limitation in Java 6, the plugin is unable to establish a connection to codacy.com.
You can run [this script](https://gist.github.com/mrfyda/51cdf48fa0722593db6a) after `sbt codacyCoverage` to upload the generated report to Codacy.

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

- Identify new Static Analysis issues
- Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
- Auto-comments on Commits and Pull Requests
- Integrations with Slack, HipChat, Jira, YouTrack
- Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
