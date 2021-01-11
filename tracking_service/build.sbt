lazy val root = (project in file("."))
  .settings(
    name := "Tracking service",
    scalaVersion := "2.10.6"
  )

libraryDependencies += "com.tumblr" %% "colossus" % "0.8.3"
libraryDependencies += "org.zeromq" %% "zeromq-scala-binding" % "0.0.7"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.2.0"