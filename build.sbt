import Dependencies._

name := "sbt-codacy-coverage"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions := Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint", "-Xfatal-warnings")

sbtPlugin := true

resolvers ++= Seq(
  DefaultMavenRepository,
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases
)

libraryDependencies ++= Seq(
  codacyScalaApi,
  coverageParser,
  `rapture-json-circe`,
  scalaTest
)

organization := "com.codacy"

organizationName := "Codacy"

organizationHomepage := Some(new URL("https://www.codacy.com"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

startYear := Some(2014)

description := "Codacy Coverage Plugin for Scala"

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("http://www.github.com/codacy/sbt-codacy-coverage/"))

pomExtra :=
  <scm>
    <url>https://github.com/codacy/sbt-codacy-coverage</url>
    <connection>scm:git:git@github.com:codacy/sbt-codacy-coverage.git</connection>
    <developerConnection>scm:git:https://github.com/codacy/sbt-codacy-coverage.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>mrfyda</id>
        <name>Rafael</name>
        <email>rafael [at] codacy.com</email>
        <url>https://github.com/mrfyda</url>
      </developer>
    </developers>
