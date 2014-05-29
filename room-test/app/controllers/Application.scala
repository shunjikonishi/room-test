package controllers

import java.util.UUID
import play.api._
import play.api.mvc._
import play.api.libs.json._
import roomframework.command._
import roomframework.command.commands._

object Application extends Controller {

  def index = Action { implicit request =>
    val sid = session.get("sessionId").getOrElse(UUID.randomUUID.toString)
    val token = CacheTokenProvider(sid).getCurrentToken
    Ok(views.html.index(token)).withSession(
      "sessionId" -> sid
    )
  }

  def test1 = WebSocket.using[String] { implicit request =>
    val sid = request.session("sessionId")
    val ci = new CommandInvoker() with AuthSupport {
      override def onDisconnect = {
        println("!!!!!!!!!!!!!! disconnect")
      }
    }
    ci.addAuthTokenProvider("room.auth", CacheTokenProvider(sid))
    ci.addHandler("echo") { command =>
      command.json(command.data)
    }
    ci.addHandler("log", new LogCommand("log: "))
    ci.addAuthHandler("authTest") { command =>
      val ret = command.data.as[String] == "test"
      (ret, command.json(JsBoolean(ret)))
    }
    ci.addHandler("close") { command =>
      ci.close
      CommandResponse.None
    }
    (ci.in, ci.out)
  }

  def log = WebSocket.using[String] { implicit request =>
    val ci = new CommandInvoker()
    ci.addHandler("log", new LogCommand("log: "))
    (ci.in, ci.out)
  }


}