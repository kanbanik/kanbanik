package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model._
import com.googlecode.kanbanik.commands.TaskManipulation
import com.googlecode.kanbanik.dto.BoardDto
import scala.collection.mutable.HashMap
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dtos.{TaskDto => NewTaskDto}
import com.googlecode.kanbanik.dto.TaskDto
import scala.Some

class TaskBuilder extends TaskManipulation {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder
  
  lazy val userBuilder = new UserBuilder

  def buildDto2(task: Task): NewTaskDto = {
    NewTaskDto(
      Some(task.id.get.toString),
      task.name,
      task.description,
      {
          if (task.classOfService.isDefined) {
            Some(classOfServiceBuilder.buildDto2(task.classOfService.get))
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
          Some(userBuilder.buildDto2(task.assignee.get, ""))
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
      task.boardId.toString
    )
  }

  def buildEntity2(taskDto: NewTaskDto): Task = {
    new Task(
    {
      if (!taskDto.id.isDefined) {
        None
      } else {
        Some(new ObjectId(taskDto.id.get))
      }
    },
    taskDto.name,
    taskDto.description,
    {
      if (!taskDto.classOfService.isDefined) {
        None
      } else {
        Some(classOfServiceBuilder.buildEntity2(taskDto.classOfService.get))
      }
    },
    determineTicketId2(taskDto),
    taskDto.version,
    taskDto.order.get,
    {
      if (!taskDto.assignee.isDefined) {
        None
      } else {
        Some(userBuilder.buildEntity2(taskDto.assignee.get))
      }
    },
    taskDto.dueDate.getOrElse(""),
    new ObjectId(taskDto.workflowitemId),
    new ObjectId(taskDto.boardId),
    new ObjectId(taskDto.projectId)
    )
  }

  private def determineTicketId2(taskDto: NewTaskDto): String = {
    if (!taskDto.id.isDefined) {
      return generateUniqueTicketId()
    }

    if (taskDto.id.isDefined && !taskDto.ticketId.isDefined) {
      throw new IllegalStateException("The task " + taskDto.id.get + " has not set a ticket id!")
    }

    taskDto.ticketId.get
  }
}