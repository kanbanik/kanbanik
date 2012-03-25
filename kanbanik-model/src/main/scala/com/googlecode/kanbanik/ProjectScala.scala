package com.googlecode.kanbanik
import org.bson.types.ObjectId

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject

class ProjectScala(
  var id: Option[ObjectId],
  var name: String,
  var boards: Option[List[BoardScala]],
  var tasks: Option[List[TaskScala]]) extends KanbanikEntity {

  def store: ProjectScala = {
    val obj = ProjectScala.asDBObject(this)
    coll(Coll.Projects) += obj
    return ProjectScala.asEntity(obj)
  }

}

object ProjectScala extends KanbanikEntity {
  def byId(id: ObjectId): ProjectScala = {
    val dbProject = coll(Coll.Projects).findOne(MongoDBObject("_id" -> id)).getOrElse(throw new IllegalArgumentException("No such project with id: " + id))
    asEntity(dbProject)
  }

  private def asDBObject(entity: ProjectScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId,
      "name" -> entity.name,
      "boards" -> None,
      "tasks" -> 
      {
        if (!entity.tasks.isDefined) {
          null
        } else {
          for { task <- entity.tasks.get } yield task.id.getOrElse(throw new IllegalArgumentException("The task has to exist!"))
        }
      }
    )

  }

  private def asEntity(dbObject: DBObject) = {
    new ProjectScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId]),
      dbObject.get("name").asInstanceOf[String],
      {
        convertList(dbObject.get("boards"), BoardScala.byId(_))
      },
      {
        convertList(dbObject.get("tasks"), TaskScala.byId(_))
      })
  }

  private def convertList[T](dbObject: Object, codeBlock: ObjectId => T) = {
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
        Some(for { boardId <- processList} yield codeBlock(boardId))
      }
    }
  }
}