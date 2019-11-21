name := "ScalaTry"

version := "1.0"

lazy val `scalatry` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
	jdbc, ehcache, ws, specs2 % Test ,guice,
	"org.apache.spark" %% "spark-core" % "2.4.4",
	"org.apache.spark" %% "spark-sql" % "2.4.4",
	"org.apache.spark" %% "spark-mllib" % "2.4.4"
)