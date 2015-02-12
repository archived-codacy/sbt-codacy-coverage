import sbt._

object Dependencies {

  lazy val playJson = "com.typesafe.play" %% "play-json" % "2.3.7"
  lazy val playWs = "com.typesafe.play" %% "play-ws" % "2.3.7"
  lazy val jGit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.2.201501210735-r"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

}
