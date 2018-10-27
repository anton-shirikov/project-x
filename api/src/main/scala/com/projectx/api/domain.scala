package com.projectx.api

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

  def findEntriesByTag(tag: Tag)(myResources: MyResources): List[Entry] = {
    myResources.entries.filter(_.tags.contains(tag))
  }

}
