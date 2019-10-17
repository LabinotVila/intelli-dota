package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import utilities.{Dataset, Constants, Statistics, Pre}

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = Pre.spark("Our App", "local[*]")
	val dataframe = Pre.dataframe(spark, Constants.MAIN_ROUTE + Constants.FETCHED_STEAM_DATA)

	def getColumns = Action {
		val columnNames = Dataset.getColumns(dataframe)

		val result = Json.toJson(columnNames)

		Ok(result)
	}

	def postPredict(attributes: Int*) = Action {
		val result = Dataset.predict(spark, dataframe, attributes)

		Ok(result)
	}

	def getCorrelationMatrix = Action {
		val result = Dataset.getCorrelationMatrix(dataframe)

		Ok(result)
	}

	def getGroupAndCount(attribute: String, partitions: Option[Int]) = Action {
		attribute match {
			case "leaver_status"    => Ok(Statistics.getBinary(dataframe, attribute))
			case _                  => Ok(Statistics.get(spark, dataframe, attribute, partitions.get))
		}
	}

	def getSample(percentage: Double) = Action {
		val result = Dataset.getSample(dataframe, percentage / 100)

		Ok(result)
	}
}
