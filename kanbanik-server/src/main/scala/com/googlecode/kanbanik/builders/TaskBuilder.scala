package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.model.TaskScala
import com.googlecode.kanbanik.dto.TaskDto

class TaskBuilder {

  val builder = new WorkflowitemBuilder

  def buildDto(task: TaskScala): TaskDto = {
    val dto = new TaskDto
    dto.setId(task.id.get.toString())
    dto.setName(task.name)
    dto.setDescription(task.description)
    dto.setClassOfService(task.classOfService)
    dto.setWorkflowitem(builder.buildDto(task.workflowitem))
    dto
  }
}