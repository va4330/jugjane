package controllers

import play.api._
import play.api.mvc._

object Browse extends Controller {
  def index = Action {
    Ok(views.html.browse())
  }  
}