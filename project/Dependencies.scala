import sbt._

object Dependencies {

  lazy val playJson = "com.typesafe.play" %% "play-json" % "2.3.6"
  lazy val playWs = "com.typesafe.play" %% "play-ws" % "2.3.6"
  lazy val jGit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.4.1.201406201815-r"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"

}
