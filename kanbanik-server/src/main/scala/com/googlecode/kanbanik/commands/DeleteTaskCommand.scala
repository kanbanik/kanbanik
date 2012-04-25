package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.VoidParams

class DeleteTaskCommand extends ServerCommand[SimpleParams[TaskDto], VoidParams] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): VoidParams = {
    val task = taskBuilder.buildEntity(params.getPayload())
    
    val project = findProjectForTask(task).getOrElse(throw new IllegalStateException("The task '" + task.id + "' is defined on NO project!"))
    removeTaskFromProject(task, project)
    task.delete
    
    new VoidParams()
  }
}