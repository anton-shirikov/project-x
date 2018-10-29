package com.projectx.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Route
import cats.effect.IO
import cats.effect.concurrent.Ref
import com.projectx.api.Entry.TextEntry
import org.scalatest.{FlatSpec, Matchers}

class ApplicationSpec extends FlatSpec with Matchers with ScalatestRouteTest with JsonMatchers {

  "GET /resources" should "return no results when there are no entries" in new Fixture {
    Get("/resources") ~> routes ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] should equalAsJson("""{"entries":[]}""")
    }
  }

  it should "return entries when they are present" in new Fixture (
    TextEntry("some text")
  ) {
    Get("/resources") ~> routes ~> check {
      val expectedJson =
        """
          |{
          |  "entries": [
          |    { "TextEntry": { "text": "some text", "tags": [] } }
          |  ]
          |}
        """.stripMargin
      status shouldEqual StatusCodes.OK
      responseAs[String] should equalAsJson(expectedJson)
    }
  }

  "POST /resources" should "add new entry" in new Fixture {
    Post("/resources", """{"TextEntry":{"text":"some text","tags":[]}}""") ~> routes

    Get("/resources") ~> routes ~> check {
      val expectedJson =
        """
          |{
          |  "entries": [
          |    { "TextEntry": { "text": "some text", "tags": [] } }
          |  ]
          |}
        """.stripMargin
      status shouldEqual StatusCodes.OK
      responseAs[String] should equalAsJson(expectedJson)
    }
  }

  "GET /resources/entries" should "filter out entries by given tags" in new Fixture (
    TextEntry("entry without tags"),
    TextEntry("something about LEAN", Set(Tag("methodology", "lean"))),
    TextEntry("something about fp", Set(Tag("category", "programming"), Tag("topic", "fp"))),
    TextEntry("something about fp in scala", Set(Tag("category", "programming"), Tag("topic", "fp"), Tag("language", "scala"))),
    TextEntry("something about Foreign Policy", Set(Tag("topic", "fp"))),
    TextEntry("something about oop", Set(Tag("category", "programming"), Tag("topic", "oop")))
  ) {
    Get("/resources/entries?tag=category:programming&tag=topic:fp") ~> routes ~> check {
      val expectedJson =
        """
          |[
          |  {
          |    "TextEntry": {
          |      "text": "something about fp",
          |      "tags": [
          |        { "name": "category", "value": "programming" },
          |        { "name": "topic", "value": "fp" }
          |      ]
          |    }
          |  },
          |  {
          |    "TextEntry": {
          |      "text": "something about fp in scala",
          |      "tags": [
          |        { "name": "category", "value": "programming" },
          |        { "name": "topic", "value": "fp" },
          |        { "name": "language", "value": "scala" }
          |      ]
          |    }
          |  }
          |]
        """.stripMargin
      status shouldEqual StatusCodes.OK
      responseAs[String] should equalAsJson(expectedJson)
    }
  }

  it should "fail on invalid tag format" in new Fixture {
    Get("/resources/entries?tag=category&tag=topic:fp") ~> routes ~> check {
      status shouldEqual StatusCodes.BadRequest
    }
  }

  class Fixture(entries: Entry*) {

    val routes: Route = {
      Route.seal(Application.routes(Ref.of[IO, MyResources](MyResources(entries.toList)).unsafeRunSync()))
    }

  }

}

