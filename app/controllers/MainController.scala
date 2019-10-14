package controllers

import javax.inject._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._

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
}
