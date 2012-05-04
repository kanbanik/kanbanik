package com.googlecode.kanbanik.model
import com.mongodb.casbah.MongoConnection

trait KanbanikEntity {

  def createConnection = MongoConnection()
  
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

  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }
}