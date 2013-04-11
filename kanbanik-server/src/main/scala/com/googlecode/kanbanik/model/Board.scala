package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import com.mongodb.casbah.Imports._
import java.util.ArrayList
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.exceptions.ResourceLockedException
import com.googlecode.kanbanik.db.HasMongoConnection

class Board(
  val id: Option[ObjectId],
  val name: String,
  val balanceWorkflowitems: Boolean,
  val version: Int,
  val workflow: Workflow,
  val tasks: List[Task]) extends HasMongoConnection with HasMidAirCollisionDetection {

  def this(
    id: Option[ObjectId],
    name: String,
    version: Int) = {
    this(id, name, true, version, new Workflow(None, List(), None), List())
  }

  def move(item: Workflowitem, beforeItem: Option[Workflowitem], destWorkflow: Workflow): Board = {
    val removed = workflow.removeItem(item)
    val added = removed.addItem(item, beforeItem, destWorkflow)

    new Board(id, name, balanceWorkflowitems, version, added, tasks)
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
        Board.Fields.balanceWorkflowitems.toString() -> balanceWorkflowitems,
        Board.Fields.workflow.toString() -> workflow.asDbObject)

      Board.asEntity(versionedUpdate(Coll.Boards, versionedQuery(idToUpdate, version), update))
    }

  }

  def withId(id: ObjectId) =
    new Board(Some(id), name, balanceWorkflowitems, version, workflow, tasks)
  
  def withName(name: String) =
    new Board(id, name, balanceWorkflowitems, version, workflow, tasks)

  def withVersion(version: Int) =
    new Board(id, name, balanceWorkflowitems, version, workflow, tasks)

  def withWorkflow(workflow: Workflow) =
    new Board(id, name, balanceWorkflowitems, version, workflow, tasks)

  def withBalancedWorkflowitems(balanceWorkflowitems: Boolean) =
    new Board(id, name, balanceWorkflowitems, version, workflow, tasks)

  def withTasks(tasks: List[Task]) =
    new Board(id, name, balanceWorkflowitems, version, workflow, tasks)

  private def asDbObject(): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> name,
      Board.Fields.balanceWorkflowitems.toString() -> balanceWorkflowitems,
      Board.Fields.version.toString() -> version,
      Board.Fields.workflow.toString() -> workflow.asDbObject,
      Board.Fields.tasks.toString() -> tasks.map(Task.asDBObject(_)))
  }

  def delete {
    versionedDelete(Coll.Boards, versionedQuery(id.get, version))
  }

}

object Board extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflow = Value("workflow")
    val balanceWorkflowitems = Value("balanceWorkflowitems")
    val tasks = Value("tasks")
  }

  def apply() = new Board(Some(new ObjectId()), "", 1)

  def all(includeTasks: Boolean): List[Board] = {
    using(createConnection) { conn =>
      def taskExclusionObject = {
        if (includeTasks) {
          // does not retrive the description of the task even it retrieves tasks
          MongoDBObject(Board.Fields.tasks + "." + Task.Fields.description.toString() -> 0)
        } else {
          MongoDBObject(Board.Fields.tasks.toString() -> 0)
        }
      }
      
      coll(conn, Coll.Boards).find(MongoDBObject(), taskExclusionObject).map(asEntity(_)).toList
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
      Some(dbObject.get(Board.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Board.Fields.name.toString()).asInstanceOf[String],
      {
        val res = dbObject.get(Board.Fields.balanceWorkflowitems.toString())
        if (res == null) {
          true
        } else {
          res.asInstanceOf[Boolean]
        }
      },
      determineVersion(dbObject.get(Board.Fields.version.toString())),
      Workflow.asEntity(dbObject.get(Board.Fields.workflow.toString()).asInstanceOf[DBObject]),
      List())

    
    board.withWorkflow(board.workflow.withBoard(Some(board)))

    val tasks = dbObject.get(Board.Fields.tasks.toString())
    if (tasks != null && tasks.isInstanceOf[BasicDBList]) {
      val list = dbObject.get(Board.Fields.tasks.toString()).asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[DBObject]]
      val projects = Project.allForBoard(board)
      val taskEnitities = list.map(Task.asEntity(_, board, projects))
      board.withTasks(taskEnitities)
    } else {
      board
    }

  }

  private def determineVersion(res: Object) = {
    if (res == null) {
      1
    } else {
      res.asInstanceOf[Int]
    }
  }

}