package com.googlecode.kanbanik.model
import org.bson.types.ObjectId

import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

class TaskScala(
  var id: Option[ObjectId],
  var name: String,
  var description: String,
  var classOfService: Int,
  var ticketId: String,
  var workflowitem: WorkflowitemScala) extends KanbanikEntity {

  def store: TaskScala = {
    val idToUpdate = id.getOrElse({
      val obj = TaskScala.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Tasks) += obj
      }
      return TaskScala.asEntity(obj)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)

    using(createConnection) { conn =>
      coll(conn, Coll.Tasks).update(idObject, $set(
        "name" -> name,
        "description" -> description,
        "classOfService" -> classOfService,
        "ticketId" -> ticketId,
        "workflowitem" -> workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem"))))

      TaskScala.byId(idToUpdate)
    }
  }

  def delete {
    using(createConnection) { conn =>
      coll(conn, Coll.Tasks).remove(MongoDBObject("_id" -> id))
    }
  }

}

object TaskScala extends KanbanikEntity {
  def byId(id: ObjectId): TaskScala = {
    using(createConnection) { conn =>
      val dbTask = coll(conn, Coll.Tasks).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such task with id: " + id))
      asEntity(dbTask)
    }
  }

  private def asDBObject(entity: TaskScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "description" -> entity.description,
      "classOfService" -> entity.classOfService,
      "ticketId" -> entity.ticketId,
      "workflowitem" -> entity.workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))
  }

  private def asEntity(dbObject: DBObject) = {
    new TaskScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      dbObject.get("description").asInstanceOf[String],
      dbObject.get("classOfService").asInstanceOf[Int],
      dbObject.get("ticketId").asInstanceOf[String],
      WorkflowitemScala.byId(dbObject.get("workflowitem").asInstanceOf[ObjectId]))
  }
}