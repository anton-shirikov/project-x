package com.projectx.api

import org.scalatest.matchers.{MatchResult, Matcher}

trait JsonMatchers {

  type Result[A] = Either[_, A]

  private class JsonMatcher(expectedJson: String) extends Matcher[String] {

    import cats.implicits._

    override def apply(result: String): MatchResult = {
      val areEquivalent = (parse(expectedJson) -> parse(result)).mapN(_ == _).getOrElse(false)

      MatchResult(
        areEquivalent,
        s"$result\ndid not equal as json to expected:\n$expectedJson",
        s"$result\nequaled as json to\n$expectedJson"
      )
    }

    private def parse(input: String): Result[io.circe.Json] = io.circe.jawn.parse(input)

  }

  def equalAsJson(expectedJson: String): Matcher[String] =
    new JsonMatcher(expectedJson)

}

