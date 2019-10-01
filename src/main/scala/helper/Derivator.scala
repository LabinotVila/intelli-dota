package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.util.Try

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		val results = new Array[Integer](11)

		var radiantCampsStacked, direCampsStacked,
			radiantGPM, direGPM,
			radiantFirstBlood, direFirstBlood,
			radiantLevels, direLevels,
			radiantObs, direObs,
			radiantRoshans, direRoshans = 0

		for (x <- 0 to 9) {
			val player = players.get(x)

			var elemCampsStacked = Try(player.getAsJsonObject.get("camps_stacked").getAsInt).getOrElse(0)
			val elemGPM = player.getAsJsonObject.get("gold_per_min").getAsInt
			val elemFirstBlood = Try(player.getAsJsonObject.get("firstblood_claimed").getAsInt).getOrElse(0)
			val elemLevels = player.getAsJsonObject.get("level").getAsInt
			val elemObs = Try(player.getAsJsonObject.get("obs").getAsJsonObject.size).getOrElse(0)
			val elemRoshan = Try(player.getAsJsonObject.get("roshans_killed").getAsInt).getOrElse(0)

			if (x > 4) {
				direCampsStacked += elemCampsStacked
				direGPM += elemGPM
				direFirstBlood += elemFirstBlood
				direLevels += elemLevels
				direObs += elemObs
				direRoshans += elemRoshan
			}
			else {
				radiantCampsStacked += elemCampsStacked
				radiantGPM += elemGPM
				radiantFirstBlood += elemFirstBlood
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
