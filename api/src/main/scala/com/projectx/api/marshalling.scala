package com.projectx.api

import io.circe.Encoder
import io.circe.Decoder
import io.circe.{Printer => CircePrinter}

object Marshalling {

  import io.circe.generic.semiauto._
  import io.circe.syntax._
  import io.circe.parser._

  val Printer: CircePrinter = CircePrinter.noSpaces.copy(dropNullValues = true)

  implicit val myResourcesEncoder: Encoder[MyResources] = deriveEncoder
  implicit val myResourcesDecoder: Decoder[MyResources] = deriveDecoder
  implicit val entryEncoder: Encoder[Entry] = deriveEncoder
  implicit val entryDecoder: Decoder[Entry] = deriveDecoder
  implicit val tagEncoder: Encoder[Tag] = deriveEncoder
  implicit val tagDecoder: Decoder[Tag] = deriveDecoder

  def encode[T: Encoder](entity: T): String = entity.asJson.pretty(Marshalling.Printer)

  def decode[T: Decoder](bytes: Array[Byte]): Either[io.circe.Error, MyResources] =
    parse(new String(bytes)).flatMap(_.as[MyResources])

}

object CirceAkkaHttpMarshalling {

  import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
  import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
  import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
  import akka.util.ByteString
  import io.circe.syntax._
  import io.circe.jawn

  implicit def marshaller[A: Encoder]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { a ⇒
      val body = a.asJson.pretty(Marshalling.Printer)
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
