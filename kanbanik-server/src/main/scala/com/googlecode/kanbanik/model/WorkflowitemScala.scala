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
  val itemType: String,
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

      moveHorizontally(idToUpdate, context)

      moveVertically(context)

      WorkflowitemScala.byId(idToUpdate)
    }
  }

  private def moveVertically(context: Option[WorkflowitemScala]) {
    val parent = findParent(this)

    using(createConnection) { conn =>
      // removing from original place
      if (parent.isDefined) {
        //        parent.get.child = this.nextItem
        //        parent.get.store

        val idObject = MongoDBObject("_id" -> parent.get.id.get)
        var nextId: ObjectId = null
        if (this.nextItem.isDefined) {
          nextId = this.nextItem.get.id.get
        }
        coll(conn, Coll.Workflowitems).update(idObject, $set("childId" -> nextId))
      }

      // adding to new place
      if (context.isDefined) {
        val child = context.get.child

        // it has no children, adding as the only one
        if (!child.isDefined) {
          context.get.child = Some(this)
          context.get.store

          val idObject = MongoDBObject("_id" -> parent.get.id.get)
          var nextId: ObjectId = null
          if (this.nextItem.isDefined) {
            nextId = this.nextItem.get.id.get
          }
          coll(conn, Coll.Workflowitems).update(idObject, $set("childId" -> nextId))
        } else {
          // before something existing - replace the child to the new one
          if (child.get.id == this.nextItem.get.id) {
            context.get.child = Some(this)
            context.get.store
          }
        }
      }
    }
  }

  private def findLastEntityInContext(context: Option[WorkflowitemScala], conn: MongoConnection): Option[DBObject] = {
    if (!context.isDefined) {
      return coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> None))
    }

    var candidate = context.get.child.getOrElse(return None)
    while (true) {
      if (!candidate.nextItem.isDefined) {
        return Some(WorkflowitemScala.asDBObject(candidate))
      }

      candidate = candidate.nextItem.get
    }

    throw new IllegalStateException("No last entity on board for context: " + context.get.id.toString)
  }

  private def findLastChildInContext(context: Option[WorkflowitemScala]): Option[DBObject] = {

    if (!context.isDefined) {
      return None
    }

    val parent = context.get

    if (!parent.child.isDefined) {
      return None
    }

    while (true) {
      val nextChild = parent.child.get
      if (!nextChild.child.isDefined) {
        return Some(WorkflowitemScala.asDBObject(nextChild))
      }
    }

    throw new IllegalStateException("No last child entity on board for context: " + context.get.id.toString)
  }

  def store: WorkflowitemScala = {
    store(None)
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

  private def findId(dbObject: DBObject): Option[ObjectId] = {
    if (dbObject == null) {
      None;
    } else {
      WorkflowitemScala.asEntity(dbObject).id
    }
  }

  def moveHorizontally(
    idToUpdate: ObjectId,
    context: Option[WorkflowitemScala]) {
    using(createConnection) { conn =>
      // a->b->c->d->e->f
      // the e moves before b
      // so, the result:
      // a->e->b->c->d->f
      // that's where the naming come from
      val e = WorkflowitemScala.byId(idToUpdate)

      // ignore if did not move
      val parent = findParent(e)
      if (parent.isDefined && context.isDefined && parent.get.id.get == context.get.id.get) {
        if (e.nextItemIdInternal.equals(nextItemIdInternal)) {
          return
        }
      }

      if (!parent.isDefined && !context.isDefined) {
        if (e.nextItemIdInternal.equals(nextItemIdInternal)) {
          return
        }
      }

      val boardId = board.id.getOrElse(throw new IllegalStateException("the board has no ID set!"))

      val lastEntity = findLastEntityInContext(context, conn)

      val f = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> e.nextItemIdInternal)).getOrElse(null)
      val d = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> id)).getOrElse(null)
      var b: DBObject = null
      if (nextItemIdInternal.isDefined) {
        b = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> nextItemIdInternal)).getOrElse(throw new IllegalArgumentException("Trying to move before not existing object with id: " + nextItemIdInternal.toString))
      }

      var a: DBObject = null;
      if (b != null) {
        a = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> findId(b))).getOrElse(null)
      }

      if (a != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(a)),
          $set("nextItemId" -> e.id))
      }

      coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> e.id),
        $set("nextItemId" -> findId(b)))

      if (d != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(d)),
          $set("nextItemId" -> findId(f)))
      }

      if (b == null && lastEntity.isDefined && !findId(lastEntity.get).equals(e.id.getOrElse(null))) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(lastEntity.get)),
          $set("nextItemId" -> e.id))
      }
    }
  }

  def findParent(child: WorkflowitemScala): Option[WorkflowitemScala] = {
    using(createConnection) { conn =>
      val parent = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("childId" -> child.id))

      if (parent.isDefined) {
        return Some(WorkflowitemScala.byId(parent.get.get("_id").asInstanceOf[ObjectId]));
      } else {
        return None
      }

    }
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