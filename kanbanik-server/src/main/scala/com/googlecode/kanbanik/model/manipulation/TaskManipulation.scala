package com.googlecode.kanbanik.commands
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.model.HasMongoConnection
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Task
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

trait TaskManipulation extends HasMongoConnection {

  def removeTaskFromProject(task: Task, project: Project) {
    if (project.tasks.isDefined) {
      project.tasks = Some(project.tasks.get.filter(_.id != task.id))
      project.store
    }
  }

  def addTaskToProject(task: Task, project: Project) {
    if (project.tasks.isDefined) {
      project.tasks = Some(task :: project.tasks.get)
      project.store
    } else {
      project.tasks = Some(List(task))
      project.store
    }
  }

  def findProjectForTask(task: Task): Option[Project] = {
    var definedOnProject: Option[Project] = None

    breakable {
      for (project <- Project.all()) {
        if (project.tasks.isDefined) {
          val tasksForProject: List[Task] = project.tasks.get
          if (tasksForProject.exists(_.id == task.id)) {
            definedOnProject = Some(project)
            break
          }
        }
      }
    }

    definedOnProject

  }

  def generateUniqueTicketId(): String = {
    using(createConnection) { conn =>
      val maxTaskId = coll(conn, Coll.TaskId).findOne()

      if (maxTaskId.isDefined) {
        val nextMaxId = maxTaskId.get.get("maxId").asInstanceOf[Int] + 1
        coll(conn, Coll.TaskId).update(MongoDBObject("_id" -> maxTaskId.get.get("_id")),
          $set("maxId" -> nextMaxId))

        return "#" + nextMaxId
      }

      coll(conn, Coll.TaskId) += MongoDBObject("maxId" -> 1)

      "#1"
    }

  }
}