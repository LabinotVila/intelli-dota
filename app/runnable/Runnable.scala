package runnable

import helper.Globals
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession

object Runnable {
	val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
	val main_dataset = spark.read.option("header", true).csv(Globals.MAIN_ROUTE + Globals.FETCHED_STEAM_DATA)
	import spark.implicits._

	def predict(arr: Seq[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]) = {
		val df = arr.toDF("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score",
			"gold_spent", "deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration")

		PipelineModel
			.load(Globals.MAIN_ROUTE + Globals.CLASSIFIED_MODEL).transform(df)
			.select("gold_per_min", "level", "leaver_status", "xp_per_min", "radiant_score", "gold_spent",
				"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing", "duration",
				"probability", "prediction")
    		.toJSON.collectAsList
	}

	def getMainTableColumns = {
		main_dataset.schema.names
	}

	def operateOn(var1: String) = {
		main_dataset.groupBy(var1).count
	}
}
