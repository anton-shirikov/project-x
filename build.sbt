inThisBuild(scalaVersion := "2.12.7")

lazy val api = project
  .in(file("api"))
  .settings(
    name := "api",
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Dependencies.All
  )
