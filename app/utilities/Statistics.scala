package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.{DataFrame, SparkSession}
import play.api.libs.json.Json

import scala.util.Try

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

		val bucketizerSplits = bucketizer.getSplits

		var list: List[Map[String, String]] = List()
		var map: Map[String, String] = Map()

		for (x <- 0 to bucketizerList.size - 1) {
			map = map +
				("bucket" -> bucketizerList(x)(0).toString) +
				("count" -> bucketizerList(x)(1).toString) +
				("lowerBound" -> bucketizerSplits(x).toString) +
				("upperBound" -> bucketizerSplits(x + 1).toString)

			list = list :+ map
		}

		Json.toJson(list)


	}

	def calculateFormula(start: Int, end: Int, partitions: Int): Array[Double] = {
		val leftover = (end - start) / partitions

		val chunks = (start until end by leftover).toArray.map(_.toDouble).dropRight(1) :+ end.toDouble

		chunks

	}
}
