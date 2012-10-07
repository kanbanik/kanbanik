package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import com.mongodb.casbah.Imports._
import java.util.ArrayList
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection

class Board(
  var id: Option[ObjectId],
  var name: String,
  var version: Int,
  var workflowVersion: Int,
  var workflowLocked: Boolean,
  var workflowitems: Option[List[Workflowitem]]
) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: Board = {
    val idToUpdate = id.getOrElse({
      val obj = Board.asDBObject(this)
      using(createConnection) {
        connection => coll(connection, Coll.Boards) += obj
      }

      return Board.asEntity(obj)
    })

    using(createConnection) { conn =>

      val update = $set(
        Board.Fields.version.toString() -> { version + 1 },
        Board.Fields.name.toString() -> name,
        Board.Fields.workflowitems.toString() -> {
          if (workflowitems.isDefined) {
            for { x <- workflowitems.get } yield x.id
          } else {
            null
          }
        })

      Board.asEntity(versionedUpdate(Coll.Boards, versionedQuery(idToUpdate, version), update))
    }

  }

  // The editing of the workflow is quite complex so it is far from being atomic. It would be dengerous to
  // not to lock it. But this is only a write lock for the editing of the workflow, which has to be explicitly called,
  // it does not lock all the worflowitems interaction such as task moving etc.
  def acquireLock() {
    println("going to lock the board with: " + workflowVersion)
    
	val query = (MongoDBObject(SimpleField.id.toString() -> id)) ++ 
			($or((Board.Fields.workflowVersion.toString() -> MongoDBObject("$exists" -> false)), (Board.Fields.workflowVersion.toString() -> workflowVersion))) ++
			($or((Board.Fields.workflowLocked.toString() -> MongoDBObject("$exists" -> false)), (Board.Fields.workflowLocked.toString() -> false)))

	println(query.toString())
	
	val update = $set(Board.Fields.workflowLocked.toString() -> true)

	using(createConnection) { conn =>
		val res = coll(conn, Coll.Boards).findAndModify(query, null, null, false, update, true, false)
		if (!res.isDefined) {
		  throw new ResourceLockedException
		}
	}
  }
  
  def releaseLock() {
    val query = (MongoDBObject(SimpleField.id.toString() -> id))

	val update = $set(
	    Board.Fields.workflowLocked.toString() -> false,
	    Board.Fields.workflowVersion.toString() -> {workflowVersion + 1}
	    )

	using(createConnection) { conn =>
		val res = coll(conn, Coll.Boards).findAndModify(query, null, null, false, update, true, false)
	}
  }
  
  def delete {
    versionedDelete(Coll.Boards, versionedQuery(id.get, version))
  }

}

object Board extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflowitems = Value("workflowitems")
    val workflowVersion = Value("workflowVersion")
    val workflowLocked = Value("workflowLocked")
  }

  def all(): List[Board] = {
    var allBoards = List[Board]()
    using(createConnection) { conn =>
      coll(conn, Coll.Boards).find().foreach(board => allBoards = asEntity(board) :: allBoards)
    }
    allBoards
  }

  def byId(id: ObjectId): Board = {
    using(createConnection) { conn =>
      val dbBoards = coll(conn, Coll.Boards).findOne(MongoDBObject(Board.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such board with id: " + id))
      asEntity(dbBoards)
    }
  }

  private def asEntity(dbObject: DBObject) = {
    new Board(
      Some(dbObject.get(Board.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Board.Fields.name.toString()).asInstanceOf[String],
      determineVersion(dbObject.get(Board.Fields.version.toString())),
      determineVersion(dbObject.get(Board.Fields.workflowVersion.toString())),
      {
    	val res = dbObject.get(Board.Fields.workflowLocked.toString())
    	
    	if (res == null) {
    		false
    	} else {
    		res.asInstanceOf[Boolean]
    	}
      },
      {
        if (dbObject.get(Board.Fields.workflowitems.toString()).isInstanceOf[BasicDBList]) {
          Some(for { x <- dbObject.get(Board.Fields.workflowitems.toString()).asInstanceOf[BasicDBList].toArray().toList } yield Workflowitem.byId(x.asInstanceOf[ObjectId]))
        } else {
          None
        }
      })
  }
  
  private def determineVersion(res: Object) = {
	  if (res == null) {
		  1
	  } else {
		  res.asInstanceOf[Int]
	  }
  }

  private def asDBObject(entity: Board): DBObject = {
    MongoDBObject(
      Board.Fields.id.toString() -> new ObjectId,
      Board.Fields.name.toString() -> entity.name,
      Board.Fields.version.toString() -> entity.version,
      Board.Fields.workflowVersion.toString() -> entity.workflowVersion,
      Board.Fields.workflowLocked.toString() -> entity.workflowLocked,
      Board.Fields.workflowitems.toString() -> {
        if (!entity.workflowitems.isDefined) {
          null
        } else {
          for { x <- entity.workflowitems.get } yield x.id
        }
      })
  }
}