package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import runnable.{Runnable, Visualization}
import helper.Globals

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = helper.SparkSes.createSparkSession("IntelliD", "local[*]")
	val dataframe = helper.DataframeImporter.importDataframe(spark, Globals.MAIN_ROUTE + Globals.FETCHED_STEAM_DATA)

	def index = Action {
		val columnNames = Runnable.getMainTableColumns(spark, dataframe)

		Ok(Json.toJson(columnNames))
	}

	def predict(attributes: Int*) = Action {
		val prediction = Runnable.predict(spark, dataframe, attributes)

		Ok(prediction.toJSON.collectAsList.toString)
	}

	def selectAsCount(var1: String) = Action {
		val fly = Visualization.operateOn(spark, dataframe.select(var1), var1)

//		Ok(fly.toJSON.collectAsList.toString)
		Ok("LIT")
	}
}
