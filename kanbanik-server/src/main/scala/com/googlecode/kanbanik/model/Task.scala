package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.Imports.$push
import com.mongodb.casbah.Imports.$pull
import com.mongodb.casbah.Imports.$and
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.db.HasEntityLoader
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.Imports._
import com.googlecode.kanbanik.commons._

case class Task(
  val id: Option[ObjectId],
  val name: String,
  val description: String,
  val classOfService: Option[ClassOfService],
  val ticketId: String,
  val version: Int,
  val order: String,
  val assignee: Option[User],
  val dueData: String,
  val workflowitemId: ObjectId,
  val boardId: ObjectId,
  val projectId: ObjectId) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store(): Task = {
    val idToUpdate = id.getOrElse({
      val obj = Task.asDBObject(this)
      using(createConnection) { conn =>
        val update = $push(Coll.Tasks.toString() -> obj)
        coll(conn, Coll.Boards).findAndModify(MongoDBObject(SimpleField.id.toString() -> boardId), null, null, false, update, true, false)
      }

      return Task.asEntity(obj)
    })

    using(createConnection) { conn =>
      val update = $set(
        Coll.Tasks.toString() + ".$." + Task.Fields.version.toString() -> { version + 1 },
        Coll.Tasks.toString() + ".$." + Task.Fields.name.toString() -> name,
        Coll.Tasks.toString() + ".$." + Task.Fields.dueDate.toString() -> dueData,
        Coll.Tasks.toString() + ".$." + Task.Fields.description.toString() -> description,
        Coll.Tasks.toString() + ".$." + Task.Fields.classOfService.toString() -> { if (classOfService.isDefined) classOfService.get.id else None },
        Coll.Tasks.toString() + ".$." + Task.Fields.assignee.toString() -> { if (assignee.isDefined) assignee.get.name else None },
        Coll.Tasks.toString() + ".$." + Task.Fields.ticketId.toString() -> ticketId,
        Coll.Tasks.toString() + ".$." + Task.Fields.order.toString() -> order,
        Coll.Tasks.toString() + ".$." + Task.Fields.projectId.toString() -> projectId,
        Coll.Tasks.toString() + ".$." + Task.Fields.workflowitem.toString() -> workflowitemId,
        Coll.Tasks.toString() + ".$." + Task.Fields.boardId.toString() -> boardId
      )

      val idField = Coll.Tasks.toString() + "." + SimpleField.id.toString()
      val versionField = Coll.Tasks.toString() + "." + SimpleField.version.toString()
      val dbBoard = versionedUpdate(Coll.Boards, versionedQuery(idToUpdate, version, idField, versionField), update)
      convertToEntity(idToUpdate, dbBoard)
    }
  }

  def convertToEntity(idToUpdate: ObjectId, dbBoard: DBObject): Task = {
    val dbTasksAsDbList = dbBoard.get(Coll.Tasks.toString()).asInstanceOf[BasicDBList]
    val dbTasks = dbTasksAsDbList.toArray().toList.asInstanceOf[List[DBObject]]
    val dbTask = dbTasks.filter(_.get(Task.Fields.id.toString()) == idToUpdate)
    // I have just put it to DB, it has to be there
    Task.asEntity(dbTask.head)
  }

  def delete(boardId: ObjectId) {
    val update = $pull(
      Coll.Tasks.toString() -> MongoDBObject(SimpleField.version.toString() -> version, SimpleField.id.toString() -> id.get))

    using(createConnection) { conn =>
      // this can not be used because of a bug in mongodb 2.2.0 which is in the runtime.
      // TODO as soon as the runtime will contain a newer version of the mongodb, this line should be uncommented
      // and that try-catch block removed
      //    val idField = Coll.Tasks.toString() + "." + SimpleField.id.toString()
      //    val versionField = Coll.Tasks.toString() + "." + SimpleField.version.toString()
      //    val dbBoard = versionedUpdate(Coll.Boards, versionedQuery(id.get, version, idField, versionField), update)

      coll(conn, Coll.Boards).update(MongoDBObject(Board.Fields.id.toString() -> boardId), update)
      try {
        Task.byId(id.get)
      } catch {
        case e: IllegalArgumentException => {
          return
        }
      }

      throw new MidAirCollisionException
    }

  }

  def project: Project = Project.byId(projectId)

  def board: Board = Board.byId(boardId, false)

}

object Task extends HasMongoConnection with HasEntityLoader {

  object Fields extends DocumentField {
    val description = Value("description")
    val ticketId = Value("ticketId")
    val order = Value("order")
    val projectId = Value("projectId")
    val workflowitem = Value("workflowitem")
    val classOfService = Value("classOfService")
    val assignee = Value("assignee")
    val dueDate = Value("dueDate")
    val boardId = Value("boardId")
  }

  def byId(id: ObjectId): Task = {
    using(createConnection) { conn =>
      
      val elemMatch = Coll.Tasks.toString() $elemMatch (MongoDBObject(Task.Fields.id.toString() -> id))
      val tasksExists = Coll.Tasks.toString() $exists true
      
      // TODO this one returns all the boards (just the IDs)
      // find a way how to tell mongo to return only the one needed!
      val dbTasks = coll(conn, Coll.Boards).find(tasksExists, elemMatch).map(_.get(Coll.Tasks.toString()).asInstanceOf[BasicDBList])
      val oneTask = dbTasks.find(t => t != null && t.size() != 0).getOrElse(throw new IllegalArgumentException("No such task with id: " + id))

      asEntity(oneTask.get(0).asInstanceOf[DBObject])
    }
  }

  def asDBObject(entity: Task): DBObject = {
    MongoDBObject(
      Fields.id.toString() -> { if (entity.id == null || !entity.id.isDefined) new ObjectId else entity.id },
      Fields.name.toString() -> entity.name,
      Fields.description.toString() -> entity.description,
      Fields.classOfService.toString() -> entity.classOfService,
      Fields.ticketId.toString() -> entity.ticketId,
      Fields.version.toString() -> entity.version,
      Fields.order.toString() -> entity.order,
      Fields.projectId.toString() -> entity.projectId,
      Fields.classOfService.toString() -> { if (entity.classOfService.isDefined) entity.classOfService.get.id else None },
      Fields.assignee.toString() -> { if (entity.assignee.isDefined) entity.assignee.get.name else None },
      Fields.dueDate.toString() -> entity.dueData,
      Fields.workflowitem.toString() -> entity.workflowitemId,
      Fields.boardId.toString() -> entity.boardId
    )
  }

  def asEntity(dbObject: DBObject): Task = {
    Task(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Fields.description.toString()).asInstanceOf[String],
      loadOrNone[ObjectId, ClassOfService](Fields.classOfService.toString(), dbObject, loadClassOfService(_)),
      dbObject.get(Fields.ticketId.toString()).asInstanceOf[String],
      dbObject.getWithDefault[Int](Fields.version, 1),
      dbObject.get(Fields.order.toString()).asInstanceOf[String],
      loadOrNone[String, User](Fields.assignee.toString(), dbObject, loadUser(_)),
      dbObject.getWithDefault[String](Fields.dueDate, ""),
      dbObject.get(Fields.workflowitem.toString()).asInstanceOf[ObjectId],
      dbObject.get(Fields.boardId.toString()).asInstanceOf[ObjectId],
      dbObject.get(Fields.projectId.toString()).asInstanceOf[ObjectId]
    )
  }

  def loadOrNone[T, R](dbField: String, dbObject: DBObject, f: T => Option[R]): Option[R] = {
    val res = dbObject.get(dbField)
    if (res == null) {
      None
    } else {
      f(res.asInstanceOf[T])
    }
  }
}
