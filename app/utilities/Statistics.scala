package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Statistics {
	def getBinary(dataframe: DataFrame, attribute: String): String = {
		dataframe.select(attribute).groupBy(attribute).count().toJSON.collectAsList().toString
	}

	def get(spark: SparkSession, dataframe: DataFrame, attribute: String, partitions: Int) = {
		val bucket = "bucket"

		val eminem = dataframe.agg(min(attribute), max(attribute)).collectAsList().get(0)

		val splits = calculateFormula(eminem.getInt(0), eminem.getInt(1), partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(splits)

		val bucketizerList = bucketizer.transform(dataframe).groupBy(bucket).count.orderBy(bucket).collect().toList

		val bucketizerSplits = bucketizer.getSplits.drop(1).dropRight(1)

		var mapOfMaps: Map[String, Map[String, String]] = Map()
		var map: Map[String, String] = Map()

		for (x <- 0 to bucketizerList.size - 1) {
			map = map + ("count" -> bucketizerList(x)(1).toString) + ("border" -> bucketizerSplits(x).toString)

			mapOfMaps = mapOfMaps + (bucketizerList(x)(0).toString -> map)

		}

		mapOfMaps.toSeq.sortWith(_._1 < _._1)
	}

	def calculateFormula(start: Int, end: Int, partitions: Int): Array[Double] = {
		val leftover = (start + end) / partitions

		val chunks = (start until end by leftover).toArray.map(_.toDouble) :+ Double.PositiveInfinity

		Array[Double](Double.NegativeInfinity) ++ chunks
	}
}
