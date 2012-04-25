package com.googlecode.kanbanik.commands
import scala.util.control.Breaks._

import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala

trait TaskManipulation {

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
}