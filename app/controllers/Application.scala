package controllers

import java.util.UUID
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import roomframework.redis._
import roomframework.command._
import roomframework.command.commands._
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import akka.actor.Cancellable
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {

  lazy val redis = sys.env.get("REDISCLOUD_URL").map(RedisService(_))

  private def createAuthProvider(sid: String) = {
    redis.map(RedisTokenProvider(_, sid)).getOrElse(CacheTokenProvider(sid))
  }

  def index = Action { implicit request =>
    val sid = session.get("sessionId").getOrElse(UUID.randomUUID.toString)
    val token = createAuthProvider(sid).currentToken
    Ok(views.html.index(sid, token)).withSession(
      "sessionId" -> sid
    ).withCookies(Cookie("test", sid))
  }

  def ajax = Action { implicit request =>
    val str = Form("str" -> text).bindFromRequest
    val sid = session.get("sessionId")
    val c = request.cookies.get("test")
    Ok(str + ", " + sid + ", " + c)
  }

  def test1 = WebSocket.using[String] { implicit request =>
    request.headers.get("user-agent").foreach(Logger.info(_))
    val sid = request.session("sessionId")
    val ci = new CommandInvoker() with AuthSupport {
      var schedule: Option[Cancellable] = None

      override def onDisconnect = {
        schedule.foreach(_.cancel)
        schedule = None
        println("!!!!!!!!!!!!!! disconnect")
      }
      addHandler("polling") { command =>
        if (schedule.isEmpty) {
          var seq = 0
          val interval = command.data.as[Int]
          val c = Akka.system.scheduler.schedule(interval seconds, interval seconds) {
            seq += 1
            send(new CommandResponse("polling", JsNumber(seq)))
          }
          schedule = Some(c)
        }
        CommandResponse.None
      }
    }
    val authProvider = createAuthProvider(sid)
    ci.addAuthTokenProvider("room.auth", authProvider)
    ci.addHandler("echo") { command =>
      command.json(command.data)
    }
    ci.addHandler("log", new LogCommand("log: "))
    ci.addAuthHandler("authTest") { command =>
      val ret = command.data.as[String] == "test"
      (ret, command.json(JsBoolean(ret)))
    }
    (ci.in, ci.out)
  }

  def log = WebSocket.using[String] { implicit request =>
    val ci = new CommandInvoker()
    ci.addHandler("log", new LogCommand("log: "))
    (ci.in, ci.out)
  }


}