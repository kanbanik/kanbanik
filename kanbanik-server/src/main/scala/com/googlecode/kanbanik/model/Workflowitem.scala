package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection

class Workflowitem(
  val id: Option[ObjectId],
  val name: String,
  val wipLimit: Int,
  val itemType: String,
  val version: Int,
  val nestedWorkflow: Workflow,
  val _board: Board)
  extends HasMongoConnection
  with HasMidAirCollisionDetection with Equals {

  def board: Board = if (_board.isInstanceOf[LazyBoard]) _board.asInstanceOf[LazyBoard].lazyLoad else _board

  // it has to be possible to do this in some cleaner way
  def withName(name: String) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      nestedWorkflow,
      _board);

  def withWipLimit(wipLimit: Int) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      nestedWorkflow,
      _board);

  def withItemType(itemType: String) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      nestedWorkflow,
      _board);

  def withVersion(version: Int) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      nestedWorkflow,
      _board);

  def withWorkflow(newWorkflow: Workflow) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      newWorkflow,
      _board);

  def withBoard(board: Board) =
    new Workflowitem(
      id,
      name,
      wipLimit,
      itemType,
      version,
      nestedWorkflow,
      board);

  def store: Workflowitem = {
    null
  }

  def storeData: Workflowitem = {
    val update = $set(
      Workflowitem.Fields.version.toString() -> { version + 1 },
      Workflowitem.Fields.name.toString() -> name,
      Workflowitem.Fields.wipLimit.toString() -> wipLimit,
      Workflowitem.Fields.itemType.toString() -> itemType,
      Workflowitem.Fields.nestedWorkflow.toString() -> nestedWorkflow.asDbObject)

    Workflowitem.asEntity(versionedUpdate(Coll.Workflowitems, versionedQuery(id.get, version), update))
  }

  def delete {
    // TODO
  }

  def asDbObject(): DBObject = {
    MongoDBObject(
      Workflowitem.Fields.id.toString() -> id.getOrElse(new ObjectId),
      Workflowitem.Fields.name.toString() -> name,
      Workflowitem.Fields.wipLimit.toString() -> wipLimit,
      Workflowitem.Fields.itemType.toString() -> itemType,
      Workflowitem.Fields.version.toString() -> version,
      Workflowitem.Fields.nestedWorkflow.toString() -> nestedWorkflow.asDbObject,
      Workflowitem.Fields.boardId.toString() -> _board.id.getOrElse(throw new IllegalStateException("can not store a workflowitem without an existing board")))
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[com.googlecode.kanbanik.model.Workflowitem]
  }

  override def equals(other: Any) = {
    other match {
      case that: Workflowitem => that.canEqual(Workflowitem.this) && id == that.id
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + id.hashCode
  }

  override def toString = id.toString

}

object Workflowitem extends HasMongoConnection {

  object Fields extends DocumentField {
    val wipLimit = Value("wipLimit")
    val itemType = Value("itemType")
    val boardId = Value("boardId")
    val nestedWorkflow = Value("nestedWorkflow")
  }

  def all(): List[Workflowitem] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Workflowitems).find().map(asEntity(_)).toList
    }
  }

  def byId(id: ObjectId): Workflowitem = {
    using(createConnection) { conn =>
      val dbWorkflow = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
      asEntity(dbWorkflow)
    }
  }

  def asEntity(dbObject: DBObject): Workflowitem = {
    new Workflowitem(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Fields.wipLimit.toString()).asInstanceOf[Int],
      dbObject.get(Fields.itemType.toString()).asInstanceOf[String],
      dbObject.get(Fields.version.toString()).asInstanceOf[Int],
      {
        val nestedWorkflow = dbObject.get(Fields.id.toString()).asInstanceOf[DBObject]
        Workflow.asEntity(nestedWorkflow)
      },
      new LazyBoard(dbObject.get(Fields.boardId.toString()).asInstanceOf[ObjectId]))
  }

}

case class LazyBoard(
  override val id: Option[ObjectId],
  override val name: String,
  override val version: Int,
  override val workflow: Workflow) extends Board(id, name, version, workflow) {

  def this(id: ObjectId) = this(Some(id), "", -1, Workflow())

  def lazyLoad = Board.byId(id.get)

}