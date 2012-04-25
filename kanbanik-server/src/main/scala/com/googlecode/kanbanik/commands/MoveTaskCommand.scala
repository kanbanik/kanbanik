package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.MoveTaskParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala

class MoveTaskCommand extends ServerCommand[MoveTaskParams, SimpleParams[TaskDto]] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskParams): SimpleParams[TaskDto] = {
    val task = taskBuilder.buildEntity(params.getTask())
    val project = ProjectScala.byId(new ObjectId(params.getProject().getId()));

    val definedOnProject = findProjectForTask(task).getOrElse(throw new IllegalStateException("The task '" + task.id + "' is defined on NO project!"))

    if (project.id == definedOnProject.id) {
      return new SimpleParams(taskBuilder.buildDto(task.store))
    }

    removeTaskFromProject(task, definedOnProject)

    addTaskToProject(task, project)

    return new SimpleParams(taskBuilder.buildDto(task.store))
  }
}