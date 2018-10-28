package com.projectx.api

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Application {

  import io.circe.Printer
  import io.circe.Encoder
  import io.circe.generic.semiauto._
  import io.circe.syntax._

  private implicit val myResourcesEncoder: Encoder[MyResources] = deriveEncoder
  private implicit val entryEncoder: Encoder[Entry] = deriveEncoder
  private implicit val tagEncoder: Encoder[Tag] = deriveEncoder

  def routes(myResources: MyResources): Route =
    path("resources") {
      get {
        complete(myResources)
      }
    }

  private implicit def marshaller[A: Encoder]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { a ⇒
      val body = a.asJson.pretty(Printer.noSpaces.copy(dropNullValues = true))
      HttpEntity(MediaTypes.`application/json`, body)
    }
  }

}

