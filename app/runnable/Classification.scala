package runnable

import helper.Globals
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.SparkSession

object Classification {
	def model(): PipelineModel = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		var dataframe = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv("C:\\Users\\Labinot\\Desktop\\tema_datas\\steam_dataset\\")
		dataframe = dataframe.withColumnRenamed("radiant_win", "label")

		val assembler = new VectorAssembler()
			.setInputCols(Globals.attributes)
			.setOutputCol("non-scaled")
		val scaler = new StandardScaler()
			.setInputCol("non-scaled")
			.setOutputCol("features")
			.setWithStd(true)
			.setWithMean(true)
		val algorithm = new RandomForestClassifier()
			.setLabelCol("label")
			.setFeaturesCol("features")
			.setNumTrees(10)
		val pipeline = new Pipeline()
    		.setStages(Array(assembler, scaler, algorithm))

		val Array(train, test) = dataframe.randomSplit(Array(0.7, 0.3))

		val model = pipeline.fit(train)

		/*
		val evaluator = new MulticlassClassificationEvaluator()
			.setLabelCol("label")
			.setPredictionCol("prediction")
			.setMetricName("accuracy")
		val accuracy = evaluator.evaluate(predictions)

		val predictedModel = model.transform(test)

		val evaluator = new MulticlassClassificationEvaluator()
			.setLabelCol("label")
			.setPredictionCol("prediction")
			.setMetricName("accuracy")
		val accuracy = evaluator.evaluate(predictedModel)

		println(accuracy * 100 + "% accuracy")
		 */

		/*
		val newTest = Seq((2585, 139, 0, 3658, 34, 89220, 65, 44, 170629, 2732, 901, 10700, 2549))
			.toDF("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
				"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		val predictions = model.transform(newTest)
		predictions.show(50)
		*/

		model
	}
}
