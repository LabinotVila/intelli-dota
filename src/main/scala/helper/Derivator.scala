package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.util.Try

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		val results = new Array[Integer](8)

		var radiantGPM, direGPM,
			radiantLevels, direLevels,
			radiantGoldSpent, direGoldSpent,
			radiantLeaverStatus, direLeaverStatus = 0

		for (x <- 0 to 9) {
			val player = players.get(x)

			val elemGPM = player.getAsJsonObject.get("gold_per_min").getAsInt
			val elemLevels = player.getAsJsonObject.get("level").getAsInt
			val elemGoldSpent = player.getAsJsonObject.get("gold_spent").getAsInt
			var elemLeaverStatus = player.getAsJsonObject.get("leaver_status").getAsInt

			if (x > 4) {
				direGPM += elemGPM
				direLevels += elemLevels
				direGoldSpent += elemGoldSpent
				direLeaverStatus += elemLeaverStatus
			}
			else {
				radiantGPM += elemGPM
				radiantLevels += elemLevels
				radiantGoldSpent += elemLevels
				radiantLeaverStatus += elemLeaverStatus
			}
		}

		results(0) = radiantGPM
		results(1) = direGPM

		results(2) = radiantLevels
		results(3) = direLevels

		results(4) = radiantGoldSpent
		results(5) = direGoldSpent

		results(6) = radiantLeaverStatus
		results(7) = direLeaverStatus

		results
	}
}
