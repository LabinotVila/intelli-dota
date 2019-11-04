package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import utilities.{Dataset, Pre, Statistics}

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	val spark = Pre.spark("Our App", "local[*]")
	val steam = Pre.dataframe(spark, sys.props.get("fetched_steam_data").get)
	val kaggle = Pre.dataframe(spark, sys.props.get("kaggle_data").get)
	val classified_kaggle = Pre.doCluster(kaggle)

	def index = Action {
		val string =
			"""List of available API calls:
			  |
			  |# GENERIC
			  |getColumns(kind: String)
			  |/getColumns?kind=steam
			  |
			  |getSample(kind: String, percentage: Double)
			  |/getSample?kind=steam&percentage=10
			  |
			  |getStages(kind: String)
			  |/getStages?kind=kaggle
			  |
			  |getCorrelationMatrix(kind: String)
			  |/getCorrelationMatrix?kind=kaggle
			  |
			  |getStats(kind: String)
			  |/getStats?kind=steam
			  |
			  |getSchema(kind: String)
			  |/getSchema?kind=steam
			  |
			  |getDoubleGroup(kind: String, col1: String, col2: String)
			  |/getGroupAndCount?attribute=leaver_status&partitions=3
			  |
			  |getGroupAndCount(kind: String, attribute: String, partitions: Option[Int])
			  |/getGroupAndCount?kind=kaggle&attribute=gold&partitions=10
			  |
			  |
			  |
			  |# CLASSIFICATION
			  |postPredict(attributes: Int*)
			  |
			  |
			  |
			  |# CLUSTER
			  |postCluster(attributes: Double*)
			  |getClusterCount
			  |/getClusterCount
			  |
			  |getClusterStats
			  |/getClusterStats
			  |""".stripMargin

		Ok(string)
	}

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
			case "steam" => Ok(Dataset.getStages(sys.props.get("classified_model").get))
			case "kaggle" => Ok(Dataset.getStages(sys.props.get("clustered_model").get))
		}
	}
	def getCorrelationMatrix(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getCorrelationMatrix(steam))
			case "kaggle" => Ok(Dataset.getCorrelationMatrix(kaggle))
		}
	}
	def getStats(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getStats(steam))
			case "kaggle" => Ok(Dataset.getStats(kaggle))
			case "rawKaggle" => Ok(Dataset.getRawStats(spark, sys.props.get("raw_kaggle_data").get))
		}
	}
	def getSchema(kind: String): Action[AnyContent] = Action {
		kind match {
			case "steam" => Ok(Dataset.getSchema(steam))
			case "kaggle" => Ok(Dataset.getSchema(kaggle))
		}
	}
	def getDoubleGroup(kind: String, col1: String, col2: String) = Action {
		kind match {
			case "steam" => Ok(Dataset.getDoubleGroup(steam, col1, col2))
			case "kaggle" => Ok(Dataset.getDoubleGroup(kaggle, col1, col2))
		}
	}
	def getGroupAndCount(kind: String, attribute: String, partitions: Option[Int]): Action[AnyContent] = Action {
		kind match {
			case "steam" => {
				attribute match {
					case "leaver_status" | "radiant_win"    => Ok(Statistics.getBinary(steam, attribute))
					case _                                  => Ok(Statistics.get(spark, steam, attribute, partitions.get))
				}
			}
			case "kaggle" => Ok(Statistics.get(spark, kaggle, attribute, partitions.get))
		}
	}

	// CLASSIFICATION ONLY
	def postPredict(attributes: Int*): Action[AnyContent] = Action {
		val result = Dataset.predict(spark, steam, attributes)

		Ok(result)
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
	def getClusterStats: Action[AnyContent] = Action {
		val result = Dataset.getClusterStats(classified_kaggle)

		Ok(result)
	}


}
