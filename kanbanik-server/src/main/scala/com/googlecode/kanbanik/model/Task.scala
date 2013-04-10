package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.Imports.$push
import com.mongodb.casbah.Imports.$pull
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObjectBuilder
import com.googlecode.kanbanik.exceptions.MidAirCollisionException

class Task(
  val id: Option[ObjectId],
  val name: String,
  val description: String,
  val classOfService: Int,
  val ticketId: String,
  val version: Int,
  val order: String,
  val workflowitem: Workflowitem,
  val project: Project) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store(): Task = {
    val idToUpdate = id.getOrElse({
      val obj = Task.asDBObject(this)
      using(createConnection) { conn =>
        val update = $push(Coll.Tasks.toString() -> obj)
        val res = coll(conn, Coll.Boards).findAndModify(MongoDBObject(SimpleField.id.toString() -> workflowitem.parentWorkflow.board.id.get), null, null, false, update, true, false)
      }
      return Task.asEntity(obj)
    })

    using(createConnection) { conn =>
      val update = $set(
        Coll.Tasks.toString() + ".$." + Task.Fields.version.toString() -> { version + 1 },
        Coll.Tasks.toString() + ".$." + Task.Fields.name.toString() -> name,
        Coll.Tasks.toString() + ".$." + Task.Fields.description.toString() -> description,
        Coll.Tasks.toString() + ".$." + Task.Fields.classOfService.toString() -> classOfService,
        Coll.Tasks.toString() + ".$." + Task.Fields.ticketId.toString() -> ticketId,
        Coll.Tasks.toString() + ".$." + Task.Fields.order.toString() -> order,
        Coll.Tasks.toString() + ".$." + Task.Fields.projectId.toString() -> project.id,
        Coll.Tasks.toString() + ".$." + Task.Fields.workflowitem.toString() -> workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))

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

  def withWorkflowitem(workflowitem: Workflowitem) = {
    new Task(
      id,
      name,
      description,
      classOfService,
      ticketId,
      version,
      order,
      workflowitem,
      project)
  }

  def withProject(project: Project) = {
    new Task(
      id,
      name,
      description,
      classOfService,
      ticketId,
      version,
      order,
      workflowitem,
      project)
  }
  
  def withOrder(order: String) = {
    new Task(
      id,
      name,
      description,
      classOfService,
      ticketId,
      version,
      order,
      workflowitem,
      project)
  }

  def delete {
    val update = $pull(
      Coll.Tasks.toString() -> MongoDBObject(SimpleField.version.toString() -> version, SimpleField.id.toString() -> id.get))

    val idField = Coll.Tasks.toString() + "." + SimpleField.id.toString()
    val versionField = Coll.Tasks.toString() + "." + SimpleField.version.toString()
    using(createConnection) { conn =>
      // this can not be used because of a bug in mongodb 2.2.0 which is in the runtime.
      // TODO as soon as the runtime will contain a newer version of the mongodb, this line should be uncommented
      // and that try-catch block removed
      //      val dbBoard = versionedUpdate(Coll.Boards, versionedQuery(id.get, version, idField, versionField), update)

      coll(conn, Coll.Boards).update(MongoDBObject(), update)
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

}

object Task extends HasMongoConnection {

  object Fields extends DocumentField {
    val description = Value("description")
    val classOfService = Value("classOfService")
    val ticketId = Value("ticketId")
    val order = Value("order")
    val projectId = Value("projectId")
    val workflowitem = Value("workflowitem")
  }

  def byId(id: ObjectId): Task = {
    using(createConnection) { conn =>
      val dbTask = coll(conn, Coll.Boards).findOne(
        MongoDBObject(Coll.Tasks.toString() + "." + Task.Fields.id.toString() -> id),
        MongoDBObject(Coll.Tasks.toString() -> 1)).getOrElse(throw new IllegalArgumentException("No such task with id: " + id))

      val withoutTheBoard = dbTask.get(Coll.Tasks.toString()).asInstanceOf[BasicDBList]
      if (withoutTheBoard.size() == 0) {
        throw new IllegalArgumentException("No such task with id: " + id)
      }

      asEntity(withoutTheBoard.get(0).asInstanceOf[DBObject])
    }
  }

  def asDBObject(entity: Task): DBObject = {
    MongoDBObject(
      Task.Fields.id.toString() -> { if (entity.id == null || !entity.id.isDefined) new ObjectId else entity.id },
      Task.Fields.name.toString() -> entity.name,
      Task.Fields.description.toString() -> entity.description,
      Task.Fields.classOfService.toString() -> entity.classOfService,
      Task.Fields.ticketId.toString() -> entity.ticketId,
      Task.Fields.version.toString() -> entity.version,
      Task.Fields.order.toString() -> entity.order,
      Task.Fields.projectId.toString() -> entity.project.id,
      Task.Fields.workflowitem.toString() -> entity.workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))
  }

  def asEntity(dbObject: DBObject, boardProvider: Task => Board, projectProvider: Task => Project): Task = {
    val task = new Task(
      Some(dbObject.get(Task.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Task.Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Task.Fields.description.toString()).asInstanceOf[String],
      dbObject.get(Task.Fields.classOfService.toString()).asInstanceOf[Int],
      dbObject.get(Task.Fields.ticketId.toString()).asInstanceOf[String],
      {
        val res = dbObject.get(Task.Fields.version.toString())
        if (res == null) {
          1
        } else {
          res.asInstanceOf[Int]
        }
      },
      dbObject.get(Task.Fields.order.toString()).asInstanceOf[String],
      null,
      null)

    val workflowitemId = dbObject.get(Task.Fields.workflowitem.toString()).asInstanceOf[ObjectId]
    val workflowitem = boardProvider(task).workflow.findItem(Workflowitem().withId(workflowitemId))
    val taskWithWorkflowitem = task.withWorkflowitem(workflowitem.get)

    val projectId = dbObject.get(Task.Fields.projectId.toString()).asInstanceOf[ObjectId]
    if (projectId != null) {
      taskWithWorkflowitem.withProject(projectProvider(task))
    } else {
      taskWithWorkflowitem
    }

  }

  def asEntity(dbObject: DBObject, board: Board, allProjcts: List[Project]): Task = {
    def boardProvider(task: Task): Board = board

    def projectProvider(task: Task): Project = {
      val projectId = dbObject.get(Task.Fields.projectId.toString()).asInstanceOf[ObjectId]
      if (projectId != null) {
        val thisProject = Project().withId(projectId)
        allProjcts.find(_.equals(thisProject)).getOrElse(throw new IllegalStateException("The project: " + projectId + " does not exists even this task: " + task.id + " is defied on it"))
      } else {
        null
      }
    }

    asEntity(dbObject, task => boardProvider(task), task => projectProvider(task))
  }

  def asEntity(dbObject: DBObject): Task = {
    def boardProvider(task: Task): Board = {
      val workflowitemId = dbObject.get(Task.Fields.workflowitem.toString()).asInstanceOf[ObjectId]
      Board.all(false).find(board => board.workflow.containsItem(Workflowitem().withId(workflowitemId))).getOrElse(throwEx(task, workflowitemId))
    }

    def projectProvider(task: Task): Project = {
      val projectId = dbObject.get(Task.Fields.projectId.toString()).asInstanceOf[ObjectId]
      if (projectId != null) {
        val thisProject = Project().withId(projectId)
        Project.byId(projectId)
      } else {
        null
      }
    }

    asEntity(dbObject, task => boardProvider(task), task => projectProvider(task))
  }

  def throwEx(task: Task, workflowitemId: ObjectId) = throw new IllegalStateException("The task " + task.id + " which is on workflowitem: " + workflowitemId + " does not exist on any board")
}