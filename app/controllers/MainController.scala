package controllers

import java.util.Optional

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

	def groupByAndCount(attr: String, partitions: Option[Int]) = Action {
		println(attr)
		attr match {
			case "leaver_status" => {
				val binaryGraph = new classes.BinaryGraph

				Ok(binaryGraph.vizualize(spark, dataframe.select(attr), attr))
			}
			case _ => {
				val NGraph = new classes.NGraph(partitions.get)

				Ok(NGraph.vizualize(spark, dataframe.select(attr), attr))
			}
		}
	}
}
