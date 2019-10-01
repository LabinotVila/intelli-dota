package helper

import com.google.gson.{Gson, JsonParser}
import models.Match

object Fetcher {
  val gson = new Gson()
  val json = new JsonParser()

  def fetchGames(api: String, from: Int, at: Int): Seq[Match] = {
    var seqOfGames = Seq[Match]()

    for (i <- from to at) {
      val response = requests.get(api + i)

      if (response.statusCode != 404) {

        val responseAsJSON = json.parse(response.text).getAsJsonObject

        val players = responseAsJSON.get("players").getAsJsonArray
        OperateMatchPlayers.findStatistics(players)

        val instanceOfMatch: Match = gson.fromJson(responseAsJSON, classOf[Match])

        seqOfGames = seqOfGames :+ instanceOfMatch
      }
    }

    seqOfGames
  }
}
