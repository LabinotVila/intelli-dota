package controllers

import javax.inject._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Person(firstName: String, lastName: String)

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  val json: JsValue = Json.parse("""
{
  "user": {
    "name" : "toto",
    "age" : 25,
    "email" : "toto@jmail.com",
    "isAlive" : true,
    "friend" : {
  	  "name" : "tata",
  	  "age" : 20,
  	  "email" : "tata@coldmail.com"
    }
  }
}
""")

  def index = Action {
    Ok(views.html.index("Welcome"))
  }

  def columns = Action {
    Ok(json)
  }


  def getS(radiantWin: String) = play.mvc.Results.ok(
    "Getting from url: " + radiantWin
  )
}
