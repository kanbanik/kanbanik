package com.googlecode.kanbanik.db
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.model.HasMongoConnection
import com.googlecode.kanbanik.model.DocumentField
import com.mongodb.DBObject
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.DocumentField
import com.mongodb.casbah.Imports._

trait HasMidAirCollisionDetection extends HasMongoConnection {
  def versionedQuery(id: ObjectId, version: Int) = {
    (MongoDBObject(SimpleField.id.toString() -> id)) ++ 
    ($or((SimpleField.version.toString() $exists false), MongoDBObject(SimpleField.version.toString() -> version)))
  }

  def versionedUpdate(collection: Coll.Value, query: DBObject, update: DBObject) = {
    using(createConnection) { conn =>
      val res = coll(conn, collection).findAndModify(query, null, null, false, update, true, false)
      res.getOrElse(throw new MidAirCollisionException)
    }
  }

  def versionedDelete(collection: Coll.Value, query: DBObject) {
    using(createConnection) { conn =>
      val res = coll(conn, collection).findAndModify(query, null, null, true, null, true, false)
      if (!res.isDefined) {
        throw new MidAirCollisionException
      }
    }
  }

  object SimpleField extends DocumentField {

  }
}