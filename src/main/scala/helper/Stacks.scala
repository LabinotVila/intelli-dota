package helper

import com.google.gson.{Gson, JsonArray}

object Stacks {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		val results = new Array[Integer](4) // 0,1 -> {stacks}, 1,2 -> {team GPM}

		var radiantCampsStacked = 0;
		var direCampsStacked = 0;

		var radiantGPM = 0;
		var direGPM = 0;

		for (x <- 0 to 9) {
			val player = players.get(x)

			val elemCampsStacked = player.getAsJsonObject.get("camps_stacked").getAsInt
			val elemGPM = player.getAsJsonObject.get("gold_per_min").getAsInt

			println("-> " + elemGPM)

			// dire side of the map
			if (x > 4) {
				direCampsStacked += elemCampsStacked
				direGPM += elemGPM
			}
				// radiant side of the map
			else {
				radiantCampsStacked += elemCampsStacked
				radiantGPM += elemGPM
			}
		}

		results(0) = radiantCampsStacked
		results(1) = direCampsStacked

		results(2) = radiantGPM
		results(3) = direGPM

		results
	}
}
