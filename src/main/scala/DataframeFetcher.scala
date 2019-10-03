import helper.{Fetcher, Variables}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level

object DataframeFetcher {
	def main(args: Array[String]) = {

		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[1]").getOrCreate
		import spark.implicits._

		val seqOfGames = Fetcher.fetchGames("https://api.opendota.com/api/matches/50", Variables.startAt, Variables.endAt)

		val gamesDF = seqOfGames.toDF

//		gamesDF.write.format("csv").save("created_dataset")
	}
}
