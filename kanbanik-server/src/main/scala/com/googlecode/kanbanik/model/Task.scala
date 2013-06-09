package com.googlecode.kanbanik.model

import org.bson.types.ObjectId

import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject

class Task(
  val id: Option[ObjectId],
  val name: String,
  val description: String,
  val classOfService: Int,
  val ticketId: String,
  val version: Int,
  val workflowitem: Workflowitem) extends HasMongoConnection with HasMidAirCollisionDetection {

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

  def withWorkflowitem(workflowitem: Workflowitem) = {
    new Task(
    		id,
    		name,
    		description,
    		classOfService,
    		ticketId,
    		version,
    		workflowitem
    )
  }
  
  def withDescription(description: String) = {
    new Task(
    		id,
    		name,
    		description,
    		classOfService,
    		ticketId,
    		version,
    		workflowitem
    )
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

  def all(): List[Task] = {
    using(createConnection) { conn =>
      // this never retrieves the description!
      coll(conn, Coll.Tasks).find(MongoDBObject(), MongoDBObject(Task.Fields.description.toString() -> 0)).map(asEntity(_)).toList
    }
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
    val task = new Task(
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
      null)
    
    val workflowitemId = dbObject.get(Task.Fields.workflowitem.toString()).asInstanceOf[ObjectId]
    val board = Board.all().find(board => board.workflow.containsItem(Workflowitem().withId(workflowitemId))).getOrElse(throwEx(task, workflowitemId))
    val workflowitem = board.workflow.findItem(Workflowitem().withId(workflowitemId))
    task.withWorkflowitem(workflowitem.get)
  }
  
  def throwEx(task: Task, workflowitemId: ObjectId) = throw new IllegalStateException("The task " + task.id + " which is on workflowitem: " + workflowitemId + " does not exist on any board" )
}