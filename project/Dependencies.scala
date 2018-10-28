import sbt._

object Dependencies {

  object Version {
    val Akka = "2.5.17"
    val AkkaHttp = "10.1.5"
    val ScalaTest = "3.0.5"
    val Logback = "1.2.3"
  }

  val Runtime = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.Akka,
    "com.typesafe.akka" %% "akka-stream" % Version.Akka,
    "com.typesafe.akka" %% "akka-http" % Version.AkkaHttp,
    "com.typesafe.akka" %% "akka-slf4j" % Version.Akka,
    "ch.qos.logback" % "logback-classic" % Version.Logback

  )

  val TestDependencies = Seq(
    "org.scalatest" %% "scalatest" % Version.ScalaTest % Test
  )

  val All: Seq[ModuleID] = TestDependencies ++ Runtime

}
