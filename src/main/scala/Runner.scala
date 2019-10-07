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
			"gold_per_min", "level", "leaver_status", "xp_per_min", "kills", "gold_spent",
			 "deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration"
		)

		val assembler = new VectorAssembler()
			.setInputCols(myArray)
			.setOutputCol("features")

		var left = assembler.transform(dataframe)
		left = left.withColumnRenamed("radiant_win", "label")

		val Array(train, test) = left.randomSplit(Array(0.7, 0.3))

		val model = new NaiveBayes().fit(train)

		val predictions = model.transform(test)
		predictions.show(50)

		val evaluator = new MulticlassClassificationEvaluator()
			.setLabelCol("label")
			.setPredictionCol("prediction")
			.setMetricName("accuracy")
		val accuracy = evaluator.evaluate(predictions)

		println(accuracy * 100 + "% accurate!")

	}
}
