package helper

import com.google.gson.{Gson, JsonParser}
import models.Match

import scala.util.Try

object Fetcher {
	val gson = new Gson()
	val json = new JsonParser()

	def fetchGames(api: String): Match = {

		val response = requests.get(api)

		if (response.statusCode == 404) return null

		val responseAsJSON = json.parse(response.text).getAsJsonObject
		val gameSkill = Try(responseAsJSON.get("skill").getAsInt).getOrElse(0)

		if (gameSkill == 0 || gameSkill == 1) return null

		val players = responseAsJSON.get("players").getAsJsonArray
		val stacks = Derivator.countStacks(players)

		stacks.foreach(hash => {
			responseAsJSON.addProperty(hash._1, hash._2)
		})

		gson.fromJson(responseAsJSON, classOf[Match])
	}
}
