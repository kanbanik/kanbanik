package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.dsl.NotOp

class ClassOfService(
  val id: Option[ObjectId],
  val name: String,
  val description: String,
  val colour: String,
  val isPublic: Boolean,
  val version: Int,
  val board: Option[Board]) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: ClassOfService = {
    val idToUpdate = id.getOrElse({
      val obj = ClassOfService.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.ClassesOfService) += obj
      }

      return ClassOfService.asEntity(obj, board)
    })

    val update = $set(
      ClassOfService.Fields.name.toString() -> name,
      ClassOfService.Fields.description.toString() -> description,
      ClassOfService.Fields.colour.toString() -> colour,
      ClassOfService.Fields.isPublic.toString() -> isPublic,
      ClassOfService.Fields.version.toString() -> { version + 1 },
      ClassOfService.Fields.board.toString() -> {
        if (board.isDefined && !isPublic) {
          board.get.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))
        } else {
          null
        }
      })

    ClassOfService.asEntity(versionedUpdate(Coll.ClassesOfService, versionedQuery(idToUpdate, version), update), board)
  }

  def delete {
    versionedDelete(Coll.ClassesOfService, versionedQuery(id.get, version))
  }

  def withId(id: ObjectId) =
    new ClassOfService(Some(id), name, description, colour, isPublic, version, board)

  def withName(name: String) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

  def withDescription(description: String) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

  def withColour(colour: String) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

  def withPublic(isPublic: Boolean) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

  def withVersion(version: Int) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

  def withBoard(board: Option[Board]) =
    new ClassOfService(id, name, description, colour, isPublic, version, board)

}

object ClassOfService extends HasMongoConnection with HasEntityLoader {
  object Fields extends DocumentField {
    val description = Value("description")
    val isPublic = Value("isPublic")
    val colour = Value("colour")
    val board = Value("board")
  }

  def byId(id: ObjectId) = {
    using(createConnection) { conn =>
      val dbObject = coll(conn, Coll.ClassesOfService).findOne(MongoDBObject(ClassOfService.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such classOfService with id: " + id))
      asEntity(dbObject)
    }
  }

  def all() = {
    using(createConnection) { conn =>
      coll(conn, Coll.ClassesOfService).find().map(asEntity(_, None)).toList
    }
  }
  
  def allForBoard(board: Board): List[ClassOfService] = {
    val allOnBoardQuery = MongoDBObject(ClassOfService.Fields.board.toString() -> board.id.get)
    using(createConnection) { conn =>
      val allOnBoard = coll(conn, Coll.ClassesOfService).find(allOnBoardQuery).map(asEntity(_, Some(board))).toList
      allOnBoard ++ allShared
    }
  }

  def allShared() = {
    val allSharedQuery = MongoDBObject(ClassOfService.Fields.isPublic.toString() -> true)
    using(createConnection) { conn =>
      coll(conn, Coll.ClassesOfService).find(allSharedQuery).map(asEntity(_, None)).toList
    }
  }

  def asEntity(dbObject: DBObject): ClassOfService = {
	val board = dbObject.get(ClassOfService.Fields.board.toString()).asInstanceOf[ObjectId]
	if (board == null) {
	  asEntity(dbObject, None)
	} else {
	  asEntity(dbObject, Some(Board().withId(board)))
	}
  }
  
  def asEntity(dbObject: DBObject, board: Option[Board]): ClassOfService = {
    new ClassOfService(
      Some(dbObject.get(ClassOfService.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(ClassOfService.Fields.name.toString()).asInstanceOf[String],
      dbObject.get(ClassOfService.Fields.description.toString()).asInstanceOf[String],
      dbObject.get(ClassOfService.Fields.colour.toString()).asInstanceOf[String],
      dbObject.get(ClassOfService.Fields.isPublic.toString()).asInstanceOf[Boolean],
      dbObject.get(ClassOfService.Fields.version.toString()).asInstanceOf[Int],
      board)
  }

  def asDBObject(entity: ClassOfService): DBObject = {
    MongoDBObject(
      ClassOfService.Fields.id.toString() -> entity.id.getOrElse(new ObjectId),
      ClassOfService.Fields.name.toString() -> entity.name,
      ClassOfService.Fields.version.toString() -> entity.version,
      ClassOfService.Fields.description.toString() -> entity.description,
      ClassOfService.Fields.isPublic.toString() -> entity.isPublic,
      ClassOfService.Fields.colour.toString() -> entity.colour,
      ClassOfService.Fields.board.toString() -> {
        if (entity.board.isDefined && !entity.isPublic) {
          entity.board.get.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))
        } else {
          null
        }
      })
  }
}