package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.googlecode.kanbanik.model.manipulation.WorkflowitemOrderManipulation

class WorkflowitemScala(
  var id: Option[ObjectId],
  var name: String,
  var wipLimit: Int,
  val itemType: String,
  private var _child: Option[WorkflowitemScala],
  private var _nextItem: Option[WorkflowitemScala],
  private var realBoard: BoardScala)
  extends KanbanikEntity
  with WorkflowitemOrderManipulation {

  private var boardId: ObjectId = null

  private var nextItemIdInternal: Option[ObjectId] = null

  private var childIdInternal: Option[ObjectId] = null

  if (realBoard != null) {
    boardId = realBoard.id.getOrElse(throw new IllegalArgumentException("Board has to exist for workflowitem"))
  }

  initNextItemIdInternal(_nextItem)
  initChildIdInternal(_child)

  def nextItem_=(item: Option[WorkflowitemScala]): Unit = {
    initNextItemIdInternal(item)
  }

  def nextItem = {
    if (_nextItem == null) {
      if (nextItemIdInternal.isDefined) {
        _nextItem = Some(WorkflowitemScala.byId(nextItemIdInternal.get))
      } else {
        _nextItem = None
      }

    }

    _nextItem
  }

  def child_=(child: Option[WorkflowitemScala]): Unit = {
    initChildIdInternal(child)
  }

  def child = {
    if (_child == null) {
      if (childIdInternal.isDefined) {
        _child = Some(WorkflowitemScala.byId(childIdInternal.get))
      } else {
        _child = None
      }

    }

    _child
  }

  def board = {
    if (realBoard == null) {
      realBoard = BoardScala.byId(boardId)
    }

    realBoard
  }

  /**
   * The context is the parent workflowitem. But the parent workflowitem
   * does not need to have the child to be set to this. The parent's child
   * is the first child, while this is the parent of any child in that context
   *
   * So, there is:
   * ---------
   * |a| b |c|
   * | |d|e| |
   * ---------
   *
   * Than b is the parent of d, but the context of d AND e
   *
   */
  def store(context: Option[WorkflowitemScala]): WorkflowitemScala = {
    val idToUpdate = id.getOrElse({
      val obj = WorkflowitemScala.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Workflowitems) += obj
      }
      return WorkflowitemScala.byId(WorkflowitemScala.asEntity(obj).id.get)
    })

    using(createConnection) { conn =>
      val idObject = MongoDBObject("_id" -> idToUpdate)
      coll(conn, Coll.Workflowitems).update(idObject, $set("name" -> name, "wipLimit" -> wipLimit, "itemType" -> itemType))

      move[WorkflowitemScala](
        idToUpdate,
        id,
        nextItemIdInternal,
        entity => entity.nextItemIdInternal,
        id => WorkflowitemScala.byId(id),
        dbObject => {
          if (dbObject == null) {
            None;
          } else {
            WorkflowitemScala.asEntity(dbObject).id
          }
        },
        entity => entity.id,
        board,
        "nextItemId",
        unit => findLastEntityInContext(context, conn))

//      move[WorkflowitemScala](
//        idToUpdate,
//        id,
//        childIdInternal,
//        entity => entity.childIdInternal,
//        id => WorkflowitemScala.byId(id),
//        dbObject => {
//          if (dbObject == null) {
//            None;
//          } else {
//            WorkflowitemScala.asEntity(dbObject).id
//          }
//        },
//        entity => entity.id,
//        board,
//        WorkflowitemScala.CHILD_NAME,
//        unit => WorkflowitemScala.asDBObject(findLastChild(this)))

      WorkflowitemScala.byId(idToUpdate)
    }
  }

  private def findLastEntityInContext(context: Option[WorkflowitemScala], conn: MongoConnection): DBObject = {
    if (!context.isDefined) {
      return coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> None)).getOrElse(throw new IllegalStateException("No last entity on board"))
    }

    var candidate = context.get.child.getOrElse(throw new IllegalStateException("No last entity on board for context: " + context.get.id.toString))
    while (true) {
      if (!candidate.nextItem.isDefined) {
        return WorkflowitemScala.asDBObject(candidate)
      }

      candidate = candidate.nextItem.get
    }

    throw new IllegalStateException("No last entity on board for context: " + context.get.id.toString)
  }

  def store: WorkflowitemScala = {
    store(None)
  }

  private def findLastChild(parent: WorkflowitemScala): WorkflowitemScala = {
    if (parent.child.isDefined) {
      return findLastChild(parent.child.get)
    }

    return parent
  }

  def delete {
    val toDelete = WorkflowitemScala.byId(id.getOrElse(throw new IllegalStateException("Can not delete item which does not exist!")))

    unregisterFromBoard()

    toDelete.nextItemIdInternal = None
    toDelete.store
    using(createConnection) { conn =>
      val newPrev = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), "nextItemId" -> id))
      if (newPrev.isDefined) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> newPrev.get.get("_id")),
          $set("nextItemId" -> None))
      }

      coll(conn, Coll.Workflowitems).remove(MongoDBObject("_id" -> id))
    }

  }

  def unregisterFromBoard() {
    val newReferences = board.workflowitems.getOrElse(List()).filter(!_.id.equals(id))
    if (newReferences != null && newReferences.size > 0) {
      board.workflowitems = Some(newReferences)
    } else {
      board.workflowitems = None
    }
    board.store
  }

  private def initChildIdInternal(child: Option[WorkflowitemScala]) {
    childIdInternal = valueOrNone(child)
  }

  private def initNextItemIdInternal(item: Option[WorkflowitemScala]) {
    nextItemIdInternal = valueOrNone(item)
  }

  private def valueOrNone(toExtract: Option[WorkflowitemScala]): Option[ObjectId] = {
    if (toExtract != null) {
      if (toExtract.isDefined) {
        val some = toExtract.get.id
        return toExtract.get.id
      }
    }
    None
  }

}

object WorkflowitemScala extends KanbanikEntity {

  val CHILD_NAME = "childId"

  def all(): List[WorkflowitemScala] = {
    var allWorkflowitems = List[WorkflowitemScala]()
    using(createConnection) { conn =>
      coll(conn, Coll.Workflowitems).find().foreach(workflowitem => allWorkflowitems = asEntity(workflowitem) :: allWorkflowitems)
    }
    allWorkflowitems
  }

  def byId(id: ObjectId): WorkflowitemScala = {
    using(createConnection) { conn =>
      val dbWorkflow = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
      asEntity(dbWorkflow)
    }
  }

  private def asEntity(dbObject: DBObject) = {
    val item = new WorkflowitemScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      dbObject.get("wipLimit").asInstanceOf[Int],
      dbObject.get("itemType").asInstanceOf[String],
      null,
      null,
      null)

    item.boardId = dbObject.get("boardId").asInstanceOf[ObjectId]

    item.nextItemIdInternal = extractObjectId(dbObject.get("nextItemId"))
    item.childIdInternal = extractObjectId(dbObject.get(CHILD_NAME))

    item
  }

  private def extractObjectId(raw: Object): Option[ObjectId] = {
    if (raw == null) {
      return None
    } else if (raw.isInstanceOf[ObjectId]) {
      return Some(raw.asInstanceOf[ObjectId])
    } else if (raw.isInstanceOf[Option[ObjectId]]) {
      return raw.asInstanceOf[Option[ObjectId]]
    }

    null
  }

  private def asDBObject(entity: WorkflowitemScala): DBObject = {
    MongoDBObject(
      "_id" -> entity.id.getOrElse(new ObjectId),
      "name" -> entity.name,
      "wipLimit" -> entity.wipLimit,
      "itemType" -> entity.itemType,
      CHILD_NAME -> entity.childIdInternal,
      "nextItemId" -> entity.nextItemIdInternal,
      "boardId" -> entity.board.id.getOrElse(throw new IllegalStateException("can not store a workflowitem without an existing board")))
  }

  private def translateChildren(children: BasicDBList): Option[List[WorkflowitemScala]] = {
    if (children == null) {
      None
    } else {
      Some(for { x <- children.toArray().toList } yield asEntity(x.asInstanceOf[DBObject]))
    }

  }

  private def translateChildren(children: Option[List[WorkflowitemScala]]): List[DBObject] = {
    if (!children.isDefined) {
      null
    } else {
      for { x <- children.get } yield asDBObject(x)
    }

  }
}