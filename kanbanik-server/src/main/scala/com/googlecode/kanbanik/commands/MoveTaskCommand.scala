package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.MoveTaskParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala
import scala.util.control.Breaks._

class MoveTaskCommand extends ServerCommand[MoveTaskParams, SimpleParams[TaskDto]] {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskParams): SimpleParams[TaskDto] = {
    val task = taskBuilder.buildEntity(params.getTask())
    val project = ProjectScala.byId(new ObjectId(params.getProject().getId()));
    var definedOnProject: ProjectScala = null

    breakable {
      for (project <- ProjectScala.all()) {
        if (project.tasks.isDefined) {
          val tasksForProject: List[TaskScala] = project.tasks.get
          if (tasksForProject.exists(_.id == task.id)) {
            definedOnProject = project
            break
          }
        }
      }
    }

    if (project.id == definedOnProject.id) {
      return new SimpleParams(taskBuilder.buildDto(task.store))
    }

    if (definedOnProject.tasks.isDefined) {
    	definedOnProject.tasks = Some(definedOnProject.tasks.get.filter(_.id != task.id))
    	definedOnProject.store
    } 
    
    if (project.tasks.isDefined) {
      project.tasks = Some(task :: project.tasks.get)
      project.store
    } else {
      project.tasks = Some(List(task))
      project.store
    }
    
    return new SimpleParams(taskBuilder.buildDto(task.store))
  }
}