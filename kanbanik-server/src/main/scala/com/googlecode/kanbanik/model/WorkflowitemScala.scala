package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._

class WorkflowitemScala(
  var id: Option[ObjectId],
  var name: String,
  var wipLimit: Int,
  private var _child: Option[WorkflowitemScala],
  private var _nextItem: Option[WorkflowitemScala],
  private var realBoard: BoardScala)
  extends KanbanikEntity {

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

  def store: WorkflowitemScala = {

    val idToUpdate = id.getOrElse({
      val obj = WorkflowitemScala.asDBObject(this)
      coll(Coll.Workflowitems) += obj
      return WorkflowitemScala.byId(WorkflowitemScala.asEntity(obj).id.get)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)
    coll(Coll.Workflowitems).update(idObject, $set("name" -> name, "wipLimit" -> wipLimit))
    
    move(idToUpdate)

    coll(Coll.Workflowitems).update(idObject, $set(WorkflowitemScala.CHILD_NAME -> childIdInternal.getOrElse(None)))

    WorkflowitemScala.byId(idToUpdate)
  }

  def delete {
    val toDelete = WorkflowitemScala.byId(id.getOrElse(throw new IllegalStateException("Can not delete item which does not exist!")))

    unregisterFromBoard()

    toDelete.nextItemIdInternal = None
    toDelete.store
    val newPrev = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), "nextItemId" -> id))
    if (newPrev.isDefined) {
      coll(Coll.Workflowitems).update(MongoDBObject("_id" -> newPrev.get.get("_id")),
        $set("nextItemId" -> None))
    }

    coll(Coll.Workflowitems).remove(MongoDBObject("_id" -> id))
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

  private def move(idToUpdate: ObjectId) {
    // a->b->c->d->e->f
    // the e moves before b
    // so, the result:
    // a->e->b->c->d->f
    // that's where the naming come from
    val e = WorkflowitemScala.byId(idToUpdate)
    // ignore if did not move
    if (!nextItemIdInternal.isDefined && !e.nextItemIdInternal.isDefined) {
      return ;
    }
    if (e.nextItemIdInternal.equals(nextItemIdInternal)) {
      return
    }

    val boardId = board.id.getOrElse(throw new IllegalStateException("the board has no ID set!"))

    val lastEntity = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> None)).getOrElse(throw new IllegalStateException("No last entity on board: " + realBoard.toString))
    val f = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> e.nextItemIdInternal)).getOrElse(null)
    val d = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> id)).getOrElse(null)
    var b: DBObject = null
    if (nextItemIdInternal.isDefined) {
      b = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> nextItemIdInternal)).getOrElse(throw new IllegalArgumentException("Trying to move before not existing object with id: " + nextItemIdInternal.toString))
    }

    var a: DBObject = null;
    if (b != null) {
      a = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> findId(b))).getOrElse(null)
    }

    if (a != null) {
      coll(Coll.Workflowitems).update(MongoDBObject("_id" -> findId(a)),
        $set("nextItemId" -> e.id))
    }

    coll(Coll.Workflowitems).update(MongoDBObject("_id" -> e.id),
      $set("nextItemId" -> findId(b)))

    if (d != null) {
      coll(Coll.Workflowitems).update(MongoDBObject("_id" -> findId(d)),
        $set("nextItemId" -> findId(f)))
    }

    if (b == null && !findId(lastEntity).equals(e.id.getOrElse(null))) {
      coll(Coll.Workflowitems).update(MongoDBObject("_id" -> findId(lastEntity)),
        $set("nextItemId" -> e.id))
    }
  }

  private def findId(dbObject: DBObject): Option[ObjectId] = {
    if (dbObject == null) {
      return None;
    }

    WorkflowitemScala.asEntity(dbObject).id
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
  
  def byId(id: ObjectId): WorkflowitemScala = {
    val dbWorkflow = coll(Coll.Workflowitems).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
    asEntity(dbWorkflow)
  }

  private def asEntity(dbObject: DBObject) = {
    val item = new WorkflowitemScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      dbObject.get("wipLimit").asInstanceOf[Int],
      null,
      null,
      null)

    item.boardId = dbObject.get("boardId").asInstanceOf[ObjectId]

    item.nextItemIdInternal = extractObjectId(dbObject.get("nextItemId"))
    item.childIdInternal = extractObjectId(dbObject.get(CHILD_NAME))
    
    item
  }
  
  private def extractObjectId(raw: Object) : Option[ObjectId] = {
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
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "wipLimit" -> entity.wipLimit,
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