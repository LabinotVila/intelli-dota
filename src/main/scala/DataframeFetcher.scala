import helper.{Fetcher, Variables}
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
			for (gameId <- Variables.startAt to Variables.endAt) {
				val game = Fetcher.fetchGames("https://api.opendota.com/api/matches/50" + gameId)
				if (game != null) matches = matches :+ game

				if (foundGames == 20) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.foreach(x => println(x))

//		gamesDF.write.format("csv").save("created_dataset")
	}
}
