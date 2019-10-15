package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import runnable.Runnable

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

	def index = Action {
		val columnNames = Runnable.getMainTableColumns
			.filter(x => !x.equals("radiant_win"))

		Ok(Json.toJson(columnNames))
	}

	def fetchGame = Action {
		Ok("Going to be added maybe?")
	}

	def predict(
		           gold_per_min: Int, level: Int, leaver_status: Int, xp_per_min: Int, radiant_score: Int,
		           gold_spent: Int, deaths: Int, denies: Int, hero_damage: Int, tower_damage: Int, last_hits: Int,
		           hero_healing: Int, duration: Int
	           ) = Action {

		val paramsAsTuple = (gold_per_min, level, leaver_status, xp_per_min, radiant_score, gold_spent, deaths,
			denies, hero_damage, tower_damage, last_hits, hero_healing, duration)

		val tupleAsSeq = Seq(paramsAsTuple)
		val prediction = Runnable.predict(tupleAsSeq).get(0)

		Ok(prediction)

	}

	def selectTwo(var1: Int, var2: Int) = Action {


		Ok("Shekshi")
	}
}
