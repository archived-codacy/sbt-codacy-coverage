import sbt._

object Dependencies {

  lazy val codacyScalaApi = "com.codacy" %% "codacy-api-scala" % "1.0.3"
  lazy val coverageParser = "com.codacy" %% "coverage-parser" % "1.1.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

}
