package helper

import scala.util.control.Breaks._

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

		breakable {

			for (i <- from to at) {

				val response = requests.get(api + i)

				if (response.statusCode != 404) {

					val responseAsJSON = json.parse(response.text).getAsJsonObject
					val gameSkill = Try(responseAsJSON.get("skill").getAsInt).getOrElse(0)

					if (gameSkill != 0 && gameSkill != 1) {
						if (countGamesFound < Variables.numberOfFeeds) {
							countGamesFound += 1
							println(countGamesFound + ": " + api + i)

							val players = responseAsJSON.get("players").getAsJsonArray
							val stacks = Derivator.countStacks(players)

							stacks.foreach(hash => {
								responseAsJSON.addProperty(hash._1, hash._2)
							})

							val radiantWin = responseAsJSON.get("radiant_win").getAsBoolean
							responseAsJSON.remove("radiant_win")

							if (radiantWin.equals(true))
								responseAsJSON.addProperty("radiant_win", 1)
							else
								responseAsJSON.addProperty("radiant_win", 0)

							val instanceOfMatch: Match = gson.fromJson(responseAsJSON, classOf[Match])

							seqOfGames = seqOfGames :+ instanceOfMatch
						} else {
							break
						}
					}
				}
			}
		}

		seqOfGames.foreach(x => println(x))

		seqOfGames
	}
}
