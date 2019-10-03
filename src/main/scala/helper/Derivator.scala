package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.collection.mutable.HashMap

object Derivator {
	val gson = new Gson()

	def countStacks(players: JsonArray): HashMap[String, Integer] = {
		val results = new HashMap[String, Integer]
		val map = Array(
			("gold_per_min", "_gpm"),
			("level", "_levels"),
			("gold_spent", "_goldSpent"),
			("leaver_status", "_leaverStatus"),
			("total_xp", "_xpm"),
			("hero_damage", "_heroDamage"),
			("tower_damage", "_towerDamage")
		)

		map.foreach(tuple => {
			var radAttr, direAttr = 0

			for (i <- 0 to 9) {
				if (i < 5) {
					val player = players.get(i)
					var attrValue = player.getAsJsonObject.get(tuple._1).getAsInt
					radAttr += attrValue
				}
				results.put("d_rad" + tuple._2, radAttr)

				if (i >= 5) {
					val player = players.get(i)
					var attrValue = player.getAsJsonObject.get(tuple._1).getAsInt
					direAttr += attrValue
				}
				results.put("d_dire" + tuple._2, direAttr)
			}
		})

		results
	}
}
