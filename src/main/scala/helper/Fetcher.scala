package helper

import com.google.gson.{Gson, JsonParser}
import models.Match

object Fetcher {
	val gson = new Gson()
	val json = new JsonParser()

	def fetchGames(api: String): Match = {

		val response = requests.get(api)

		if (response.statusCode != 200) return null

		val responseAsJSON = json.parse(response.text).getAsJsonObject.get("result").getAsJsonObject

		if(
			responseAsJSON.has("error") || responseAsJSON.get("duration").getAsInt == 0
			|| !responseAsJSON.has("radiant_win")) return null

		val preparedGames = Derivator.prepareGame(responseAsJSON.get("players").getAsJsonArray)

		val radiantWin = responseAsJSON.get("radiant_win").getAsBoolean
		responseAsJSON.remove("radiant_win")

		if (radiantWin) responseAsJSON.addProperty("radiant_win", 1)
		else responseAsJSON.addProperty("radiant_win", 0)

		preparedGames.foreach(hash => responseAsJSON.addProperty(hash._1, hash._2))

		gson.fromJson(responseAsJSON, classOf[Match])
	}
}
