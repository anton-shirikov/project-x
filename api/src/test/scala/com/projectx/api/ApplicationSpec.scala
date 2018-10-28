package com.projectx.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Route
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

  class Fixture(entries: Entry*) {

    def routes: Route = {
      Application.routes(MyResources(entries.toList))
    }

  }

}

