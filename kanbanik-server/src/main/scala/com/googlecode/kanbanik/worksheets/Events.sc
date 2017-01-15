
import com.mongodb.casbah.{MongoCollection, MongoConnection, WriteConcern}
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.commons.Implicits._
import com.mongodb.casbah.commons.MongoDBObject

case class Payload1(p1: Integer, p2: String)
case class Payload2(p1: Integer, p3: Boolean)

case class Event(id: ObjectId, payload: String)

object DB extends HasMongoConnection {

  def store(obj: MongoDBObject) {
    using(createConnection) { conn =>
      coll(conn, Coll.Events) += obj
    }
  }
}


"$aaaa".replaceAll("\\$", "_")

def toS[A <: AnyRef](source: A): String = write(source)

implicit val formats = DefaultFormats

val x = toS(Event(new ObjectId, "xx"))
val p2 = toS(Payload2(1, true))

val record = parse(x)

val asMap = record.values.asInstanceOf[Map[String, Any]]

val y = asMap.asDBObject
y.get("payload").asInstanceOf[String]

DB.store(y)




trait ResourceManipulation {
  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      // do not close it - this is not a connection, this is a pool
      // of connections. It will be closed at shutdown of the web
      // container using a listener class
    }
}

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

}
