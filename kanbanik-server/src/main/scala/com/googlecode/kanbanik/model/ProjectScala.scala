package com.googlecode.kanbanik.model
import org.bson.types.ObjectId

import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject

class ProjectScala(
  var id: Option[ObjectId],
  var name: String,
  var boards: Option[List[BoardScala]],
  var tasks: Option[List[TaskScala]]) extends KanbanikEntity {

  def store: ProjectScala = {
    val idToUpdate = id.getOrElse({
      val obj = ProjectScala.asDBObject(this)
      coll(Coll.Projects) += obj
      return ProjectScala.asEntity(obj)
    })

    val idObject = MongoDBObject("_id" -> idToUpdate)

    coll(Coll.Projects).update(idObject, $set(
      "name" -> name,
      "boards" -> ProjectScala.toIdList[BoardScala](boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
      "tasks" -> ProjectScala.toIdList[TaskScala](tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))))
    )

    ProjectScala.byId(idToUpdate)
  }

  def delete {
    coll(Coll.Projects).remove(MongoDBObject("_id" -> id))
  }

}

object ProjectScala extends KanbanikEntity {
  
  def all(): List[ProjectScala] = {
    var allProjects = List[ProjectScala]()
    coll(Coll.Projects).find().foreach(project => allProjects = asEntity(project) :: allProjects)
    allProjects
  }
  
  def byId(id: ObjectId): ProjectScala = {
    val dbProject = coll(Coll.Projects).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such project with id: " + id))
    asEntity(dbProject)
  }

  private def asDBObject(entity: ProjectScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "boards" -> toIdList[BoardScala](entity.boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))),
      "tasks" -> toIdList[TaskScala](entity.tasks, _.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))))

  }

  private def asEntity(dbObject: DBObject) = {
    new ProjectScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      {
        toEntityList(dbObject.get("boards"), BoardScala.byId(_))
      },
      {
        toEntityList(dbObject.get("tasks"), TaskScala.byId(_))
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