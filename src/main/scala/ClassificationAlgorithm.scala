import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}

object ClassificationAlgorithm {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[1]").getOrCreate
		import spark.implicits._

		// classification inc here
	}
}
