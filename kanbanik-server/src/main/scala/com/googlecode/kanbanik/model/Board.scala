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
  val workflow: Workflow) extends HasMongoConnection with HasMidAirCollisionDetection {

  def this(
    id: Option[ObjectId],
    name: String,
    version: Int) = {
    this(id, name, true, version, new Workflow(None, List(), None))
  }

  def move(item: Workflowitem, beforeItem: Option[Workflowitem], destWorkflow: Workflow): Board = {
    val removed = workflow.removeItem(item)
    val added = removed.addItem(item, beforeItem, destWorkflow)

    new Board(id, name, balanceWorkflowitems, version, added)
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

  def withName(name: String) =
    new Board(id, name, balanceWorkflowitems, version, workflow)

  def withVersion(version: Int) =
    new Board(id, name, balanceWorkflowitems, version, workflow)

  def withWorkflow(workflow: Workflow) =
    new Board(id, name, balanceWorkflowitems, version, workflow)

  def withBalancedWorkflowitems(balanceWorkflowitems: Boolean) =
    new Board(id, name, balanceWorkflowitems, version, workflow)
  
  private def asDbObject(): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> name,
      Board.Fields.balanceWorkflowitems.toString() -> balanceWorkflowitems,
      Board.Fields.version.toString() -> version,
      Board.Fields.workflow.toString() -> workflow.asDbObject)
  }

  def delete {
    versionedDelete(Coll.Boards, versionedQuery(id.get, version))
  }

}

object Board extends HasMongoConnection {
  
  object Fields extends DocumentField {
    val workflow = Value("workflow")
    val balanceWorkflowitems = Value("balanceWorkflowitems")
  }

  def apply() = new Board(Some(new ObjectId()), "", 1)
  
  def all(): List[Board] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Boards).find().map(asEntity(_)).toList
    }
  }

  def byId(id: ObjectId): Board = {
    using(createConnection) { conn =>
      val dbBoards = coll(conn, Coll.Boards).findOne(MongoDBObject(Board.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such board with id: " + id))
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
      Workflow.asEntity(dbObject.get(Board.Fields.workflow.toString()).asInstanceOf[DBObject]))
    
    board.withWorkflow(board.workflow.withBoard(Some(board)))
  }

  private def determineVersion(res: Object) = {
    if (res == null) {
      1
    } else {
      res.asInstanceOf[Int]
    }
  }

}