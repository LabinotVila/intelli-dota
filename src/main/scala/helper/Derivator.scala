package helper

import com.google.gson.{Gson, JsonArray}

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		val results = new Array[Integer](11)

		var radiantCampsStacked = 0
		var direCampsStacked = 0

		var radiantGPM = 0
		var direGPM = 0

		var radiantFirstBlood = 0

		var radiantLevels = 0
		var direLevels = 0

		var radiantObs = 0
		var direObs = 0

		var radiantRoshans = 0
		var direRoshans = 0

		for (x <- 0 to 9) {
			val player = players.get(x)

			val elemCampsStacked = player.getAsJsonObject.get("camps_stacked").getAsInt
			val elemGPM = player.getAsJsonObject.get("gold_per_min").getAsInt
			val elemFirstBlood = player.getAsJsonObject.get("firstblood_claimed").getAsInt
			val elemLevels = player.getAsJsonObject.get("level").getAsInt
			val elemObs = player.getAsJsonObject.get("obs").getAsJsonObject.size()
			val elemRoshan = player.getAsJsonObject.get("roshans_killed").getAsInt

			if (x > 4) {
				direCampsStacked += elemCampsStacked
				direGPM += elemGPM
				if(elemFirstBlood.equals(1)) radiantFirstBlood = 0
				direLevels += elemLevels
				direObs += elemObs
				direRoshans += elemRoshan
			}
			else {
				radiantCampsStacked += elemCampsStacked
				radiantGPM += elemGPM
				if(elemFirstBlood.equals(1)) radiantFirstBlood = 1
				radiantLevels += elemLevels
				radiantObs += elemObs
				radiantRoshans += elemRoshan
			}
		}

		results(0) = radiantCampsStacked
		results(1) = direCampsStacked

		results(2) = radiantGPM
		results(3) = direGPM

		results(4) = radiantFirstBlood

		results(5) = radiantLevels
		results(6) = direLevels

		results(7) = radiantObs
		results(8) = direObs

		results(9) = radiantRoshans
		results(10) = direRoshans

		results
	}
}
