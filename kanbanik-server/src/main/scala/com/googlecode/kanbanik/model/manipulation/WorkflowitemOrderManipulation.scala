package com.googlecode.kanbanik.model.manipulation
import org.bson.types.ObjectId

import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.KanbanikEntity
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

// TODO generalize to any other order manipulation
trait WorkflowitemOrderManipulation extends KanbanikEntity {

  def move[T](
      idToUpdate: ObjectId,
      id: Option[ObjectId],
      nextItemIdInternal: Option[ObjectId], 
      nextInternalId: T => Option[ObjectId], 
      toEntity: ObjectId => T,
      findId: DBObject => Option[ObjectId],
      toId: T => Option[ObjectId],
      board: BoardScala,
      nextItemId: String
      ) {
    using(createConnection) { conn =>
      // a->b->c->d->e->f
      // the e moves before b
      // so, the result:
      // a->e->b->c->d->f
      // that's where the naming come from
      val e = toEntity(idToUpdate)
      // ignore if did not move
      if (!nextItemIdInternal.isDefined && !nextInternalId(e).isDefined) {
        return ;
      }
      if (nextInternalId(e).equals(nextItemIdInternal)) {
        return
      }
      
//      e.nextItemIdInternal == nextInternalId(e)

      val boardId = board.id.getOrElse(throw new IllegalStateException("the board has no ID set!"))

      val lastEntity = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, nextItemId -> None)).getOrElse(throw new IllegalStateException("No last entity on board"))
      val f = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> nextInternalId(e))).getOrElse(null)
      val d = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, nextItemId -> id)).getOrElse(null)
      var b: DBObject = null
      if (nextItemIdInternal.isDefined) {
        b = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, "_id" -> nextItemIdInternal)).getOrElse(throw new IllegalArgumentException("Trying to move before not existing object with id: " + nextItemIdInternal.toString))
      }

      var a: DBObject = null;
      if (b != null) {
        a = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("boardId" -> boardId, nextItemId -> findId(b))).getOrElse(null)
      }

      if (a != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(a)),
          $set(nextItemId -> toId(e)))
      }

      coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> toId(e)),
        $set(nextItemId -> findId(b)))

      if (d != null) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(d)),
          $set(nextItemId -> findId(f)))
      }

      if (b == null && !findId(lastEntity).equals(toId(e).getOrElse(null))) {
        coll(conn, Coll.Workflowitems).update(MongoDBObject("_id" -> findId(lastEntity)),
          $set(nextItemId -> toId(e)))
      }
    }
  }
  
//  private def findId(
//      dbObject: DBObject,
//      toId: DBObject => Option[ObjectId]): Option[ObjectId] = {
//    if (dbObject == null) {
//      return None;
//    }
//
//    WorkflowitemScala.asEntity(dbObject).id
//  }
}