package utilities

import org.apache.spark.sql.SparkSession

object Pre {
	def spark(appName: String, master: String) = {
		SparkSession.builder.appName(appName).master(master).getOrCreate
	}

	def dataframe(spark: SparkSession, path: String) = {
		spark.read.option("header", true).option("inferSchema", true).csv(path)
	}
}
