package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import utilities.{Constants, Dataset, Pre, Statistics}

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = Pre.spark("Our App", "local[*]")
	val steam = Pre.dataframe(spark, Constants.MAIN_ROUTE + Constants.FETCHED_STEAM_DATA)
	val kaggle = Pre.dataframe(spark, Constants.MAIN_ROUTE + Constants.KAGGLE_DATA)
	val classified_kaggle = Pre.doCluster(kaggle)

	// DOUBLE FUNCTIONALITY
	def getColumns(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getColumns(steam))
			case "kaggle" => Ok(Dataset.getColumns(kaggle))
		}
	}
	def getSample(kind: String, percentage: Double): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getSample(steam, percentage / 100))
			case "kaggle" => Ok(Dataset.getSample(kaggle, percentage / 100))
		}
	}
	def getStages(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getStages(Constants.CLASSIFIED_MODEL))
			case "kaggle" => Ok(Dataset.getStages(Constants.CLUSTERED_MODEL))
		}
	}
	def getCorrelationMatrix(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getCorrelationMatrix(steam))
			case "kaggle" => Ok(Dataset.getCorrelationMatrix(kaggle))
		}
	}

	// CLASSIFICATION ONLY
	def postPredict(attributes: Int*): Action[AnyContent] = Action {
		val result = Dataset.predict(spark, steam, attributes)

		Ok(result)
	}
	def getGroupAndCount(attribute: String, partitions: Option[Int]): Action[AnyContent] = Action {
		attribute match {
			case "leaver_status"    => Ok(Statistics.getBinary(steam, attribute))
			case _                  => Ok(Json.toJson(Statistics.get(spark, steam, attribute, partitions.get)))
		}
	}

	// CLUSTER ONLY
	def postCluster(attributes: Double*): Action[AnyContent] = Action {
		val result = Dataset.cluster(spark, kaggle, attributes)

		Ok(result)
	}
	def getClusterCount: Action[AnyContent] = Action {
		val result = Statistics.getBinary(classified_kaggle, "prediction")

		Ok(result)
	}
	def getClusterStats(): Action[AnyContent] = Action {
		val result = Dataset.getClusterStats(classified_kaggle)

		Ok(result)
	}
}
