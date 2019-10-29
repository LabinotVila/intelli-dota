package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.{DataFrame, SparkSession}
import play.api.libs.json.Json

object Statistics {
	def getBinary(dataframe: DataFrame, attribute: String): String = {
		dataframe.select(attribute).groupBy(attribute).count().toJSON.collectAsList().toString
	}

	def get(spark: SparkSession, dataframe: DataFrame, attribute: String, partitions: Int) = {
		val bucket = "bucket"

		val eminem = dataframe.agg(max(attribute)).collectAsList().get(0)

		val splits = calculateFormula(eminem.getInt(0), partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(splits)

		val bucketizerList = bucketizer.transform(dataframe).groupBy(bucket).count.orderBy(bucket).collect().toList

		val bucketizerSplits = bucketizer.getSplits.drop(1).dropRight(1)

		var list: List[Map[String, String]] = List()
		var map: Map[String, String] = Map()

		for (x <- 0 to bucketizerList.size - 1) {
			map = map + ("bucket" -> bucketizerList(x)(0).toString) + ("count" -> bucketizerList(x)(1).toString) + ("border" -> bucketizerSplits(x).toString)

			list = list :+ map
		}

		Json.toJson(list)
	}

	def calculateFormula(end: Int, partitions: Int): Array[Double] = {
		val leftover = end / partitions

		val chunks = (0 until end by leftover).toArray.map(_.toDouble) :+ Double.PositiveInfinity

		Array[Double](Double.NegativeInfinity) ++ chunks
	}
}
