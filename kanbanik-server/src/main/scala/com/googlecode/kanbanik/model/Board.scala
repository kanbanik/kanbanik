package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import com.mongodb.casbah.Imports._
import java.util.ArrayList
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection

class Board(
  var id: Option[ObjectId],
  var name: String,
  var version: Int,
  var workflowitems: Option[List[Workflowitem]]) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: Board = {
    val idToUpdate = id.getOrElse({
      val obj = Board.asDBObject(this)
      using(createConnection) {
        connection => coll(connection, Coll.Boards) += obj
      }

      return Board.asEntity(obj)
    })

    val idObject = MongoDBObject(Board.Fields.id.toString() -> idToUpdate)

    using(createConnection) { conn =>

      val update = $set(
        Board.Fields.version.toString() -> { version + 1 },
        Board.Fields.name.toString() -> name,
        Board.Fields.workflowitems.toString() -> {
          if (workflowitems.isDefined) {
            for { x <- workflowitems.get } yield x.id
          } else {
            null
          }
        })

      Board.asEntity(versionedUpdate(Coll.Boards, versionedQuery(id, version), update))
    }

  }

  def delete {
    versionedDelete(Coll.Boards, versionedQuery(id, version))
  }

}

object Board extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflowitems = Value("workflowitems")
  }

  def all(): List[Board] = {
    var allBoards = List[Board]()
    using(createConnection) { conn =>
      coll(conn, Coll.Boards).find().foreach(board => allBoards = asEntity(board) :: allBoards)
    }
    allBoards
  }

  def byId(id: ObjectId): Board = {
    using(createConnection) { conn =>
      val dbBoards = coll(conn, Coll.Boards).findOne(MongoDBObject(Board.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such board with id: " + id))
      asEntity(dbBoards)
    }
  }

  private def asEntity(dbObject: DBObject) = {
    new Board(
      Some(dbObject.get(Board.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Board.Fields.name.toString()).asInstanceOf[String],
      {
        val res = dbObject.get(Board.Fields.version.toString())
        if (res == null) {
          1
        } else {
          res.asInstanceOf[Int]
        }
      },
      {
        if (dbObject.get(Board.Fields.workflowitems.toString()).isInstanceOf[BasicDBList]) {
          Some(for { x <- dbObject.get(Board.Fields.workflowitems.toString()).asInstanceOf[BasicDBList].toArray().toList } yield Workflowitem.byId(x.asInstanceOf[ObjectId]))
        } else {
          None
        }
      })
  }

  private def asDBObject(entity: Board): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> entity.name,
      Board.Fields.version.toString() -> entity.version,
      Board.Fields.workflowitems.toString() -> {
        if (!entity.workflowitems.isDefined) {
          null
        } else {
          for { x <- entity.workflowitems.get } yield x.id
        }
      })
  }
}