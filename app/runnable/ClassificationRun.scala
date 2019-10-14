package runnable

import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession

object ClassificationRun {
	def setUpModel() = {
		Classification.setUpModel
	}

	def predict(model: PipelineModel) = {
		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		val newTest = Seq((2585, 139, 0, 3658, 34, 89220, 65, 44, 170629, 2732, 901, 10700, 2549))
			.toDF("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
				"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		val predictions = model.transform(newTest)

		predictions
	}

	def main(args: Array[String]) = {
		val model = ClassificationRun.setUpModel

		ClassificationRun.predict(model).show
	}
}
