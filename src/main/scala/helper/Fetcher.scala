package helper

import com.google.gson.{Gson, JsonParser}
import models.Match
import helper.Variables

import scala.util.Try

object Fetcher {
	val gson = new Gson()
	val json = new JsonParser()

	def fetchGames(api: String, from: Int, at: Int): Seq[Match] = {
		var seqOfGames = Seq[Match]()

		var countGamesFound = 0

		for (i <- from to at) {

			val response = requests.get(api + i)

			if (response.statusCode != 404) {

				val responseAsJSON = json.parse(response.text).getAsJsonObject
				val gameSkillLevel = Try(responseAsJSON.get("skill").getAsInt).getOrElse(0)

				if (gameSkillLevel == Variables.gameSkill && countGamesFound <= Variables.numberOfFeeds) {
					countGamesFound += 1

					val players = responseAsJSON.get("players").getAsJsonArray
					val stacks = Derivator.countStacks(players)

					responseAsJSON.addProperty("d_rad_gpm", stacks(0))
					responseAsJSON.addProperty("d_dire_gpm", stacks(1))

					responseAsJSON.addProperty("d_rad_levels", stacks(2))
					responseAsJSON.addProperty("d_dire_levels", stacks(3))

					responseAsJSON.addProperty("d_rad_goldSpent", stacks(4))
					responseAsJSON.addProperty("d_dire_goldSpent", stacks(5))

					responseAsJSON.addProperty("d_rad_leaverStatus", stacks(6))
					responseAsJSON.addProperty("d_dire_leaverStatus", stacks(7))

					responseAsJSON.addProperty("d_rad_xpm", stacks(8))
					responseAsJSON.addProperty("d_dire_xpm", stacks(9))

					responseAsJSON.addProperty("d_rad_heroDamage", stacks(10))
					responseAsJSON.addProperty("d_dire_heroDamage", stacks(11))

					responseAsJSON.addProperty("d_rad_towerDamage", stacks(12))
					responseAsJSON.addProperty("d_dire_towerDamage", stacks(13))

					val radiantWin = responseAsJSON.get("radiant_win").getAsBoolean
					responseAsJSON.remove("radiant_win")

					if (radiantWin.equals(true))
						responseAsJSON.addProperty("radiant_win", 1)
					else
						responseAsJSON.addProperty("radiant_win", 0)

					val instanceOfMatch: Match = gson.fromJson(responseAsJSON, classOf[Match])

					seqOfGames = seqOfGames :+ instanceOfMatch
				}
			}
		}

		seqOfGames.foreach(x => println(x))

		seqOfGames
	}
}
