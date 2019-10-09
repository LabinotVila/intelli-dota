import helper.{Fetcher, Globals}
import models.Match
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level
import util.control.Breaks._

object Importer {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate
		import spark.implicits._

		var matches = Seq[Match]()

		var foundGames = 0

		breakable {
			for (gameId <- Globals.startAt to Globals.endAt) {
				val game = Fetcher.fetchGames(Globals.api + gameId + Globals.key)

				if (game != null) {
					println(foundGames + ". Analyzing game: " + Globals.api + gameId + Globals.key)

					matches = matches :+ game
					foundGames += 1
				}

				if (foundGames == Globals.numberOfFeeds) break
			}
		}

		val gamesDF = matches.toDF

		gamesDF.foreach(x => println(x))

		gamesDF.write.format("csv")
			.option("header", true)
    		.mode("overwrite")
			.save("created_dataset")

		println("Successfully exported to the folder!")
	}
}
