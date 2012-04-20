package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ClassOfService
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.TaskScala
import com.googlecode.kanbanik.model.WorkflowitemScala

class TaskBuilder {

  val builder = new WorkflowitemBuilder

  def buildDto(task: TaskScala): TaskDto = {
    val dto = new TaskDto
    dto.setId(task.id.get.toString())
    dto.setName(task.name)
    dto.setDescription(task.description)
    dto.setClassOfService(ClassOfService.fromId(task.classOfService))
    dto.setWorkflowitem(builder.buildDtoNonRecursive(task.workflowitem))
    dto.setTicketId(task.ticketId)
    dto
  }

  def buildEntity(taskDto: TaskDto): TaskScala = {
    new TaskScala(
      Some(new ObjectId(taskDto.getId())),
      taskDto.getName(),
      taskDto.getDescription(),
      taskDto.getClassOfService().getId(),
      taskDto.getTicketId(),
      WorkflowitemScala.byId(new ObjectId(taskDto.getWorkflowitem().getId())));
  }
}