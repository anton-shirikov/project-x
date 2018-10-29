package com.projectx.api

// Annotations / Labels vs Tags (key/value vs pure value)
case class Tag(name: String, value: String)

sealed trait Entry {
  def tags: Set[Tag]
}

object Entry {
  case class TextEntry(text: String, tags: Set[Tag] = Set.empty) extends Entry
}

case class MyResources(entries: List[Entry])

object MyResources {

  def add(resource: Entry)(myResources: MyResources): MyResources = {
    MyResources(resource :: myResources.entries)
  }

  def findEntriesByTags(tags: Set[Tag])(myResources: MyResources): List[Entry] = {
    myResources.entries.filter(e => tags.diff(e.tags).isEmpty)
  }

}
