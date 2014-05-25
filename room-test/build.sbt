name := "room-test"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "net.debasishg" % "redisclient_2.10" % "2.12"
)     

play.Project.playScalaSettings
