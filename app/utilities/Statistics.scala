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
		val bucket = "bucket"

		val eminem = dataframe.agg(min(attribute), max(attribute)).collectAsList().get(0)

		val splits = calculateFormula(eminem.getInt(0), eminem.getInt(1), partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(splits)

		val partOne = bucketizer.transform(dataframe).groupBy(bucket).count.orderBy(bucket).toJSON.collectAsList.toString

		val partTwo = bucketizer.getSplits.drop(1).dropRight(1).mkString(",")

		partOne.concat("\n[" + partTwo + "]")
	}

	def calculateFormula(start: Int, end: Int, partitions: Int): Array[Double] = {
		val leftover = (start + end) / partitions

		val chunks = (start until end by leftover).toArray.map(_.toDouble) :+ Double.PositiveInfinity

		Array[Double](Double.NegativeInfinity) ++ chunks
	}
}
