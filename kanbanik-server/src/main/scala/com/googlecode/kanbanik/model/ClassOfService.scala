package com.googlecode.kanbanik.model

import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$set
import com.googlecode.kanbanik.db.HasEntityLoader

class ClassOfService(
  val id: Option[ObjectId],
  val name: String,
  val description: String,
  val colour: String,
  val isPublic: Boolean,
  val version: Int,
  val board: Board) extends HasMongoConnection with HasMidAirCollisionDetection {

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
      ClassOfService.Fields.board.toString() -> board.id.getOrElse(throw new IllegalArgumentException("The board has to exist!")))

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
  
  def withBoard(board: Board) =
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

  def allForBoard(board: Board): List[ClassOfService] = {
    using(createConnection) { conn =>
      coll(conn, Coll.ClassesOfService).find(MongoDBObject(ClassOfService.Fields.board.toString() -> board.id.get)).map(asEntity(_, board)).toList
    }
  }

  def asEntity(dbObject: DBObject): ClassOfService = {
    val boardId = dbObject.get(ClassOfService.Fields.board.toString()).asInstanceOf[ObjectId]
    val board = loadBoard(boardId, false).getOrElse(null)
    asEntity(dbObject, board)
  }
  
  def asEntity(dbObject: DBObject, board: Board): ClassOfService = {
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
      ClassOfService.Fields.board.toString() -> entity.board.id.getOrElse(throw new IllegalArgumentException("The board has to exist!")))
  }
}