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

  "findEntriesByTags" should "return nothing when entries with specified tags are not found" in new Fixture {
    val searchTag = Tag("tag1", "value")
    val myResources = MyResources(entries)

    val results = MyResources.findEntriesByTags(Set(searchTag))(myResources)

    results shouldBe empty
  }

  it should "return all entries for specific tag" in new Fixture {
    val searchTag = Tag("tag1", "value")
    val entriesWithSearchedTags = List(
      TextEntry("entry with tag1", Set(searchTag)),
      TextEntry("another entry with tag1", Set(searchTag, Tag("irrelavent", "val")))
    )
    val myResources = MyResources(entries ++ entriesWithSearchedTags)

    val results = MyResources.findEntriesByTags(Set(searchTag))(myResources)

    results shouldEqual entriesWithSearchedTags
  }

  it should "only return entries that contain all tags" in new Fixture {
    val searchTag1 = Tag("tag1", "value")
    val searchTag2 = Tag("tag2", "value")
    val entriesWithSearchedTags = List(
      TextEntry("entry with tag1 and tag2", Set(searchTag1, searchTag2)),
      TextEntry("another entry with tag1, tag2 and other tags", Set(searchTag1, searchTag2, Tag("irrelavent", "val")))
    )
    val allEntities = entriesWithSearchedTags ++ entries ++ List(
      TextEntry("entry with tag1", Set(searchTag1)),
      TextEntry("entry with tag2", Set(searchTag2))
    )
    val myResources = MyResources(entries ++ entriesWithSearchedTags)

    val results = MyResources.findEntriesByTags(Set(searchTag1, searchTag2))(myResources)

    results shouldEqual entriesWithSearchedTags
  }

  class Fixture {
    val entries = List(
      TextEntry("entry without tags"),
      TextEntry("entry with some tag", Set(Tag("irrelevant", "value"))),
    )
  }


}
