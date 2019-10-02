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

				if (gameSkillLevel == Variables.getGameSkill() && countGamesFound <= Variables.getNumberOfFeeds()) {
					countGamesFound += countGamesFound
					println(api + i)
					println(countGamesFound)

					val players = responseAsJSON.get("players").getAsJsonArray
					val stacks = Derivator.countStacks(players)

					responseAsJSON.addProperty("derived_radiant_stacked_camps", stacks(0))
					responseAsJSON.addProperty("derived_dire_stacked_camps", stacks(1))

					responseAsJSON.addProperty("derived_radiant_gpm", stacks(2))
					responseAsJSON.addProperty("derived_dire_gpm", stacks(3))

					responseAsJSON.addProperty("derived_radiant_first_blood", stacks(4))

					responseAsJSON.addProperty("derived_radiant_levels", stacks(5))
					responseAsJSON.addProperty("derived_dire_levels", stacks(6))

					responseAsJSON.addProperty("derived_radiant_obs", stacks(7))
					responseAsJSON.addProperty("derived_dire_obs", stacks(8))

					responseAsJSON.addProperty("derived_radiant_roshans", stacks(9))
					responseAsJSON.addProperty("derived_dire_roshans", stacks(10))

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

		seqOfGames
	}
}
