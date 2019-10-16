package runnable

import org.apache.spark.ml.feature.{Bucketizer, QuantileDiscretizer}
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

import scala.math
import scala.util.Try

object Visualization {
	def operateOn(spark: SparkSession, dataframe: DataFrame, var1: String): Any = {
//		return dataframe.groupBy(var1).count

		val temp = "temp"

		val df = dataframe
			.withColumn(temp, dataframe.col(var1).cast(DoubleType)).drop(var1)
    		.withColumnRenamed(temp, var1)

		val min = df.orderBy(var1).first().getDouble(0)
		val max = df.orderBy(desc(var1)).first().getDouble(0)
		val mid = (max + min) / 2

		println(Double.NegativeInfinity.getClass)

		val splits = Array(Double.NegativeInfinity, min, mid, max, Double.PositiveInfinity)

		splits.foreach(x => println(x))

		val bucketizer = new Bucketizer()
			.setInputCol(var1)
			.setOutputCol("buckets")
			.setSplits(splits)

		val model = bucketizer.transform(dataframe)

		model.show(30)
	}
}
