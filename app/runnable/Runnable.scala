package runnable

import helper.Globals
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession

object Runnable {
	val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
	import spark.implicits._

	def predict(arr: Seq[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]) = {


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

	def getMainTableColumns = {

		val dataframe = spark.read.option("header", true).csv(Globals.MAIN_ROUTE + Globals.FETCHED_STEAM_DATA)

		dataframe.schema.names
	}
}
