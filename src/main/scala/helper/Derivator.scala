package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.collection.mutable.HashMap
import scala.util.Try

object Derivator {
	val gson = new Gson()

	def prepareGame(players: JsonArray): HashMap[String, Integer] = {
		val results = new HashMap[String, Integer]
		val map = Array(
			"gold_per_min", "level", "leaver_status", "xp_per_min", "kills", "deaths", "denies",
			"hero_damage", "tower_damage"
		)

		map.foreach(attribute => {
			var radAttr, direAttr = 0

			for (i <- 0 to 9) {
				var attr = Try(players.get(i).getAsJsonObject.get(attribute).getAsInt).getOrElse(0)

				if (i < 5) radAttr += attr


				if (i >= 5) direAttr += attr
			}
			results.put("d_dire_" + attribute, direAttr)
			results.put("d_rad_" + attribute, radAttr)
		})

		results
	}
}
