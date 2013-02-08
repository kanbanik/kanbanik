package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import scala.collection.immutable.HashMap

class Project(
  var id: Option[ObjectId],
  var name: String,
  var version: Int,
  var boards: Option[List[Board]],
  var tasks: Option[List[Task]]) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: Project = {
    val idToUpdate = id.getOrElse({
      val obj = Project.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Projects) += obj
      }
      return Project.asEntity(obj)
    })

    val update = $set(
        Project.Fields.name.toString() -> name,
        Project.Fields.version.toString() -> { version + 1 },
        Project.Fields.boards.toString() -> Project.toIdList[Board](boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
        Project.Fields.tasks.toString() -> Project.toIdList[Task](tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))))
    
    Project.asEntity(versionedUpdate(Coll.Projects, versionedQuery(idToUpdate, version), update))
  }

  def delete {
     versionedDelete(Coll.Projects, versionedQuery(id.get, version))
  }

}

object Project extends HasMongoConnection {

  object Fields extends DocumentField {
    val boards = Value("boards")
    val tasks = Value("tasks")
  }

  def all(): List[Project] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Projects).find().map(asEntity(_)).toList
    }
  }

  def byId(id: ObjectId): Project = {
    using(createConnection) { conn =>
      val dbProject = coll(conn, Coll.Projects).findOne(MongoDBObject(Project.Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such project with id: " + id))
      asEntity(dbProject)
    }
  }

  private def asDBObject(entity: Project): DBObject = {
    MongoDBObject(
      Project.Fields.id.toString() -> new ObjectId,
      Project.Fields.name.toString() -> entity.name,
      Project.Fields.version.toString() -> entity.version,
      Project.Fields.boards.toString() -> toIdList[Board](entity.boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
      Project.Fields.tasks.toString() -> toIdList[Task](entity.tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))))

  }

  private def asEntity(dbObject: DBObject) = {
    val allTasks = buildMap[Task](Task.all)
    val allBoards = buildMap[Board](Board.all)
    
    new Project(
      Some(dbObject.get(Project.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Project.Fields.name.toString()).asInstanceOf[String],
      {
        val res = dbObject.get(Project.Fields.version.toString())
        if (res == null) {
          1
        } else {
          res.asInstanceOf[Int]
        }
      },
      {
        toEntityList(dbObject.get(Project.Fields.boards.toString()), allBoards.get(_).get)
      },
      {
        toEntityList(dbObject.get(Project.Fields.tasks.toString()), allTasks.get(_).get)
      })
  }

  def buildMap[T <: { def id: Option[ObjectId]}](allEntities: List[T]):Map[ObjectId, T] = {
    val zipped = allEntities.map(_.id.get) zip allEntities
    zipped.toMap
  }
  
  private def toIdList[T](entities: Option[List[T]], codeBlock: T => ObjectId) = {
    if (!entities.isDefined) {
      null
    } else {
      for { entity <- entities.get } yield codeBlock(entity)
    }
  }

  private def toEntityList[T](dbObject: Object, codeBlock: ObjectId => T) = {
    if (dbObject == null || dbObject == None) {
      None
    } else {

      var processList: List[ObjectId] = null

      if (dbObject.isInstanceOf[List[ObjectId]]) {
        processList = dbObject.asInstanceOf[List[ObjectId]]
      } else {
        processList = dbObject.asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[ObjectId]]
      }

      if (processList == null || processList.size == 0) {
        None
      } else {
        Some(for { boardId <- processList } yield codeBlock(boardId))
      }
    }
  }
}