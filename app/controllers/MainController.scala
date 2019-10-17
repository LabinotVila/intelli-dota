package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import utilities.{Dataset, Globals, Statistics, Pre}

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = Pre.spark("Our App", "local[*]")
	val dataframe = Pre.dataframe(spark, Globals.MAIN_ROUTE + Globals.FETCHED_STEAM_DATA)

	def getColumns = Action {
		val columnNames = Dataset.getColumns(spark, dataframe)

		val result = Json.toJson(columnNames)

		Ok(result)
	}

	def postPredict(attributes: Int*) = Action {
		val result = Dataset.predict(spark, dataframe, attributes)

		Ok(result)
	}

	def getCorrelationMatrix() = Action {
		val result = Dataset.getCorrelationMatrix(spark, dataframe)

		Ok(result)
	}

	def getGroupAndCount(attribute: String, partitions: Option[Int]) = Action {
		attribute match {
			case "leaver_status" => Ok(Statistics.getBinary(spark, dataframe.select(attribute), attribute))
			case _ => Ok(Statistics.get(spark, dataframe.select(attribute), attribute, partitions.get))
		}
	}

	def getSample(percentage: Double) = Action {
		val result = Dataset.getSample(spark, dataframe, percentage / 100)

		Ok(result)
	}
}
