package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{TasksDto, EmptyDto, ErrorDto, TaskDto}

class DeleteTasksCommand extends Command[TasksDto, TasksDto] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(taskDto: TasksDto): Either[TasksDto, ErrorDto] = {

	  val results = taskDto.values.par.map(doExecute)
	  val errorResults = results.filter(_.isRight)
	  if (errorResults.isEmpty) {
	    Left(TasksDto(results.map { case Left(x) => x}.toList))
	  } else {
	    val messages = errorResults.map {
        case Left(x) => ""
        case Right(x) => x
      }

	    Right(ErrorDto(messages.mkString(", ")))
	  }
  }
  
  private def doExecute(taskDto: TaskDto): Either[TaskDto, ErrorDto] = {
    val boardId = new ObjectId(taskDto.boardId)
    val workflowitemId = new ObjectId(taskDto.workflowitemId)
    
    val board = Board.byId(boardId, includeTasks = false)

    if (!board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))) {
      return Right(ErrorDto("The workflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
    }
    
    val task = taskBuilder.buildEntity(taskDto)
      
    try {
    	task.delete(boardId)
    } catch {
      case e: MidAirCollisionException =>
        	Right(ErrorDto(ServerMessages.midAirCollisionException))
    }

    Left(taskDto)
  }
}