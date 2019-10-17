package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{col, max, min}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, SparkSession}

object Statistics {
	def getBinary(spark:SparkSession, dataframe: DataFrame, attr: String): String = {
		dataframe.groupBy(attr).count().toJSON.collectAsList().toString
	}

	def get(spark:SparkSession, dataframe: DataFrame, attribute: String, partitions: Int): String = {
		val temp = "temp"
		val bucket = "bucket"

		val df = dataframe
			.withColumn(temp, dataframe.col(attribute).cast(DoubleType))
			.drop(attribute).withColumnRenamed(temp, attribute)

		val values = df.agg(max(col(attribute)), min(col(attribute))).collectAsList().get(0)

		val f = calculateFormula(values.getDouble(0), values.getDouble(1), partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(f)

		val partOne = bucketizer.transform(df).groupBy(bucket).count.orderBy(bucket).toJSON.collectAsList.toString

		val partTwo = bucketizer.getSplits.drop(1).dropRight(1).mkString(",")

		partOne.concat("\n[" + partTwo + "]")
	}

	def calculateFormula(start: Double, end: Double, partitions: Int): Array[Double] = {
		val leftover = (start + end) / partitions

		var array = Array[Double](Double.NegativeInfinity)

		var sum = 0.0
		for (_ <- 1 to partitions) {
			sum += leftover

			array = array :+ sum
		}

		array = array :+ Double.PositiveInfinity

		array
	}
}
