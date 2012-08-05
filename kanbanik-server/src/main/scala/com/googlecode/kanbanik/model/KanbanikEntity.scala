package com.googlecode.kanbanik.model
import com.mongodb.casbah.MongoConnection
import com.googlecode.kanbanik.model.manipulation.ResourceManipulation
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.MongoOptions
import com.mongodb.ServerAddress

trait KanbanikEntity extends ResourceManipulation {

  def createConnection = {
    KanbanikEntity.initConnection
    KanbanikEntity.connection
  }

  object Coll extends Enumeration {
    val Workflowitems = Value("workflowitems")
    val Boards = Value("boards")
    val Tasks = Value("tasks")
    val Projects = Value("projects")
    val TaskId = Value("taskid")
  }

  def coll(connection: MongoConnection, collName: Coll.Value) = {
    connection("kanbanik")(collName.toString())
  }

}

object KanbanikEntity {
  var connection: MongoConnection = null
  def initConnection {
    if (connection == null) {
      connection = MongoConnection()
      connection.writeConcern = WriteConcern.Safe
    }

  }

  def destroyConnection = {
    if (connection != null) {
      connection.close()
    }
  }
}