lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "BikeShare",
    libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "1.6.0",
        "org.apache.spark" %% "spark-mllib" % "2.0.1"
        )
  )
