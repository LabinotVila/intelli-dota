import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{NaiveBayes, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}

object Runner {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		// classification inc here
		var dataframe = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv("created_dataset")
		dataframe = dataframe.withColumnRenamed("radiant_win", "label")

		val Array(train, test) = dataframe.randomSplit(Array(0.7, 0.3))

		val myArray = Array(
			"gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score",
			"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration"
		)

		val assembler = new VectorAssembler()
			.setInputCols(myArray)
			.setOutputCol("non-scaled")
		val scaler = new StandardScaler()
			.setInputCol("non-scaled")
			.setOutputCol("features")
			.setWithStd(true)
			.setWithMean(false)

		val algorithm = new RandomForestClassifier()
			.setLabelCol("label")
			.setFeaturesCol("features")
			.setNumTrees(10)
//		val algorithm = new NaiveBayes()

		val pipeline = new Pipeline()
    		.setStages(Array(assembler, scaler, algorithm))

		val left = pipeline.fit(train)

		val predictions = left.transform(test)
		predictions.show(50)

		val evaluator = new MulticlassClassificationEvaluator()
			.setLabelCol("label")
			.setPredictionCol("prediction")
			.setMetricName("accuracy")
		val accuracy = evaluator.evaluate(predictions)

		println(accuracy * 100 + "% accurate!")

	}
}
