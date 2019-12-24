package utilities

import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import play.api.libs.json.Json

object Statistics {
	def getBinary(dataframe: DataFrame, attribute: String): String = {
		var df = dataframe.select(attribute).groupBy(attribute).count()

		if (attribute.equals("prediction")) {
			df = df.orderBy(attribute)
		}

		df.toJSON.collectAsList.toString
	}

	def get(spark: SparkSession, dataframe: DataFrame, attribute: String, partitions: Int) = {
		val bucket = "bucket"

		val df = dataframe.select(dataframe.col(attribute).cast(DoubleType))
		val eminem = df.agg(min(attribute), max(attribute)).collectAsList().get(0)

		val splits = calculateFormula(eminem, partitions)

		val bucketizer = new Bucketizer().setInputCol(attribute).setOutputCol(bucket).setSplits(splits)

		val bucketizerList = bucketizer.transform(df).groupBy(bucket).count.orderBy(bucket).collect().toList

		val bucketizerSplits = bucketizer.getSplits

		var list: List[Map[String, String]] = List()
		var map: Map[String, String] = Map()

		for (x <- bucketizerList.indices) {

			val theX = bucketizerSplits(x)
			val theY = bucketizerSplits(x + 1)

//			val lowerBound = BigDecimal(theX).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
//			val upperBound = BigDecimal(theY).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

			map = map +
				("bucket" -> bucketizerList(x)(0).toString) +
				("count" -> bucketizerList(x)(1).toString) +
				("lowerBound" -> f"$theX%1.3f") +
				("upperBound" -> f"$theY%1.3f")

			list = list :+ map
		}

		Json.toJson(list)
	}

	def calculateFormula(row: Row, partitions: Int): Array[Double] = {
		val start = row.getDouble(0)
		val end = row.getDouble(1)

		val leftover = (end - start) / partitions

		val chunks = (start until end by leftover).toArray :+ Double.PositiveInfinity

		chunks

	}
}
