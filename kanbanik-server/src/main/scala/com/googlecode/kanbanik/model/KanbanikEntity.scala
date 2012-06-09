package com.googlecode.kanbanik.model
import com.mongodb.casbah.MongoConnection
import com.googlecode.kanbanik.model.manipulation.ResourceManipulation

trait KanbanikEntity extends ResourceManipulation {

  def createConnection = {
    MongoConnection()
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