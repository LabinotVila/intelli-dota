import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler

object Runner {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		// classification inc here
		val dataframe = spark.read
			.option("header", true)
    		.option("inferSchema", true)
			.csv("created_dataset")

		val myArray = Array(
			"d_rad_kills", "d_dire_kills",
			"d_rad_gold_per_min", "d_dire_gold_per_min",
			"d_rad_level", "d_dire_level",
			"d_rad_leaver_status", "d_dire_leaver_status",
			"d_rad_xp_per_min", "d_dire_xp_per_min",
			"d_rad_denies", "d_dire_denies",
			"d_rad_hero_damage", "d_dire_hero_damage",
			"d_rad_tower_damage", "d_dire_tower_damage"
		)


		val assembler = new VectorAssembler()
			.setInputCols(myArray)
			.setOutputCol("features")

		var left = assembler.transform(dataframe)
		left = left.withColumnRenamed("d_rad_win", "label")

		val Array(train, test) = left.randomSplit(Array(0.7, 0.3))

		val model = new NaiveBayes().fit(train)

		val predictions = model.transform(test)
		predictions.show(50)
	}
}
