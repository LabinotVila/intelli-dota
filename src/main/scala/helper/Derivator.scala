package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.collection.mutable
import scala.collection.mutable.HashMap

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): HashMap[String, Integer] = {
		val results = new HashMap[String, Integer]
		val elements = new mutable.HashMap[String, String]()

		var d_rad_gpm, d_dire_gpm,
		d_rad_levels, d_dire_levels,
		d_rad_goldSpent, d_dire_goldSpent,
		d_rad_leaverStatus, d_dire_leaverStatus,
		d_rad_xpm, d_dire_xpm,
		d_rad_heroDamage, d_dire_heroDamage,
		d_rad_towerDamage, d_dire_towerDamage = 0

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
				d_dire_gpm += elemGPM
				d_dire_levels += elemLevels
				d_dire_goldSpent += elemGoldSpent
				d_dire_leaverStatus += elemLeaverStatus
				d_dire_xpm += elemXPM
				d_dire_heroDamage += elemHeroDamage
				d_dire_towerDamage += elemTowerDamage
			}
			else {
				d_rad_gpm += elemGPM
				d_rad_levels += elemLevels
				d_rad_goldSpent += elemGoldSpent
				d_rad_leaverStatus += elemLeaverStatus
				d_rad_xpm += elemXPM
				d_rad_heroDamage += elemHeroDamage
				d_rad_towerDamage += elemTowerDamage
			}
		}

		results.put("d_rad_gpm", d_rad_gpm)
		results.put("d_dire_gpm", d_dire_gpm)

		results.put("d_rad_levels", d_rad_levels)
		results.put("d_dire_levels", d_dire_levels)

		results.put("d_rad_goldSpent", d_rad_goldSpent)
		results.put("d_dire_goldSpent", d_dire_goldSpent)

		results.put("d_rad_leaverStatus", d_rad_leaverStatus)
		results.put("d_dire_leaverStatus", d_dire_leaverStatus)

		results.put("d_rad_xpm", d_rad_xpm)
		results.put("d_dire_xpm", d_dire_xpm)

		results.put("d_rad_heroDamage", d_rad_heroDamage)
		results.put("d_dire_heroDamage", d_dire_heroDamage)

		results.put("d_rad_towerDamage", d_rad_towerDamage)
		results.put("d_dire_towerDamage", d_dire_towerDamage)

		results
	}
}
