package com.googlecode.kanbanik.model
import org.bson.types.ObjectId

import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.Imports.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject

class Workflowitem(
  var id: Option[ObjectId],
  var name: String,
  var wipLimit: Int,
  var itemType: String,
  private var _child: Option[Workflowitem],
  private var _nextItem: Option[Workflowitem],
  private var realBoard: Board)
  extends HasMongoConnection {

  private var boardId: ObjectId = null

  private var nextItemIdInternal: Option[ObjectId] = null

  private var childIdInternal: Option[ObjectId] = null

  if (realBoard != null) {
    boardId = realBoard.id.getOrElse(throw new IllegalArgumentException("Board has to exist for workflowitem"))
  }

  initNextItemIdInternal(_nextItem)
  initChildIdInternal(_child)

  def nextItem_=(item: Option[Workflowitem]): Unit = {
    initNextItemIdInternal(item)
  }

  def nextItem = {
    if (_nextItem == null) {
      if (nextItemIdInternal.isDefined) {
        _nextItem = Some(Workflowitem.byId(nextItemIdInternal.get))
      } else {
        _nextItem = None
      }

    }

    _nextItem
  }

  def child_=(child: Option[Workflowitem]): Unit = {
    initChildIdInternal(child)
    // re-init the _child
    _child = null
  }

  def child = {
    if (_child == null) {
      if (childIdInternal.isDefined) {
        _child = Some(Workflowitem.byId(childIdInternal.get))
      } else {
        _child = None
      }

    }

    _child
  }

  def board = {
    if (realBoard == null) {
      realBoard = Board.byId(boardId)
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
  def store(context: Option[Workflowitem]): Workflowitem = {
    val idToUpdate = id.getOrElse({
      val obj = Workflowitem.asDBObject(this)
      var storedThis: Workflowitem = null
      using(createConnection) { conn =>
        val prevLast = findLastEntityInContext(context, conn)
        coll(conn, Coll.Workflowitems) += obj

        storedThis = Workflowitem.byId(Workflowitem.asEntity(obj).id.get)
        moveVertically(storedThis.id.get, context, storedThis)

        if (prevLast.isDefined) {

          val prevLastEntity = Workflowitem.asEntity(prevLast.get)
          // first put to the end
          coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> prevLastEntity.id.get),
            $set(Workflowitem.Fields.nextItemId.toString() -> storedThis.id.get))

          coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> storedThis.id.get),
            $set(Workflowitem.Fields.nextItemId.toString() -> None))

          // than move to the correct place
          storedThis.store(context)
        }
      }

      return storedThis
    })

    using(createConnection) { conn =>
      storeData

      // remember some state before it gets changed
      val prevContext = findContext(Workflowitem.byId(idToUpdate))
      val lastEntity = findLastEntityInContext(context, conn)
      
      moveVertically(idToUpdate, context, Workflowitem.byId(idToUpdate))

      // put as argument before it was changed
      moveHorizontally(idToUpdate, context, prevContext, lastEntity)

      return Workflowitem.byId(idToUpdate)
    }
  }

  def storeData() {
    using(createConnection) { conn =>
      val idObject = MongoDBObject(Workflowitem.Fields.id.toString() -> id.get)
      coll(conn, Coll.Workflowitems).update(idObject, $set(Workflowitem.Fields.name.toString() -> name, Workflowitem.Fields.wipLimit.toString() -> wipLimit, Workflowitem.Fields.itemType.toString() -> itemType))
    }
  }

  private def moveVertically(
    idToUpdate: ObjectId,
    context: Option[Workflowitem],
    currentEntity: Workflowitem) {

    using(createConnection) { conn =>
      val prevThis = Workflowitem.byId(idToUpdate)
      val parent = findParent(prevThis)

      // removing from original place
      if (parent.isDefined) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> parent.get.id.get),
          $set(Workflowitem.Fields.childId.toString() -> idFromEntity(prevThis.nextItem)))
      }

      // adding to new place
      if (context.isDefined) {
        val child = context.get.child

        // it has no children, adding as the only one
        if (!child.isDefined) {
          coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> context.get.id.get),
            $set(Workflowitem.Fields.childId.toString() -> idToUpdate))

        } else {
          // before something existing - replace the child to the new one
          if (this.nextItemIdInternal.isDefined && child.get.id.get == this.nextItemIdInternal.get) {
            coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> context.get.id.get),
              $set(Workflowitem.Fields.childId.toString() -> idToUpdate))
          }
        }
      }

      updateBoard(context, currentEntity)
    }
  }

  private def idFromEntity(entity: Option[Workflowitem]): Option[ObjectId] = {
    if (entity.isDefined) {
      return entity.get.id
    }

    return None
  }

  private def findLastEntityInContext(context: Option[Workflowitem], conn: MongoConnection): Option[DBObject] = {
    if (!context.isDefined) {
      coll(conn, Coll.Workflowitems).find(MongoDBObject(Workflowitem.Fields.boardId.toString() -> boardId, Workflowitem.Fields.nextItemId.toString() -> None)).foreach(
        item =>
          if (!findContext(Workflowitem.asEntity(item)).isDefined) {
            return Some(item)
          })

      return None
    }

    var candidate = context.get.child.getOrElse(return None)
    while (true) {
      if (!candidate.nextItem.isDefined) {
        return Some(Workflowitem.asDBObject(candidate))
      }

      candidate = candidate.nextItem.get
    }

    throw new IllegalStateException("No last entity on board for context: " + context.get.id.toString)
  }

  private def findLastChildInContext(context: Option[Workflowitem]): Option[DBObject] = {

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
        return Some(Workflowitem.asDBObject(nextChild))
      }
    }

    throw new IllegalStateException("No last child entity on board for context: " + context.get.id.toString)
  }

  def store: Workflowitem = {
    store(None)
  }

  def delete {
    val toDelete = Workflowitem.byId(id.getOrElse(throw new IllegalStateException("Can not delete item which does not exist!")))

    toDelete.nextItemIdInternal = None
    toDelete.store

    val lastChildOfParent = findLastChildInContext(findParent(toDelete))
    if (lastChildOfParent.isDefined) {
      val newParent = Workflowitem.asEntity(lastChildOfParent.get)
      newParent.child = Some(toDelete)
      newParent.store
    }

    unregisterFromBoard()

    using(createConnection) { conn =>
      val newPrev = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), Workflowitem.Fields.nextItemId.toString() -> id))
      if (newPrev.isDefined) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> newPrev.get.get(Workflowitem.Fields.id.toString())),
          $set(Workflowitem.Fields.nextItemId.toString() -> None))
      }

      val newParent = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), Workflowitem.Fields.childId.toString() -> id))
      if (newParent.isDefined) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> newPrev.get.get(Workflowitem.Fields.id.toString())),
          $set(Workflowitem.Fields.nextItemId.toString() -> None))
      }

      coll(conn, Coll.Workflowitems).remove(MongoDBObject(Workflowitem.Fields.id.toString() -> id))
    }

  }

  def unregisterFromBoard() {
    realBoard = Board.byId(boardId)
    val newReferences = board.workflowitems.getOrElse(List()).filter(!_.id.equals(id))
    if (newReferences != null && newReferences.size > 0) {
      board.workflowitems = Some(newReferences)
    } else {
      board.workflowitems = None
    }
    
    realBoard = board.store
  }

  private def initChildIdInternal(child: Option[Workflowitem]) {
    childIdInternal = valueOrNone(child)
  }

  private def initNextItemIdInternal(item: Option[Workflowitem]) {
    nextItemIdInternal = valueOrNone(item)
  }

  private def valueOrNone(toExtract: Option[Workflowitem]): Option[ObjectId] = {
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
      Workflowitem.asEntity(dbObject).id
    }
  }

  private def moveHorizontally(
    idToUpdate: ObjectId,
    context: Option[Workflowitem],
    prevContext: Option[Workflowitem],
    lastEntity: Option[DBObject]) {
    using(createConnection) { conn =>
      // a->b->c->d->e->f
      // the e moves before b
      // so, the result:
      // a->e->b->c->d->f
      // that's where the naming come from
      val e = Workflowitem.byId(idToUpdate)

      // ignore if did not move
      if (prevContext.isDefined && context.isDefined && prevContext.get.id.get == context.get.id.get) {
        if (e.nextItemIdInternal.equals(nextItemIdInternal)) {
          return
        }
      }

      if (!prevContext.isDefined && !context.isDefined) {
        if (e.nextItemIdInternal.equals(nextItemIdInternal)) {
          return
        }
      }

      val boardId = board.id.getOrElse(throw new IllegalStateException("the board has no ID set!"))

      val f = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> boardId, Workflowitem.Fields.id.toString() -> e.nextItemIdInternal)).getOrElse(null)
      val d = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> boardId, Workflowitem.Fields.nextItemId.toString() -> id)).getOrElse(null)
      var b: DBObject = null
      if (nextItemIdInternal.isDefined) {
        b = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> boardId, Workflowitem.Fields.id.toString() -> nextItemIdInternal)).getOrElse(throw new IllegalArgumentException("Trying to move before not existing object with id: " + nextItemIdInternal.toString))
      }

      var a: DBObject = null;
      if (b != null) {
        a = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> boardId, Workflowitem.Fields.nextItemId.toString() -> findId(b))).getOrElse(null)
      }

      if (a != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> findId(a)),
          $set(Workflowitem.Fields.nextItemId.toString() -> e.id))
      }

      coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> e.id),
        $set(Workflowitem.Fields.nextItemId.toString() -> findId(b)))

      if (d != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> findId(d)),
          $set(Workflowitem.Fields.nextItemId.toString() -> findId(f)))
      }

      if (b == null && lastEntity.isDefined && !findId(lastEntity.get).equals(e.id.getOrElse(null))) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject(Workflowitem.Fields.id.toString() -> findId(lastEntity.get)),
          $set(Workflowitem.Fields.nextItemId.toString() -> e.id))
      }
    }
  }

  def findContext(child: Workflowitem): Option[Workflowitem] = {

    using(createConnection) { conn =>
      var prev = child
      while (true) {
        val parent = findParent(prev)
        if (parent.isDefined) {
          return parent
        }

        prev = findPrev(prev).getOrElse(return None)
      }
    }

    None
  }

  private def findPrev(next: Workflowitem): Option[Workflowitem] = {
    using(createConnection) { conn =>
      val prev = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.boardId.toString() -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), Workflowitem.Fields.nextItemId.toString() -> next.id.get))
      return Some(Workflowitem.asEntity(prev.getOrElse(return None)))
    }
  }

  def findParent(child: Workflowitem): Option[Workflowitem] = {
    using(createConnection) { conn =>
      val parent = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Workflowitem.Fields.childId.toString() -> child.id))

      if (parent.isDefined) {
        return Some(Workflowitem.byId(parent.get.get(Workflowitem.Fields.id.toString()).asInstanceOf[ObjectId]));
      } else {
        return None
      }

    }
  }

  /**
   * The board.workflowitems can contain only workflowitems which has no parent (e.g. top level entities)
   * This method ensures it.
   */
  private def updateBoard(context: Option[Workflowitem], currentEntity: Workflowitem) {
    val isInBoard = findIfIsInBoard(board, currentEntity)
    val hasNewParent = context.isDefined

    realBoard = Board.byId(boardId)
    if (isInBoard && hasNewParent) {
      if (board.workflowitems.isDefined) {
        board.workflowitems = Some(board.workflowitems.get.filter(_.id != currentEntity.id))
        realBoard = board.store
      }
    } else if (!isInBoard && !hasNewParent) {
      if (board.workflowitems.isDefined) {
        board.workflowitems = Some(currentEntity :: board.workflowitems.get)
        realBoard = board.store
      } else {
        board.workflowitems = Some(List(currentEntity))
        realBoard = board.store
      }
    }
    
  }

  private def findIfIsInBoard(board: Board, workflowitem: Workflowitem): Boolean = {

    if (!workflowitem.id.isDefined) {
      return false
    }

    if (!board.workflowitems.isDefined) {
      return false
    }

    board.workflowitems.get.foreach(item => {
      if (item.id.get == workflowitem.id.get) {
        return true
      }
    })

    false
  }
}

object Workflowitem extends HasMongoConnection {

  object Fields extends DocumentField {
    val wipLimit = Value("wipLimit")
    val itemType = Value("itemType")
    val nextItemId = Value("nextItemId")
    val boardId = Value("boardId")
    val childId = Value("childId")
  }
  
  def all(): List[Workflowitem] = {
    var allWorkflowitems = List[Workflowitem]()
    using(createConnection) { conn =>
      coll(conn, Coll.Workflowitems).find().foreach(workflowitem => allWorkflowitems = asEntity(workflowitem) :: allWorkflowitems)
    }
    allWorkflowitems
  }

  def byId(id: ObjectId): Workflowitem = {
    using(createConnection) { conn =>
      val dbWorkflow = coll(conn, Coll.Workflowitems).findOne(MongoDBObject(Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
      asEntity(dbWorkflow)
    }
  }

  private def asEntity(dbObject: DBObject) = {
    val item = new Workflowitem(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Fields.wipLimit.toString()).asInstanceOf[Int],
      dbObject.get(Fields.itemType.toString()).asInstanceOf[String],
      null,
      null,
      null)

    item.boardId = dbObject.get(Fields.boardId.toString()).asInstanceOf[ObjectId]

    item.nextItemIdInternal = extractObjectId(dbObject.get(Fields.nextItemId.toString()))
    item.childIdInternal = extractObjectId(dbObject.get(Fields.childId.toString()))

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

  private def asDBObject(entity: Workflowitem): DBObject = {
    MongoDBObject(
      Fields.id.toString() -> entity.id.getOrElse(new ObjectId),
      Fields.name.toString() -> entity.name,
      Fields.wipLimit.toString() -> entity.wipLimit,
      Fields.itemType.toString() -> entity.itemType,
      Fields.childId.toString() -> entity.childIdInternal,
      Fields.nextItemId.toString() -> entity.nextItemIdInternal,
      Fields.boardId.toString() -> entity.board.id.getOrElse(throw new IllegalStateException("can not store a workflowitem without an existing board")))
  }

  private def translateChildren(children: BasicDBList): Option[List[Workflowitem]] = {
    if (children == null) {
      None
    } else {
      Some(for { x <- children.toArray().toList } yield asEntity(x.asInstanceOf[DBObject]))
    }

  }

  private def translateChildren(children: Option[List[Workflowitem]]): List[DBObject] = {
    if (!children.isDefined) {
      null
    } else {
      for { x <- children.get } yield asDBObject(x)
    }

  }

}