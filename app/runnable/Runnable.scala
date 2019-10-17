package runnable

import helper.Globals
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.stat.{ChiSquareTest, Correlation}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.types._

object Runnable {
	def correlationMatrix(spark: SparkSession, dataframe: DataFrame) = {
		val array = Array("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent", "deaths",
			"denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		val assembler = new VectorAssembler()
			.setInputCols(array)
			.setOutputCol("features")
		val df = assembler.transform(dataframe)

		val data = Correlation.corr(df, "features")
		data.toJSON.collectAsList().toString
	}


	def predict(spark: SparkSession, dataFrame: DataFrame, s: Seq[Int]) = {
		val structureFields = List(
			StructField("gold_per_min", IntegerType, nullable = false),
			StructField("level", IntegerType, nullable = false),
			StructField("leaver_status", IntegerType, nullable = false),
			StructField("xp_per_min", IntegerType, nullable = false),
			StructField("radiant_score", IntegerType, nullable = false),
			StructField("gold_spent", IntegerType, nullable = false),
			StructField("deaths", IntegerType, nullable = false),
			StructField("denies", IntegerType, nullable = false),
			StructField("hero_damage", IntegerType, nullable = false),
			StructField("tower_damage", IntegerType, nullable = false),
			StructField("last_hits", IntegerType, nullable = false),
			StructField("hero_healing", IntegerType, nullable = false),
			StructField("duration", IntegerType, nullable = false)
		)

		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val dataframe = spark.createDataFrame(RDD, StructType(structureFields))

		PipelineModel
			.load(Globals.MAIN_ROUTE + Globals.CLASSIFIED_MODEL).transform(dataframe)
			.select("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
				"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration",
				"probability", "prediction")
	}

	def getMainTableColumns(spark: SparkSession, dataframe: DataFrame) = {
		dataframe.schema.names.filter(x => !x.equals("radiant_win"))
	}
}
