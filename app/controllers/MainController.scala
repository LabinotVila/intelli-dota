package controllers

import javax.inject._
import play.api.mvc._
import runnable.ClassificationPredicter
import helper.Globals
import org.apache.spark.ml.PipelineModel
import models.MatchCut
import org.apache.spark.sql.SparkSession
import play.api.libs.json.JsValue

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

	/**
	 * Create an Action to render an HTML page with a welcome message.
	 * The configuration in the `routes` file means that this method
	 * will be called when the application receives a `GET` request with
	 * a path of `/`.
	 */
	def index = Action {
		Ok("Welcome to IntelliDota")
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

		val prediction = ClassificationPredicter.predict(tupleAsSeq)

		Ok("Nice")
	}

}
