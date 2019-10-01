package helper

import com.google.gson.{Gson, JsonArray}

object Stacks {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		var results = new Array[Integer](2)

		var radiantCampsStacked = 0;
		var direCampsStacked = 0;

		for (x <- 0 to 9) {
			val player = players.get(x)
			val elemCampsStacked = player.getAsJsonObject.get("camps_stacked").getAsInt

			if (x > 4)
				direCampsStacked += elemCampsStacked;
			else
				radiantCampsStacked += elemCampsStacked
		}

		results(0) = radiantCampsStacked
		results(1) = direCampsStacked

		results
	}
}
