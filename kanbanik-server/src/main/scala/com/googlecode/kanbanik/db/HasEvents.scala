package com.googlecode.kanbanik.db

import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.DocumentField

trait HasEvents extends HasMongoConnection {

  type MappableEntity = {
    def asMap(): Map[String, Any]
  }

  object Event extends DocumentField {
    val entityId = Value("entityId")
    val timestamp = Value("timestamp")
    val eventType = Value("eventType")
  }

  object EventType extends Enumeration {
    val TaskMoved = Value("TaskMoved")
    val TaskChanged = Value("TaskChanged")
    val TaskCreated = Value("TaskCreated")
    val TaskDeleted = Value("TaskDeleted")
  }

  def publish(eventType: EventType.Value, specific: Map[String, Any], allowEmpty: Boolean = false) {
    if (!allowEmpty && specific.count((e: (String, Any)) => e._1 != Event.id.toString) == 0) {
      // do not publish an empty event if the only value is the ID
      return
    }

    val renamedId = specific.map(
      (e: (String, Any)) => if (e._1 == Event.id.toString) Event.entityId.toString -> e._2 else e
    )

    val common = Map(
      Event.id.toString -> new ObjectId,
      Event.timestamp.toString -> System.currentTimeMillis(),
      Event.eventType.toString -> eventType.toString
    )

    using(createConnection) { conn =>
      coll(conn, Coll.Events) += MongoDBObject((common ++ renamedId).toList)
    }
  }

  /** Diffs two values and returns the diff usable for the statistics
    * - always returns the ID
    * - never returns the version
    * - returns the changed fields from the newVal
    */
  def diff(oldVal: Map[String, Any], newVal: Map[String, Any]): Map[String, Any] =
    newVal.filter((e: (String, Any)) =>
      e._1 != Event.version.toString && // never return version - it has no meaning for statistics
        (e._1 == Event.id.toString || // always return the ID - needed to group the events of one entity in statistics
          oldVal.get(e._1).getOrElse(None) != e._2) // but return only the changed fields so the event will be slim
    )

  def diff(oldVal: MappableEntity, newVal: MappableEntity): Map[String, Any] =
    diff(oldVal.asMap(), newVal.asMap())

}
