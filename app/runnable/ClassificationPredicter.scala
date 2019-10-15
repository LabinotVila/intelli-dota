package runnable

import helper.Globals
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession

object ClassificationPredicter {

	def predict(arr: Seq[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]) = {
		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		val model = PipelineModel.load(Globals.MAIN_ROUTE + Globals.CLASSIFIED_MODEL)

		val df = arr.toDF("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
			"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		var predictions = model.transform(df)

		predictions = predictions.select("gold_per_min", "level", "leaver_status", "xp_per_min",
			"radiant_score", "gold_spent",
			"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration",
			"probability", "prediction")

		val json2res = predictions.toJSON.collectAsList

		json2res
	}
}
