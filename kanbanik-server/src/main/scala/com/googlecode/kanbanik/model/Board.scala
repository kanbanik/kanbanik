package com.googlecode.kanbanik.model

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dtos.WorkfloVerticalSizing

case class Board(
  val id: Option[ObjectId],
  val name: String,
  val version: Int,
  val workflow: Workflow,
  val tasks: List[Task],
  val userPictureShowingEnabled: Boolean,
  val workfloVerticalSizing: WorkfloVerticalSizing.Value) extends HasMongoConnection with HasMidAirCollisionDetection {

  def this(
    id: Option[ObjectId],
    name: String,
    version: Int) = {
    this(id, name, version, new Workflow(None, List(), None), List(), true, WorkfloVerticalSizing.BALANCED)
  }

  def move(item: Workflowitem, beforeItem: Option[Workflowitem], destWorkflow: Workflow): Board = {
    val removed = workflow.removeItem(item)
    val added = removed.addItem(item, beforeItem, destWorkflow)

    new Board(id, name, version, added, tasks, userPictureShowingEnabled, workfloVerticalSizing)
  }

  def store: Board = {
    val idToUpdate = id.getOrElse({
      val obj = asDbObject
      using(createConnection) {
        connection => coll(connection, Coll.Boards) += obj
      }

      return Board.asEntity(obj)
    })

    using(createConnection) { conn =>

      val update = $set(
        Board.Fields.version.toString() -> { version + 1 },
        Board.Fields.name.toString() -> name,
        Board.Fields.workflow.toString() -> workflow.asDbObject,
        Board.Fields.userPictureShowingEnabled.toString() -> userPictureShowingEnabled,
        Board.Fields.workfloVerticalSizing.toString() -> workfloVerticalSizing.id)

      Board.asEntity(versionedUpdate(Coll.Boards, versionedQuery(idToUpdate, version), update))
    }

  }

  private def asDbObject(): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> name,
      Board.Fields.workfloVerticalSizing.toString() -> workfloVerticalSizing.id,
      Board.Fields.version.toString() -> version,
      Board.Fields.workflow.toString() -> workflow.asDbObject,
      Board.Fields.userPictureShowingEnabled.toString() -> userPictureShowingEnabled,
      Board.Fields.tasks.toString() -> tasks.map(Task.asDBObject(_)))
  }

  def delete {
    versionedDelete(Coll.Boards, versionedQuery(id.get, version))
  }

}

object Board extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflow = Value("workflow")
    val tasks = Value("tasks")
    val userPictureShowingEnabled = Value("showUserPictures")
    val workfloVerticalSizing = Value("workfloVerticalSizing")
  }

  def apply() = new Board(Some(new ObjectId()), "", 1)

  def all(includeTasks: Boolean): List[Board] = {
    // does not retrieve the description of the task even it retrieves tasks
    all(includeTasks, false)
  }

  def all(includeTasks: Boolean, includeTaskDescription: Boolean): List[Board] = {
    using(createConnection) { conn =>
      val taskExclusionObject = {
        if (includeTasks) {
            if (includeTaskDescription) {
              // ok, no eclusions
              MongoDBObject()
            } else {
              MongoDBObject(Board.Fields.tasks + "." + Task.Fields.description.toString() -> 0)
            }
        } else {
          MongoDBObject(Board.Fields.tasks.toString() -> 0)
        }
      }
      
      coll(conn, Coll.Boards).find(MongoDBObject(), taskExclusionObject).sort(MongoDBObject(Board.Fields.name.toString() -> 1)).map(asEntity(_)).toList
    }
  }

  def byId(id: ObjectId, includeTasks: Boolean): Board = {
    using(createConnection) { conn =>
      def taskExclusionObject = {
        if (!includeTasks) {
          MongoDBObject(Board.Fields.tasks.toString() -> 0)
        } else {
          MongoDBObject()
        }
      }
      
      val dbBoards = coll(conn, Coll.Boards).findOne(MongoDBObject(Board.Fields.id.toString() -> id), taskExclusionObject).getOrElse(throw new IllegalArgumentException("No such board with id: " + id))
      asEntity(dbBoards)
    }
  }

  def asEntity(dbObject: DBObject) = {
    val resBoard = new Board(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.getWithDefault[Int](Fields.version, 1),
      Workflow.asEntity(dbObject.get(Fields.workflow.toString()).asInstanceOf[DBObject]),
      List(),
      dbObject.getWithDefault[Boolean](Fields.userPictureShowingEnabled, true),
      WorkfloVerticalSizing.fromId(dbObject.getWithDefault[Int](Fields.workfloVerticalSizing, -1))
    )

    
    resBoard.copy(workflow = resBoard.workflow.copy(_board = Some(resBoard)))

    val tasks = dbObject.get(Fields.tasks.toString())
    if (tasks != null && tasks.isInstanceOf[BasicDBList]) {
      val list = dbObject.get(Fields.tasks.toString()).asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[DBObject]]
      val allUsers = User.all()
      val allClassOfServices = ClassOfService.all()
      val taskEnitities = list.map(Task.asEntity(_, allClassOfServices, allUsers))
      resBoard.copy(tasks = taskEnitities)
    } else {
      resBoard
    }
  }
}
