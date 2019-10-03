import helper.{Fetcher, Globals}
import models.Match
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level
import util.control.Breaks._

object DataframeFetcher {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[1]").getOrCreate
		import spark.implicits._

		var matches = Seq[Match]()

		var foundGames = 0

		breakable {
			for (gameId <- Globals.startAt to Globals.endAt) {
				val game = Fetcher.fetchGames(Globals.api + gameId)
				if (game != null) matches = matches :+ game

				if (foundGames == 20) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.foreach(x => println(x))

//		gamesDF.write.format("csv").save("created_dataset")
	}
}
