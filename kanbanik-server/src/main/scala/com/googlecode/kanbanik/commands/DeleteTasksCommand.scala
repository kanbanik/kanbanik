package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.db.HasEvents
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.{Board, Task, User, Workflowitem}
import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos._

class DeleteTasksCommand extends Command[TasksDto, TasksDto]
  with TaskManipulation
  with HasEvents {

  private lazy val taskBuilder = new TaskBuilder()

  override def execute(taskDto: TasksDto, user: User): Either[TasksDto, ErrorDto] = {

	  val results = taskDto.values.par.map(doExecute(_, user))
	  val errorResults = results.filter(_.isRight)
	  if (errorResults.isEmpty) {
	    Left(TasksDto(results.map(_.left.get).toList))
	  } else {
	    val messages = errorResults.map {
        case Left(x) => ""
        case Right(x) => x
      }

	    Right(ErrorDto(messages.mkString(", ")))
	  }
  }
  
  private def doExecute(taskDto: TaskDto, user: User): Either[TaskDto, ErrorDto] = {
    val boardId = new ObjectId(taskDto.boardId)
    val workflowitemId = new ObjectId(taskDto.workflowitemId)
    
    val board = Board.byId(boardId, includeTasks = false)

    if (!board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))) {
      return Right(ErrorDto("The workflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
    }
    
    val task = taskBuilder.buildEntity(taskDto)
      
    try {
    	task.delete(boardId, user)
      publish(EventType.TaskDeleted, Map(Task.Fields.id.toString -> task.id), true)
    } catch {
      case e: MidAirCollisionException =>
        	Right(ErrorDto(ServerMessages.midAirCollisionException))
    }

    Left(taskDto)
  }

  override def checkPermissions(param: TasksDto, user: User): Option[List[String]] = {

    val checks = param.values.map(task => List(
      checkOneOf(PermissionType.DeleteTask_b, task.boardId),
      checkOneOf(PermissionType.DeleteTask_p, task.projectId)
    )).flatten

    doCheckPermissions(user, checks)
  }

  override def filter(toReturn: TasksDto, user: User): Boolean = {
    toReturn.values.filter(task =>
      !(canRead(user, PermissionType.ReadBoard, task.boardId) && canRead(user, PermissionType.ReadProject, task.projectId))
    ).isEmpty
  }

}
