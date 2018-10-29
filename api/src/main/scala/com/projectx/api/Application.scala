package com.projectx.api

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import cats.effect.IO
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.util.{Failure, Success}

object Application {

  import io.circe.Encoder
  import io.circe.Decoder
  import io.circe.generic.semiauto._

  import CirceAkkaHttpMarshalling._

  private implicit val myResourcesEncoder: Encoder[MyResources] = deriveEncoder
  private implicit val entryEncoder: Encoder[Entry] = deriveEncoder
  private implicit val entryDecoder: Decoder[Entry] = deriveDecoder
  private implicit val tagEncoder: Encoder[Tag] = deriveEncoder
  private implicit val tagDecoder: Decoder[Tag] = deriveDecoder

  def routes(myResources: Ref[IO, MyResources]): Route =
    pathPrefix("resources") {
      path("entries") {
        (get & parameter("tag".as[String].*)) { requestedTags =>
          val io = for {
            resources <- myResources.get
            tags <- parseTags(requestedTags)
          } yield MyResources.findEntriesByTags(tags.toSet)(resources)

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

object CirceAkkaHttpMarshalling {

  import io.circe.jawn
  import io.circe.Printer
  import io.circe.Encoder
  import io.circe.Decoder
  import io.circe.syntax._

  implicit def marshaller[A: Encoder]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { a ⇒
      val body = a.asJson.pretty(Printer.noSpaces.copy(dropNullValues = true))
      HttpEntity(MediaTypes.`application/json`, body)
    }
  }

  implicit def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] = {
    Unmarshaller.byteStringUnmarshaller.map {
      case ByteString.empty ⇒
        throw Unmarshaller.NoContentException
      case data: ByteString ⇒
        jawn
          .parseByteBuffer(data.asByteBuffer)
          .fold(throw _, identity)
          .as[A]
          .getOrElse(throw new Exception)
    }
  }

}

