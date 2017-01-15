package com.googlecode.kanbanik

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId





package object model extends HasMongoConnection {

  def publish[T](eventType: EventType.Value, e: T): T = {
    using(createConnection) { conn =>
//      coll(conn, Coll.Events) += Event(eventType, e)
    }

    e
  }
}
