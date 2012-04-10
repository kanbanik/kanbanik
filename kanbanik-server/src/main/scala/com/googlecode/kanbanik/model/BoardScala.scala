package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import com.mongodb.casbah.Imports._
import java.util.ArrayList

class BoardScala(
  var id: Option[ObjectId],
  var name: String,
  var workflowitems: Option[List[WorkflowitemScala]]) extends KanbanikEntity {

  def store: BoardScala = {
    val idToUpdate = id.getOrElse({
      val obj = BoardScala.asDBObject(this)
      coll(Coll.Boards) += obj
      return BoardScala.asEntity(obj)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)
    coll(Coll.Boards).update(idObject, $set("name" -> name))
    coll(Coll.Boards).update(idObject, $set("workflowitems" -> {
      val newItems = workflowitems.getOrElse(return null)
      for { x <- newItems } yield x.id
    }))
    BoardScala.byId(idToUpdate)
  }

  def delete {
    coll(Coll.Boards).remove(MongoDBObject("_id" -> id))
  }

}

object BoardScala extends KanbanikEntity {

  def all(): List[BoardScala] = {
    var allBoards = List[BoardScala]()
    coll(Coll.Boards).find().foreach(board => allBoards = asEntity(board) :: allBoards)
    allBoards
  }

  def byId(id: ObjectId): BoardScala = {
    val dbBoards = coll(Coll.Boards).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such board with id: " + id))
    asEntity(dbBoards)
  }

  private def asEntity(dbObject: DBObject) = {
    new BoardScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      {
        if (dbObject.get("workflowitems").isInstanceOf[BasicDBList]) {
          Some(for { x <- dbObject.get("workflowitems").asInstanceOf[BasicDBList].toArray().toList } yield WorkflowitemScala.byId(x.asInstanceOf[ObjectId]))
        } else {
          None
        }
      })
  }

  private def asDBObject(entity: BoardScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "workflowitems" -> {
        if (!entity.workflowitems.isDefined) {
          null
        } else {
          for { x <- entity.workflowitems.get } yield x.id
        }
      })
  }
}