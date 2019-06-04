import sbt._

lazy val root = (project in file("."))
    .settings(
        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-zio" % "1.0-RC4",
            "org.scalatest" %% "scalatest" % "3.0.1",
            "org.coursera" % "dropwizard-metrics-datadog" % "1.1.13"
        )
    )