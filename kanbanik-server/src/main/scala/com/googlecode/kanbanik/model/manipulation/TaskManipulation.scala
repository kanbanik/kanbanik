package com.googlecode.kanbanik.commands
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import com.googlecode.kanbanik.model.KanbanikEntity
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject

trait TaskManipulation extends KanbanikEntity {

  def removeTaskFromProject(task: TaskScala, project: ProjectScala) {
    if (project.tasks.isDefined) {
      project.tasks = Some(project.tasks.get.filter(_.id != task.id))
      project.store
    }
  }

  def addTaskToProject(task: TaskScala, project: ProjectScala) {
    if (project.tasks.isDefined) {
      project.tasks = Some(task :: project.tasks.get)
      project.store
    } else {
      project.tasks = Some(List(task))
      project.store
    }
  }

  def findProjectForTask(task: TaskScala): Option[ProjectScala] = {
    var definedOnProject: Option[ProjectScala] = None

    breakable {
      for (project <- ProjectScala.all()) {
        if (project.tasks.isDefined) {
          val tasksForProject: List[TaskScala] = project.tasks.get
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