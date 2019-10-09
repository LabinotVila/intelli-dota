import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{NaiveBayes, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}

import helper.Globals

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

		val assembler = new VectorAssembler()
			.setInputCols(Globals.attributes)
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

		val pipeline = new Pipeline()
    		.setStages(Array(assembler, scaler, algorithm))

		val model = pipeline.fit(train)

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
