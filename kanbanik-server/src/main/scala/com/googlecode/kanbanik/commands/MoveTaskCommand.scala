package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.MoveTaskParams

class MoveTaskCommand extends ServerCommand[MoveTaskParams, SimpleParams[TaskDto]] {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskParams): SimpleParams[TaskDto] = {
    val task = taskBuilder.buildEntity(params.getTask())
    // TODO update also the project
    new SimpleParams(taskBuilder.buildDto(task.store))
  }
}