package com.projectx.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

object Main {

  import cats.effect._
  import cats.syntax.all._

  private val Port = 9090

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    IO.fromFuture(IO(Http().bindAndHandle(routes, "localhost", Port))).map { binding =>
      logger.info("API server started on port {}", binding.localAddress.getPort)
    }.handleError(ex => logger.error("Failed to start API server", ex)).unsafeRunSync()
  }

  private def routes: Route =
    path("health") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, """{"status": "healthy"}"""))
      }
    }

}