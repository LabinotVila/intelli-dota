package helper

import org.apache.spark.sql.SparkSession

object SparkSes {
	def createSparkSession(appName: String, master: String) = {
		SparkSession.builder.appName(appName).master(master).getOrCreate
	}
}
