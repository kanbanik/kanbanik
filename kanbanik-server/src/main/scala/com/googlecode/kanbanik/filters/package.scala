package com.googlecode.kanbanik

import com.googlecode.kanbanik.model.DocumentField
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._

package object filters {

  object Fields extends DocumentField {

  }

  def andFilter(andWith: DBObject, ids: Option[List[ObjectId]], names: Option[List[String]]): DBObject =
    ($and(
      andWith,
      $or(
        makeFilters(ids, names)
      )
    ))


  def makeFilters(ids: Option[List[ObjectId]], names: Option[List[String]]): Seq[DBObject] = {
    val res = (makeFilter(names, Fields.name.toString) :: List(makeFilter(ids, Fields.id.toString))).
      collect { case candidate: Option[DBObject] if candidate.isDefined => candidate.get }

    if (res.isEmpty) {
      // because the empty sequence is not supported
      Seq(MongoDBObject())
    } else {
      res.toSeq
    }
  }

  def makeFilter[T](input: Option[List[T]], filedName: String): Option[DBObject] = {
    if (input.isDefined) {
      Some(filedName $in input.get)
    } else {
      None
    }
  }
}
