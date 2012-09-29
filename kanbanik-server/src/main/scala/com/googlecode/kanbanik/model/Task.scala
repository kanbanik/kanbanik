package com.googlecode.kanbanik.model
import org.bson.types.ObjectId

import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

class Task(
  var id: Option[ObjectId],
  var name: String,
  var description: String,
  var classOfService: Int,
  var ticketId: String,
  var version: Int,
  var workflowitem: Workflowitem) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: Task = {
    val idToUpdate = id.getOrElse({
      val obj = Task.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Tasks) += obj
      }
      return Task.asEntity(obj)
    })

    using(createConnection) { conn =>
      val update = $set(
        Task.Fields.version.toString() -> { version + 1 },
        Task.Fields.name.toString() -> name,
        Task.Fields.description.toString() -> description,
        Task.Fields.classOfService.toString() -> classOfService,
        Task.Fields.ticketId.toString() -> ticketId,
        Task.Fields.workflowitem.toString() -> workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))
      
      Task.asEntity(versionedUpdate(Coll.Tasks, versionedQuery(idToUpdate, version), update))
    }
  }

  def delete {
    versionedDelete(Coll.Tasks, versionedQuery(id.get, version))
  }

}

object Task extends HasMongoConnection {

  object Fields extends DocumentField {
    val description = Value("description")
    val classOfService = Value("classOfService")
    val ticketId = Value("ticketId")
    val workflowitem = Value("workflowitem")
  }

  def byId(id: ObjectId): Task = {
    using(createConnection) { conn =>
      val dbTask = coll(conn, Coll.Tasks).findOne(MongoDBObject(Task.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such task with id: " + id))
      asEntity(dbTask)
    }
  }

  private def asDBObject(entity: Task): DBObject = {
    MongoDBObject(
      Task.Fields.id.toString() -> new ObjectId,
      Task.Fields.name.toString() -> entity.name,
      Task.Fields.description.toString() -> entity.description,
      Task.Fields.classOfService.toString() -> entity.classOfService,
      Task.Fields.ticketId.toString() -> entity.ticketId,
      Task.Fields.version.toString() -> entity.version,
      Task.Fields.workflowitem.toString() -> entity.workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))
  }

  private def asEntity(dbObject: DBObject) = {
    new Task(
      Some(dbObject.get(Task.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Task.Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Task.Fields.description.toString()).asInstanceOf[String],
      dbObject.get(Task.Fields.classOfService.toString()).asInstanceOf[Int],
      dbObject.get(Task.Fields.ticketId.toString()).asInstanceOf[String],
      {
    	  val res = dbObject.get(Board.Fields.version.toString())
    	  if (res == null) {
    		  1
    	  } else {
    		  res.asInstanceOf[Int]
    	  }
      },
      Workflowitem.byId(dbObject.get(Task.Fields.workflowitem.toString()).asInstanceOf[ObjectId]))
  }
}