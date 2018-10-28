import sbt._

object Dependencies {

  object Version {
    val Akka = "2.5.17"
    val AkkaHttp = "10.1.5"
    val ScalaTest = "3.0.5"
    val Logback = "1.2.3"
    val CatsEffect = "1.0.0"
    val Cats = "1.4.0"
    val Circe = "0.10.0"
    val AkkaHttpCirce = "1.22.0"
  }

  val Runtime = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.Akka,
    "com.typesafe.akka" %% "akka-stream" % Version.Akka,
    "com.typesafe.akka" %% "akka-http" % Version.AkkaHttp,
    "com.typesafe.akka" %% "akka-slf4j" % Version.Akka,
    "ch.qos.logback" % "logback-classic" % Version.Logback,
    "org.typelevel" %% "cats-effect" % Version.CatsEffect,
    "org.typelevel" %% "cats-core" % Version.Cats,
    "io.circe" %% "circe-core" % Version.Circe,
    "io.circe" %% "circe-generic-extras" % Version.Circe,
    "io.circe" %% "circe-parser" % Version.Circe,
    "io.circe" %% "circe-java8" % Version.Circe,
    "de.heikoseeberger" %% "akka-http-circe" % Version.AkkaHttpCirce

  )

  val TestDependencies = Seq(
    "org.scalatest" %% "scalatest" % Version.ScalaTest % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % Version.AkkaHttp % Test

  )

  val All: Seq[ModuleID] = TestDependencies ++ Runtime

}
