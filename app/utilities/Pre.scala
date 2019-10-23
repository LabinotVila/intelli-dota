package utilities

import org.apache.spark.sql.{DataFrame, SparkSession}
import utilities.Dataset.getPredictedModel

object Pre {
	def spark(appName: String, master: String) = {
		SparkSession.builder.appName(appName).master(master).getOrCreate
	}

	def dataframe(spark: SparkSession, path: String) = {
		spark.read.option("header", true).option("inferSchema", true).csv(path)
	}

	def doCluster(dataframe: DataFrame): DataFrame = {
		val classifiedModel = getPredictedModel(Constants.CLUSTERED_MODEL)

		val transformed = classifiedModel.transform(dataframe)

		transformed
	}
}
