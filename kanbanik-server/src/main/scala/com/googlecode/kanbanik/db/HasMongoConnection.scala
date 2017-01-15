package com.googlecode.kanbanik.db
import com.googlecode.kanbanik.model.manipulation.ResourceManipulation
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.MongoCollection

trait HasMongoConnection extends ResourceManipulation {

  def createConnection = {
    HasMongoConnection.initConnection()
    HasMongoConnection.connection
  }

  object Coll extends Enumeration {
    val Workflowitems = Value("workflowitems")
  	val Workflow = Value("workflow")
    val Boards = Value("boards")
    val Tasks = Value("tasks")
    val Projects = Value("projects")
    val ClassesOfService = Value("classesOfService")
    val TaskId = Value("taskid")
    val KanbanikVersion = Value("kanbanikVersion")
    val Users = Value("users")
    val WorkflowitemLocks = Value("workflowitemLocks")
    val Events = Value("Events")
  }

  def coll(connection: MongoConnection, collName: Coll.Value): MongoCollection = {
    connection(HasMongoConnection.dbName)(collName.toString)
  }
  
  def coll(connection: MongoConnection, collName: String): MongoCollection = {
    connection(HasMongoConnection.dbName)(collName)
  }

}

object HasMongoConnection {

  var connection: MongoConnection = null
  def initConnection() {
    if (connection == null) {
      connection = MongoConnection(server, port)
      if (authenticationRequired) {
    	  connection(dbName).authenticate(user, password)
      }
      connection.writeConcern = WriteConcern.Safe
    }

  }
  
  var server = "127.0.0.1"
  var port = 27017
  var user = ""
  var password = ""
  var dbName = "kanbanik"
  var authenticationRequired = false
  
  def initConnectionParams(
      server: String, 
      port: String, 
      user: String, 
      password: String, 
      dbName: String,
      authenticationRequired: String) {
    
    if (server != "") {
      this.server = server
    }
    
    if (user != "") {
      this.user = user
    }
    
    if (password != "") {
      this.password = password
    }
    
    if (dbName != "") {
      this.dbName = dbName
    }
    
    try {
    	this.port = port.toInt
    } catch {
      case e: NumberFormatException =>
        throw new IllegalArgumentException("The port number: '" + port + "' is not a valid integer")
    }
    
    try {
    	this.authenticationRequired = authenticationRequired.toBoolean
    } catch {
      case e: NumberFormatException => 
        throw new IllegalArgumentException("authenticationRequired '"+ authenticationRequired + "' field is not a valid boolean")
    }
    
  }

  def destroyConnection() = {
    if (connection != null) {
      connection.close()
    }
  }
}
