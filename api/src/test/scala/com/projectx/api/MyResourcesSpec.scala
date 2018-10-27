package com.projectx.api

import com.projectx.api.Entry._
import org.scalatest.{FlatSpec, Matchers}

class MyResourcesSpec extends FlatSpec with Matchers {

  "add" should "add new entry to the list of my resources" in {
    val myResources = MyResources(List.empty)
    val newEntry = TextEntry("Something important")

    val updatedResources = MyResources.add(newEntry)(myResources)

    updatedResources.entries should contain(newEntry)
  }

  "findEntriesByTag" should "return all entries for specific tag" in {
    val aTag = Tag("Name", "value")
    val entries = List(
      TextEntry("entry"),
      TextEntry("another entry", Set(aTag, Tag("key", "value"))),
      TextEntry("yet another entry", Set(aTag))
    )
    val myResources = MyResources(entries)

    val results = MyResources.findEntriesByTag(aTag)(myResources)

    results shouldEqual entries.tail
  }

}
