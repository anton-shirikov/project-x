package com.projectx.api

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import cats.effect.IOApp
import cats.effect.concurrent.Ref
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Main extends IOApp {

  import cats.effect._
  import cats.syntax.all._

  private val Port = 9090
  private val Path = Paths.get("/home/elama/project-x/data.json")

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      myResources <- Storage.restore(Path)
      binding <- IO.fromFuture(IO(Http().bindAndHandle(routes(myResources), "localhost", Port)))
      _ = logger.info("API server started on port {}", binding.localAddress.getPort)
      _ <- Storage.runPeriodicalSyncToFile(Path, myResources, 1.second)
    } yield ()).handleError(ex => logger.error("Failed to start API server", ex)).as(ExitCode.Success)
  }

  private def routes(myResources: Ref[IO, MyResources]): Route =
    path("health") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, """{"status": "healthy"}"""))
      }
    } ~ Application.routes(myResources)

}