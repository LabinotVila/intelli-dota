package classes

import org.apache.spark.api.java.Optional
import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, SparkSession}
import runnable.VizualizationMetrix

abstract class Graph {
	def vizualize(spark: SparkSession, dataframe: DataFrame, attr: String): String
}

class BinaryGraph extends Graph {
	def vizualize(spark: SparkSession, dataframe: DataFrame, attr: String): String = {
		dataframe.groupBy(attr).count().toJSON.collectAsList().toString
	}
}

class NGraph(partitions: Int) extends Graph {
	def vizualize(spark: SparkSession, dataframe: DataFrame, attr: String): String = {
		val temp = "temp"
		val bucket = "bucket"

		val df = dataframe
			.withColumn(temp, dataframe.col(attr).cast(DoubleType))
			.drop(attr).withColumnRenamed(temp, attr)

		val min = df.orderBy(attr).first().getDouble(0)
		val max = df.orderBy(desc(attr)).first().getDouble(0)

		val f = VizualizationMetrix.calculateFormula(min, max, partitions) :+ Double.PositiveInfinity

		val bucketizer = new Bucketizer()
			.setInputCol(attr)
			.setOutputCol(bucket)
			.setSplits(f)

		val partOne = bucketizer.transform(df).groupBy(bucket).count.orderBy(bucket).toJSON.collectAsList.toString

		val partTwo = bucketizer.getSplits.drop(1).dropRight(1).mkString(",")

		partOne.concat("\n[" + partTwo + "]")
	}
}