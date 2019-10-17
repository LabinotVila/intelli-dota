package helper

import org.apache.spark.sql.SparkSession

object DataframeImporter {
	def importDataframe(spark: SparkSession, path: String) = {
		spark.read.option("header", true).option("inferSchema", true).csv(path)
	}
}
