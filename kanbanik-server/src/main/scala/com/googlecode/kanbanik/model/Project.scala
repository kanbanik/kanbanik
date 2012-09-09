package com.googlecode.kanbanik.model
import org.bson.types.ObjectId

import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject

class Project(
  var id: Option[ObjectId],
  var name: String,
  var boards: Option[List[Board]],
  var tasks: Option[List[Task]]) extends HasMongoConnection {

  def store: Project = {
    val idToUpdate = id.getOrElse({
      val obj = Project.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Projects) += obj
      }
      return Project.asEntity(obj)
    })

    val idObject = MongoDBObject(Project.Fields.id.toString() -> idToUpdate)

    using(createConnection) { conn =>
      coll(conn, Coll.Projects).update(idObject, $set(
        Project.Fields.name.toString() -> name,
        Project.Fields.boards.toString() -> Project.toIdList[Board](boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
        Project.Fields.tasks.toString() -> Project.toIdList[Task](tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!")))))
    }
    Project.byId(idToUpdate)
  }

  def delete {
    using(createConnection) { conn =>
      coll(conn, Coll.Projects).remove(MongoDBObject(Project.Fields.id.toString() -> id))
    }
  }

}

object Project extends HasMongoConnection {

  object Fields extends DocumentField {
    val boards = Value("boards")
    val tasks = Value("tasks")
  }

  def all(): List[Project] = {
    var allProjects = List[Project]()
    using(createConnection) { conn =>
      coll(conn, Coll.Projects).find().foreach(project => allProjects = asEntity(project) :: allProjects)
    }
    allProjects
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
      Project.Fields.boards.toString() -> toIdList[Board](entity.boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
      Project.Fields.tasks.toString() -> toIdList[Task](entity.tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))))

  }

  private def asEntity(dbObject: DBObject) = {
    new Project(
      Some(dbObject.get(Project.Fields.id.toString()).asInstanceOf[ObjectId]),
      dbObject.get(Project.Fields.name.toString()).asInstanceOf[String],
      {
        toEntityList(dbObject.get(Project.Fields.boards.toString()), Board.byId(_))
      },
      {
        toEntityList(dbObject.get(Project.Fields.tasks.toString()), Task.byId(_))
      })
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