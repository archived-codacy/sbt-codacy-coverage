import sbt._

object Dependencies {

  lazy val codacyScalaApi = "com.codacy" %% "codacy-api-scala" % "2.0.0"
  lazy val coverageParser = "com.codacy" %% "coverage-parser" % "1.1.4"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

}
