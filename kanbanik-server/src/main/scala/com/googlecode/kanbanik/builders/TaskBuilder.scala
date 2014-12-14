package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model._
import com.googlecode.kanbanik.commands.TaskManipulation
import com.googlecode.kanbanik.dtos.TaskDto
import scala.Some

class TaskBuilder extends TaskManipulation {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder
  
  lazy val userBuilder = new UserBuilder

  def buildDto(task: Task): TaskDto = {
    TaskDto(
      Some(task.id.get.toString),
      task.name,
      {
        if (task.description == null || task.description == "") {
          None
        } else {
          Some(task.description)
        }
      },
      {
          if (task.classOfService.isDefined) {
            Some(classOfServiceBuilder.buildDto(task.classOfService.get))
          } else {
            None
          }
      },
      Some(task.ticketId),
      task.workflowitemId.toString,
      task.version,
      task.projectId.toString,
      {
        if (task.assignee.isDefined) {
          Some(userBuilder.buildDto(task.assignee.get, ""))
        } else {
          None
        }
      },
      Some(task.order),
      if (task.dueData == null || task.dueData == "") {
        None
      } else {
        Some(task.dueData)
      },
    {
      if (task.boardId == null) {
        null
      } else {
        task.boardId.toString
      }
    },
    task.taskTags
    )
  }

  def buildEntity(taskDto: TaskDto): Task = {
    new Task(
    {
      if (!taskDto.id.isDefined) {
        None
      } else {
        Some(new ObjectId(taskDto.id.get))
      }
    },
    taskDto.name,
    taskDto.description.getOrElse(""),
    {
      if (!taskDto.classOfService.isDefined) {
        None
      } else {
        Some(classOfServiceBuilder.buildEntity(taskDto.classOfService.get))
      }
    },
    determineTicketId(taskDto),
    taskDto.version,
    taskDto.order.orNull,
    {
      if (!taskDto.assignee.isDefined) {
        None
      } else {
        Some(userBuilder.buildEntity(taskDto.assignee.get))
      }
    },
    taskDto.dueDate.getOrElse(""),
    new ObjectId(taskDto.workflowitemId),
    new ObjectId(taskDto.boardId),
    new ObjectId(taskDto.projectId),
    taskDto.taskTags
    )
  }

  private def determineTicketId(taskDto: TaskDto): String = {
    if (!taskDto.id.isDefined) {
      return generateUniqueTicketId()
    }

    if (taskDto.id.isDefined && !taskDto.ticketId.isDefined) {
      throw new IllegalStateException("The task " + taskDto.id.get + " has not set a ticket id!")
    }

    taskDto.ticketId.get
  }
}