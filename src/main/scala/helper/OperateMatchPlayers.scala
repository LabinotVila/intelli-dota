package helper
import java.util

import com.google.gson.{Gson, JsonArray, JsonObject, JsonParser}

object OperateMatchPlayers {
	val gson = new Gson()
	val json = new JsonParser()

	def findStatistics(players: JsonArray) = {

		players.forEach(element => {
			val elementCampsStacked = element.getAsJsonObject.get("camps_stacked")
			val campsStackedASMap = new Gson().fromJson(elementCampsStacked, classOf[Map[String, Double]])

			print(campsStackedASMap)
		})
	}
}
