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
  var boardId: ObjectId)
  extends KanbanikEntity {

  def store: WorkflowitemScala = {

    val idToUpdate = id.getOrElse({
      val obj = WorkflowitemScala.asDBObject(this)
      coll(Coll.Workflowitems) += obj
      return WorkflowitemScala.asEntity(obj)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)
    coll(Coll.Workflowitems).update(idObject, $set("name" -> name, "wipLimit" -> wipLimit))
    move(idToUpdate)
    WorkflowitemScala.byId(idToUpdate)
  }

  def move(idToUpdate: ObjectId) {
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

    val lastEntity = coll(Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "nextItemId" -> null)).getOrElse(throw new IllegalStateException("No last entity on board: " + boardId.toString))
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
  
  private def findId(dbObject: DBObject) : ObjectId = {
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

  def asEntity(dbObject: DBObject) = {
    new WorkflowitemScala(
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
      dbObject.get("boardId").asInstanceOf[ObjectId])
  }

  def asDBObject(entity: WorkflowitemScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId(),
      "name" -> entity.name,
      "wipLimit" -> entity.wipLimit,
      "children" -> translateChildren(entity.children),
      "nextItemId" -> entity.nextItemId.getOrElse(null),
      "boardId" -> entity.boardId)
  }

  def translateChildren(children: BasicDBList): Option[List[WorkflowitemScala]] = {
    if (children == null) {
      None
    } else {
      Some(for { x <- children.toArray().toList } yield asEntity(x.asInstanceOf[DBObject]))
    }

  }

  def translateChildren(children: Option[List[WorkflowitemScala]]): List[DBObject] = {
    if (!children.isDefined) {
      null
    } else {
      for { x <- children.get } yield asDBObject(x)
    }

  }
}