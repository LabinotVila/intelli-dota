package runnable

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import classes.Graph
import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.sql.types.DoubleType

object Visualization {
	def operateOn(spark: SparkSession, dataframe: DataFrame, var1: String): String = {
		val temp = "temp"
		val bucket = "bucket"

		val df = dataframe
			.withColumn(temp, dataframe.col(var1).cast(DoubleType))
    		.drop(var1).withColumnRenamed(temp, var1)

		val min = df.orderBy(var1).first().getDouble(0)
		val max = df.orderBy(desc(var1)).first().getDouble(0)

		val f = VizualizationMetrix.calculateFormula(min, max, 5) :+ Double.PositiveInfinity

		val bucketizer = new Bucketizer()
			.setInputCol(var1)
			.setOutputCol(bucket)
			.setSplits(f)

		val partOne = bucketizer.transform(df).groupBy(bucket).count.orderBy(bucket).toJSON.collectAsList.toString

		val partTwo = bucketizer.getSplits.drop(1).dropRight(1).mkString(",")

		partOne.concat("\n[" + partTwo + "]")
	}
}
