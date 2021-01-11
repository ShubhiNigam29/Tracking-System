lazy val root := (project in file("."))
  .settings(
    name := "Tracking service",
    version := "0.1",
    scalaVersion := "2.12.12",
    sbtVersion := "1.4.6"
  )

libraryDependencies += "com.tumblr" %% "colossus" % "0.8.3"
libraryDependencies += "com.newmotion" %% "akka-rabbitmq" % "6.0.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.2.0"