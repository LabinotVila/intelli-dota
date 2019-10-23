package controllers

import javax.inject._
import org.apache.spark.sql.DataFrame
import play.api.libs.json.Json
import play.api.mvc._
import utilities.{Constants, Dataset, Pre, Statistics}

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = Pre.spark("Our App", "local[*]")
	val steam = Pre.dataframe(spark, Constants.MAIN_ROUTE + Constants.FETCHED_STEAM_DATA)
	val kaggle = Pre.dataframe(spark, Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)

	// DOUBLE FUNCTIONALITY
	def getColumns(kind: String) = Action {
		kind match {
			case "steam" => Ok(Json.toJson(Dataset.getColumns(steam)))
			case "kaggle" => Ok(Json.toJson(Dataset.getColumns(kaggle)))
		}
	}
//	}
//	def getSample(kind: DataFrame, percentage: Double) = Action {
//		val result = Dataset.getSample(kind, percentage / 100)
//
//		Ok(result)
//	}
//
//	// CLASSIFICATION ONLY
//	def getCorrelationMatrix = Action {
//		val result = Dataset.getCorrelationMatrix(steam_dataframe)
//
//		Ok(result)
//	}
//	def postPredict(attributes: Int*) = Action {
//		val result = Dataset.predict(spark, steam_dataframe, attributes)
//
//		Ok(result)
//	}
//	def getGroupAndCount(attribute: String, partitions: Option[Int]) = Action {
//		attribute match {
//			case "leaver_status"    => Ok(Statistics.getBinary(steam_dataframe, attribute))
//			case _                  => Ok(Json.toJson(Statistics.get(spark, steam_dataframe, attribute, partitions.get)))
//		}
//	}
//

//
//	def getStages(path: String) = Action {
//		val model = Dataset.getStages(path)
//
//		Ok(Json.toJson(model))
//	}
//
//	def getCenters() = Action {
//		val result = Dataset.getCenters
//
//		Ok("Y")
//	}
}
