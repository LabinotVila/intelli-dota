package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Statistics {
	def getBinary(dataframe: DataFrame, attribute: String): String = {
		dataframe.select(attribute).groupBy(attribute).count().toJSON.collectAsList().toString
	}

	def get(spark: SparkSession, dataframe: DataFrame, attribute: String, partitions: Int): String = {
		import spark.implicits._
		val bucket = "bucket"

		val eminem = dataframe.agg(min(attribute), max(attribute)).collectAsList().get(0)

		val splits = calculateFormula(eminem.getInt(0), eminem.getInt(1), partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(splits)

		val partOne = bucketizer.transform(dataframe).groupBy(bucket).count.orderBy(bucket).toJSON.collectAsList().toString
//		val asd: Seq[Row] = bucketizer.transform(dataframe).groupBy(bucket).count.orderBy(bucket).collect().toSeq.map(
//			row => {
//
//				println(row.toString())
//				row
//			}
//		)

		val partTwo = bucketizer.getSplits.drop(1).dropRight(1).mkString(",")

		partOne.concat("\n[" + partTwo + "]")
	}

	def calculateFormula(start: Int, end: Int, partitions: Int): Array[Double] = {
		val leftover = (start + end) / partitions

		val chunks = (start until end by leftover).toArray.map(_.toDouble) :+ Double.PositiveInfinity

		Array[Double](Double.NegativeInfinity) ++ chunks
	}
}
