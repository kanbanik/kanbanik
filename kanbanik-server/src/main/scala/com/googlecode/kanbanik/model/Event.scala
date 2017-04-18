package com.googlecode.kanbanik.model

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId


case class Event(id: ObjectId, eventType: EventType.Value, payload: String, timestamp: Long) extends HasMongoConnection {
  def store: User = {
    val obj = Event.asDBObject(this)
    using(createConnection) { conn =>
      coll(conn, Coll.Events) += obj
    }

    null
  }
}

object Event extends HasMongoConnection {
//  def asEntity(dbObject: DBObject): Event = {
//    Event(
//      dbObject.get(Fields.id.toString).asInstanceOf[String],
//      dbObject.get(Fields.id.toString).asInstanceOf[String],
//      dbObject.get(Fields.id.toString),
//      dbObject.get(Fields.id.toString).asInstanceOf[String],
//    )
//  }

  def apply(name: EventType.Value, payload: String): Event =
    Event(new ObjectId, name, payload, System.currentTimeMillis())

  object Fields extends DocumentField {
    val payload = Value("payload")
    val timestamp = Value("timestamp")
  }

  def asDBObject(entity: Event): DBObject = {
    MongoDBObject(
      Fields.id.toString -> entity.id,
      Fields.name.toString -> entity.eventType,
      Fields.timestamp.toString -> entity.timestamp,
      Fields.payload.toString -> entity.payload
    )
  }
}

object EventType extends Enumeration {
  val TaskMoved = Value("TaskMoved")
  val TaskChanged = Value("TaskChanged")
  val TaskCreated = Value("TaskCreated")
  val TaskDeleted = Value("TaskDeleted")
}
