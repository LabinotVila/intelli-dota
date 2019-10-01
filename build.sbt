name := "Test2"

version := "0.1"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "com.lihaoyi" %% "requests" % "0.1.8",
  "com.google.code.gson" % "gson" % "2.8.5",
  "org.apache.spark" %% "spark-mllib" % "2.4.4"
)

fork in run := true
javaOptions in run ++= Seq(
  "-Dlog4j.debug=true",
  "-Dlog4j.configuration=log4j.properties")
outputStrategy := Some(StdoutOutput)