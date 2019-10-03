package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.util.Try

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): Array[Integer] = {
		val results = new Array[Integer](14)

		var radiantGPM, direGPM,
			radiantLevels, direLevels,
			radiantGoldSpent, direGoldSpent,
			radiantLeaverStatus, direLeaverStatus,
			radiantXPM, direXPM,
			radiantHeroDamage, direHeroDamage,
			radiantTowerDamage, direTowerDamage = 0

		for (x <- 0 to 9) {
			val player = players.get(x)

			val elemGPM = player.getAsJsonObject.get("gold_per_min").getAsInt
			val elemLevels = player.getAsJsonObject.get("level").getAsInt
			val elemGoldSpent = player.getAsJsonObject.get("gold_spent").getAsInt
			var elemLeaverStatus = player.getAsJsonObject.get("leaver_status").getAsInt
			var elemXPM = player.getAsJsonObject.get("total_xp").getAsInt
			var elemHeroDamage = player.getAsJsonObject.get("hero_damage").getAsInt
			var elemTowerDamage = player.getAsJsonObject.get("tower_damage").getAsInt

			if (x > 4) {
				direGPM += elemGPM
				direLevels += elemLevels
				direGoldSpent += elemGoldSpent
				direLeaverStatus += elemLeaverStatus
				direXPM += elemXPM
				direHeroDamage += elemHeroDamage
				direTowerDamage += elemTowerDamage
			}
			else {
				radiantGPM += elemGPM
				radiantLevels += elemLevels
				radiantGoldSpent += elemGoldSpent
				radiantLeaverStatus += elemLeaverStatus
				radiantXPM += elemXPM
				radiantHeroDamage += elemHeroDamage
				radiantTowerDamage += elemTowerDamage
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

		results(8) = radiantXPM
		results(9) = direXPM

		results(10) = radiantHeroDamage
		results(11) = direHeroDamage

		results(12) = radiantTowerDamage
		results(13) = direTowerDamage

		results
	}
}
