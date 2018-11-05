package com.projectx.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.IO
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.util.{Failure, Success}

object Application {

  import CirceAkkaHttpMarshalling._
  import Marshalling._

  def routes(myResources: Ref[IO, MyResources]): Route =
    pathPrefix("resources") {
      path("entries") {
        (get & parameter("tag".as[String].*)) { requestedTags =>
          val io = for {
            resources <- myResources.get
            tags <- parseTags(requestedTags)
          } yield MyResources.findEntriesByTags(tags)(resources)

          onComplete(io.unsafeToFuture()) {
            case Success(result) => complete(result)
            case Failure(_) => complete(StatusCodes.BadRequest, "Invalid request")
          }
        }
      } ~ get {
        complete(myResources.get.unsafeToFuture())
      } ~ (post & entity(as[Entry])) { entry =>
        complete(myResources.update(MyResources.add(entry)).map(_ => "ok").unsafeToFuture())
      }
    }

  private def parseTag(tag: String): IO[Tag] = IO {
    val Array(name, value) = tag.split(":")
    Tag(name, value)
  }

  private def parseTags(tags: Iterable[String]): IO[Set[Tag]] = {
    tags.toList.map(parseTag).sequence.map(_.toSet)
  }

}
