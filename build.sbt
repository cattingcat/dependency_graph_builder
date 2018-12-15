lazy val settings = Seq(
  scalacOptions ++=  Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8",
    "-Ypartial-unification"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val root = (project in file("."))
  .settings(
    settings,
    name := "graph-constructor",
    version := "0.1",
    scalaVersion := "2.12.8",
    //mainClass in Compile := Some(mainPath)
  )


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.5.0-RC1",
  "org.typelevel" %% "cats-effect" % "1.0.0"
)

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "1.0.0",
  "co.fs2" %% "fs2-io" % "1.0.0"
)

val http4sVersion = "0.20.0-SNAPSHOT"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-blaze-server",
  "org.http4s" %% "http4s-blaze-client",
  "org.http4s" %% "http4s-circe"
).map(_ % http4sVersion)

val circeVersion = "0.10.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
libraryDependencies += "io.circe" %% "circe-yaml" % "0.8.0"

libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "5.2.0.201812061821-r"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
