name := "seccamp-l3-simple-cpu"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:reflectiveCalls")
val chiselGroupId = "edu.berkeley.cs"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2",
  chiselGroupId %% "chisel3" % "3.0.+",
  chiselGroupId %% "chisel-iotesters" % "1.1.+"
)
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)
