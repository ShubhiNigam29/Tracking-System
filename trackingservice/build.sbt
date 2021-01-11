lazy val root := (project in file("."))
  .settings(
    name := "Tracking Service",
    version := "0.1",
    scalaVersion := "2.12.12",
    sbtVersion := "1.4.6"
  )

libraryDependencies += "com.tumblr" %% "colossus" % "0.8.3"
libraryDependencies += "org.zeromq" %% "zeromq-scala-binding" % "0.0.9"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0"