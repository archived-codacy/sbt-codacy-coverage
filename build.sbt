name := "sbt-codacy-coverage"

crossSbtVersions := Seq("0.13.18", "1.2.8")

scalaVersion := (CrossVersion partialVersion (sbtVersion in pluginCrossBuild).value match {
  case Some((0, 13)) => "2.10.7"
  case Some((1, _)) => "2.12.9"
  case _ => sys error s"Unhandled sbt version ${(sbtVersion in pluginCrossBuild).value}"
})

scalacOptions := Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint", "-Xfatal-warnings")

sbtPlugin := true

libraryDependencies ++= Seq(
  "com.codacy" %% "coverage-parser" % "2.0.12",
  "com.sun.activation" % "javax.activation" % "1.2.0"
)

organizationName := "Codacy"

organizationHomepage := Some(new URL("https://www.codacy.com"))

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

startYear := Some(2014)

description := "Codacy Coverage Plugin for Scala"

scmInfo := Some(
  ScmInfo(
    url("https://github.com/codacy/sbt-codacy-coverage"),
    "scm:git:https://github.com/codacy/sbt-codacy-coverage.git"
  )
)

pgpPassphrase := Option(System.getenv("SONATYPE_GPG_PASSPHRASE")).map(_.toCharArray)

homepage := Some(url("http://www.github.com/codacy/sbt-codacy-coverage/"))

publicMvnPublish
