package helper

import com.google.gson.{Gson, JsonArray, JsonNull}

import scala.collection.mutable.HashMap
import scala.util.Try

object Derivator {
	val gson = new Gson()

	def prepareGame(players: JsonArray): HashMap[String, Integer] = {
		val results = new HashMap[String, Integer]

		Globals.attributes.foreach(attribute => {
			var radAttr = 0

			for (i <- 0 to 5) {
				var attr = Try(players.get(i).getAsJsonObject.get(attribute).getAsInt).getOrElse(0)

				radAttr += attr
			}

			results.put(attribute, radAttr)
		})

		results
	}
}
