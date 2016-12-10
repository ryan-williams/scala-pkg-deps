organization := "com.runsascoded"
name := "scala-pkg-deps"
version := "1.0.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scala-graph" %% "graph-core" % "1.11.4",
  "org.scala-graph" %% "graph-dot" % "1.11.0",
  "commons-io" % "commons-io" % "2.4"
)

testDeps += libraries.value('test_utils)

mainClass := Some("com.runsascoded.deps.InterPackageDepsToDot")

assemblyIncludeScala := true
