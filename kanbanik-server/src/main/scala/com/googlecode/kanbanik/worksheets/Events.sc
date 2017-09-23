
import com.googlecode.kanbanik.model.DocumentField
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

object Event extends DocumentField {
  val entityId = Value("entityId")
  val timestamp = Value("timestamp")
}


object SomethingElse extends DocumentField {
  val x1 = Value("x1")
  val x2 = Value("x2")
}

def diff(oldVal: Map[String, Any], newVal: Map[String, Any]): Map[String, Any] =
  newVal.filter((e: (String, Any)) =>
    e._1 != Event.version.toString && // never return version - it has no meaning for statistics
      (e._1 == Event.id.toString || // always return the ID - needed to group the events of one entity in statistics
        oldVal.get(e._1).getOrElse(None) != e._2) // but return only the changed fields so the event will be slim
  )


val mold = Map(
  SomethingElse.id.toString -> "the id",
  SomethingElse.version.toString -> "the version",
  SomethingElse.x1.toString -> "the x 1",
  SomethingElse.x2.toString -> "the x 2"

)

val mew = Map(
  SomethingElse.id.toString -> "the id",
  SomethingElse.version.toString -> "the version + 1",
  SomethingElse.x1.toString -> "the x 1",
  SomethingElse.x2.toString -> "the x 2 changed"

)

mew.count((e: (String, String)) => e._1 != Event.id.toString)

diff(mold, mew)


val base = MongoDBObject(
  mold.toList
)

val builder = MongoDBObject.newBuilder

val ext = MongoDBObject(
"a" -> "X1",
"y" -> "X2"
)

(builder ++= base ++= ext).result()

val mangle = base.put("x", "y")
mangle

object DB extends HasMongoConnection {

  def store(obj: MongoDBObject) {
    using(createConnection) { conn =>
      coll(conn, Coll.Events) += obj
    }
  }
}


"$aaaa".replaceAll("\\$", "_")

// task created
// task deleted
// task moved
// task edited


// {entity_type, timestamp, entity_id, ...}



def toS[A <: AnyRef](source: A): String = write(source)

implicit val formats = DefaultFormats

val x = toS(Event(new ObjectId, "xx"))
val p2 = toS(Payload2(1, true))

val record = parse(x)

val asMap = record.values.asInstanceOf[Map[String, Any]]

val y = asMap.asDBObject
y.get("payload").asInstanceOf[String]

//DB.store(y)




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
