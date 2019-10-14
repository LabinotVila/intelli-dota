name := "ScalaTry"

version := "1.0"

lazy val `scalatry` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
	jdbc, ehcache, ws, specs2 % Test ,guice,
	"org.apache.spark" %% "spark-core" % "2.4.4",
	"org.apache.spark" %% "spark-sql" % "2.4.4",
	"org.apache.spark" %% "spark-mllib" % "2.4.4")

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.2"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )