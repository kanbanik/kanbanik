package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.mongodb.casbah.Imports._
import java.util.ArrayList
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.exceptions.ResourceLockedException
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing
import com.googlecode.kanbanik.commons._

class Board(
  val id: Option[ObjectId],
  val name: String,
  val version: Int,
  val workflow: Workflow,
  val tasks: List[Task],
  val userPictureShowingEnabled: Boolean,
  val workfloVerticalSizing: WorkfloVerticalSizing) extends HasMongoConnection with HasMidAirCollisionDetection {

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
        Board.Fields.workfloVerticalSizing.toString() -> workfloVerticalSizing.getIndex())

      Board.asEntity(versionedUpdate(Coll.Boards, versionedQuery(idToUpdate, version), update))
    }

  }

  def withId(id: ObjectId) =
    new Board(Some(id), name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)
  
  def withName(name: String) =
    new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)

  def withVersion(version: Int) =
    new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)

  def withWorkflow(workflow: Workflow) =
    new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)

  def withTasks(tasks: List[Task]) =
    new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)
  
  def withUserPictureShowingEnabled(userPictureShowingEnabled: Boolean) =   
  	new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)
  
  def withWorkfloVerticalSizing(workfloVerticalSizing: WorkfloVerticalSizing) =   
  	new Board(id, name, version, workflow, tasks, userPictureShowingEnabled, workfloVerticalSizing)
  
  private def asDbObject(): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> name,
      Board.Fields.workfloVerticalSizing.toString() -> workfloVerticalSizing.getIndex(),
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
    using(createConnection) { conn =>
      val taskExclusionObject = {
        if (includeTasks) {
          // does not retrive the description of the task even it retrieves tasks
          MongoDBObject(Board.Fields.tasks + "." + Task.Fields.description.toString() -> 0)
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
    val board = new Board(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.getWithDefault[Int](Fields.version, 1),
      Workflow.asEntity(dbObject.get(Fields.workflow.toString()).asInstanceOf[DBObject]),
      List(),
      dbObject.getWithDefault[Boolean](Fields.userPictureShowingEnabled, true),
      WorkfloVerticalSizing.fromId(dbObject.getWithDefault[Int](Fields.workfloVerticalSizing, 0))
    )

    
    board.withWorkflow(board.workflow.withBoard(Some(board)))

    val tasks = dbObject.get(Fields.tasks.toString())
    if (tasks != null && tasks.isInstanceOf[BasicDBList]) {
      val list = dbObject.get(Fields.tasks.toString()).asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[DBObject]]
      val projects = Project.allForBoard(board)
      val taskEnitities = list.map(Task.asEntity(_, board, projects))
      board.withTasks(taskEnitities)
    } else {
      board
    }
  }
}