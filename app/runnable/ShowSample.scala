package runnable

import org.apache.spark.sql.{DataFrame, SparkSession}

object ShowSample {
	def showSample(spark: SparkSession, dataframe: DataFrame, percentage: Double) = {
		dataframe.sample(percentage).toJSON.collectAsList().toString
	}
}
