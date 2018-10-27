lazy val api = project
  .in(file("api"))
  .settings(
    name := "api",
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= testDependencies
  )

def testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)
