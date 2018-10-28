package com.projectx.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Route
import cats.effect.IO
import cats.effect.concurrent.Ref
import com.projectx.api.Entry.TextEntry
import org.scalatest.{FlatSpec, Matchers}

class ApplicationSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  it should "return no results when there are no entries" in new Fixture {
    Get("/resources") ~> routes ~> check {
      responseAs[String] shouldEqual """{"entries":[]}"""
    }
  }

  it should "return entries when they are present" in new Fixture(
    TextEntry("some text")
  ) {
    Get("/resources") ~> routes ~> check {
      responseAs[String] shouldEqual """{"entries":[{"TextEntry":{"text":"some text","tags":[]}}]}"""
    }
  }

  it should "add new entry" in new Fixture {
    Post("/resources", """{"TextEntry":{"text":"some text","tags":[]}}""") ~> routes

    Get("/resources") ~> routes ~> check {
      responseAs[String] shouldEqual """{"entries":[{"TextEntry":{"text":"some text","tags":[]}}]}"""
    }
  }

  class Fixture(entries: Entry*) {

    val routes: Route = {
      Application.routes(Ref.of[IO, MyResources](MyResources(entries.toList)).unsafeRunSync())
    }

  }

}

