import sbt._

object Dependencies {

  val codacyScalaApi = "com.codacy" %% "codacy-api-scala" % "3.0.7"
  val coverageParser = "com.codacy" %% "coverage-parser" % "2.0.7"
  val `rapture-json-circe` = "com.propensive" %% "rapture-json-circe" % "2.0.0-M8" exclude("org.spire-math", "jawn-parser_2.11")

}
