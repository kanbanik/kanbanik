package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder

class SaveTaskCommand extends ServerCommand[SimpleParams[TaskDto], SimpleParams[TaskDto]] {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): SimpleParams[TaskDto] = {
    val task = taskBuilder.buildEntity(params.getPayload())
    return new SimpleParams(taskBuilder.buildDto(task.store))
  }
}