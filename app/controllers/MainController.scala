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
			case "steam" => Ok(Dataset.getColumns(steam))
			case "kaggle" => Ok(Dataset.getColumns(kaggle))
		}
	}
	def getSample(kind: String, percentage: Double) = Action {
		kind match {
			case "steam" => Ok(Dataset.getSample(steam, percentage / 100))
			case "kaggle" => Ok(Dataset.getSample(kaggle, percentage / 100))
		}
	}
	def getStages(kind: String) = Action {
		kind match {
			case "steam" => Ok(Dataset.getStages(Constants.CLASSIFIED_MODEL))
			case "kaggle" => Ok(Dataset.getStages(Constants.CLUSTERED_MODEL))
		}
	}
	def getCorrelationMatrix(kind: String) = Action {
		kind match {
			case "steam" => Ok(Dataset.getCorrelationMatrix(steam))
			case "kaggle" => Ok(Dataset.getCorrelationMatrix(kaggle))
		}
	}

	// CLASSIFICATION ONLY
	def postPredict(attributes: Int*) = Action {
		val result = Dataset.predict(spark, steam, attributes)

		Ok(result)
	}
	def getGroupAndCount(attribute: String, partitions: Option[Int]) = Action {
		attribute match {
			case "leaver_status"    => Ok(Statistics.getBinary(steam, attribute))
			case _                  => Ok(Json.toJson(Statistics.get(spark, steam, attribute, partitions.get)))
		}
	}

	// CLUSTER ONLY
	def postCluster(attributes: Double*) = Action {
		val result = Dataset.cluster(spark, kaggle, attributes)

		Ok(result)
	}

}
