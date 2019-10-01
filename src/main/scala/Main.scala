import helper.Fetcher
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.ml.classification.{NaiveBayes, RandomForestClassifier}
import org.apache.spark.ml.feature.{LabeledPoint, StringIndexer, VectorAssembler}

object Main {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[1]").getOrCreate
		import spark.implicits._

		val seqOfGames = Fetcher.fetchGames("https://api.opendota.com/api/matches/50", 48581500, 48582000)
		val gamesDF = seqOfGames.toDF

		gamesDF.foreach(x => println(x))
	}
}
