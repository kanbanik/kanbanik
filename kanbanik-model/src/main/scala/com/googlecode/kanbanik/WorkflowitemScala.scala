package com.googlecode.kanbanik
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._

class WorkflowitemScala(
  var id: Option[ObjectId],
  var name: String,
  var wipLimit: Int,
  var children: Option[List[WorkflowitemScala]],
  var nextItemId: Option[ObjectId],
  private var realBoard: BoardScala)
  extends KanbanikEntity {

  private var boardId: ObjectId = null

  if (realBoard != null) {
    boardId = realBoard.id.getOrElse(throw new IllegalArgumentException("Board has to exist for workflowitem"))
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
      return WorkflowitemScala.asEntity(obj)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)
    coll(Coll.Workflowitems).update(idObject, $set("name" -> name, "wipLimit" -> wipLimit))
    move(idToUpdate)

    coll(Coll.Workflowitems).update(idObject, $set("children" -> WorkflowitemScala.translateChildren(children)))

    WorkflowitemScala.byId(idToUpdate)
  }

  def delete {
    val toDelete = WorkflowitemScala.byId(id.getOrElse(throw new IllegalStateException("Can not delete item which does not exist!")))

    unregisterFromBoard()

    toDelete.nextItemId = None
    toDelete.store
    val newPrev = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> board.id.getOrElse(throw new IllegalStateException("The board has to be set for workflowitem!")), "nextItemId" -> id))
    if (newPrev.isDefined) {
      coll(Coll.Workflowitems).update(MongoDBObject("_id" -> newPrev.get.get("_id")),
        $set("nextItemId" -> null))
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
    if (!nextItemId.isDefined && !e.nextItemId.isDefined) {
      return ;
    }
    if (e.nextItemId.equals(nextItemId)) {
      return
    }

    val boardId = board.id.getOrElse(throw new IllegalStateException("the board has no ID set!"))

    val lastEntity = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> null)).getOrElse(throw new IllegalStateException("No last entity on board: " + realBoard.toString))
    val f = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> e.nextItemId)).getOrElse(null)
    val d = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> id)).getOrElse(null)
    var b: DBObject = null
    if (nextItemId.isDefined) {
      b = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> nextItemId)).getOrElse(throw new IllegalArgumentException("Trying to move before not existing object with id: " + nextItemId.toString))
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

  private def findId(dbObject: DBObject): ObjectId = {
    if (dbObject == null) {
      return null;
    }

    WorkflowitemScala.asEntity(dbObject).id.getOrElse(null)
  }

}

object WorkflowitemScala extends KanbanikEntity {
  def byId(id: ObjectId): WorkflowitemScala = {
    val dbWorkflow = coll(Coll.Workflowitems).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
    asEntity(dbWorkflow)
  }

  private def asEntity(dbObject: DBObject) = {
    val item = new WorkflowitemScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      dbObject.get("wipLimit").asInstanceOf[Int],
      {
        if (dbObject.get("children").isInstanceOf[BasicDBList]) {
          translateChildren(dbObject.get("children").asInstanceOf[BasicDBList])
        } else {
          None
        }
      },
      {
        if (dbObject.get("nextItemId") == null) {
          None
        } else {
          Some(dbObject.get("nextItemId").asInstanceOf[ObjectId])
        }
      },
      null)

    item.boardId = dbObject.get("boardId").asInstanceOf[ObjectId]

    item
  }

  private def asDBObject(entity: WorkflowitemScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "wipLimit" -> entity.wipLimit,
      "children" -> translateChildren(entity.children),
      "nextItemId" -> entity.nextItemId.getOrElse(null),
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