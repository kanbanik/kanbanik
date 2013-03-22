package com.googlecode.kanbanik.db

import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.DocumentField
import com.mongodb.DBObject
import com.mongodb.casbah.Imports.$or
import com.mongodb.casbah.Imports.ConcreteDBObjectOk
import com.mongodb.casbah.Imports.mongoQueryStatements
import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject

trait HasMidAirCollisionDetection extends HasMongoConnection {
  
  def versionedQuery(id: Any, version: Int, idField: String, versionField: String): DBObject = {
    (MongoDBObject(idField -> id)) ++ 
    ($or((versionField $exists false), MongoDBObject(versionField -> version)))
  }
  
  def versionedQuery(id: Any, version: Int): DBObject = {
    versionedQuery(id, version, SimpleField.id.toString(), SimpleField.version.toString())
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