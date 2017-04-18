package com.googlecode.kanbanik.db

import com.googlecode.kanbanik.model.{EventType, Task}
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.Implicits._

trait HasEvents extends HasMongoConnection {

  def publish(eventType: EventType.Value, specific: DBObject) {
    val builder = MongoDBObject.newBuilder
    val common = MongoDBObject(
      "id" -> new ObjectId,
      "timestamp" -> System.currentTimeMillis()
    )

    val complete = (builder ++= common ++= specific).result()

    using(createConnection) { conn =>
      coll(conn, Coll.Events) += complete
    }
  }

}
