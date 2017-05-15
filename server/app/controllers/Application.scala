package controllers

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout
import f2m.{Compressed, ConfigUtils, RLECompression}

import scalaj.http._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Marker trait for inner akka messages
  */
sealed trait ActorMessage {}

/**
  * Get request from storage actor
  */
case class Get() extends ActorMessage {}

/**
  * Set request for storage actor
  * @param values items
  */
case class Set(values: Seq[Compressed[String]]) extends ActorMessage {}

/**
  * Application controller
  */
class Application @Inject () (system: ActorSystem) extends Controller {

  val storageActor = system.actorOf(Props[StorageActor], "storage")
  val fetchingActor = system.actorOf(Props(new FetchingActor(storageActor)), "fetcher")

  val refreshDuration: FiniteDuration = ConfigUtils.config().getInt("f2m.refreshTimeoutInSec") seconds

  system.scheduler.schedule(0 seconds, refreshDuration, fetchingActor, ())

  def index: Action[AnyContent] = Action.async {
    implicit val q: akka.util.Timeout = Timeout(10 seconds)
    val res: Future[Any] = storageActor ? Get
    val typed = res.map(x => x.asInstanceOf[Seq[Compressed[String]]])
    typed.map(x => Ok(x.mkString("\n")))
  }
}

/**
  * Storage actor
  */
class StorageActor extends Actor {
  override def receive: Receive = state(List())

  def state(values: Seq[Compressed[String]]): Receive = {
    case Set(x) => context.become(state(x))
    case Get => sender ! values
  }
}

/**
  * Fetching actor
  * @param storage storage actorRef
  */
class FetchingActor(storage: ActorRef) extends Actor {
  val url = ConfigUtils.config().getString("f2m.url")

  override def receive: Receive = {
    case _ =>
      val response = Http(url).asString

      storage ! Set(RLECompression.compress(response.body.split("\n").toList))
  }
}
