package com.googlecode.kanbanik.commands

import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.model.{User, Workflowitem}
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{ErrorDto, WorkflowitemDto, EmptyDto}

class DeleteWorkflowitemCommand extends Command[WorkflowitemDto, EmptyDto] with HasMongoConnection with HasEntityLoader {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val boardBuilder = new BoardBuilder

  override def execute(params: WorkflowitemDto, user: User): Either[EmptyDto, ErrorDto] = {

    val theId = new ObjectId(params.id.getOrElse(
    return Right(ErrorDto("The ID has to be defined"))
    ))

    val board = loadBoard(new ObjectId(params.parentWorkflow.get.board.id.getOrElse(
      return Right(ErrorDto("The ID of the Board has to be set"))
    )), includeTasks = true).getOrElse(
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("board " + params.parentWorkflow.get.board.name)))
    )

    val item = Workflowitem().copy(id = Some(theId))

    try {
      board.workflow.containsItem(item)
    } catch {
      case e: IllegalArgumentException =>
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("workflowitem")))
    }

    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitemId == theId)
    if (tasksOnWorkflowitem.size != 0) {
      val ticketIds = tasksOnWorkflowitem.map(_.ticketId).mkString(", ")
      return Right(ErrorDto("This workflowitem can not be deleted, because there are tasks associated with this workflowitem. Tasks: [" + ticketIds + "]"))
    }

    val foundItem = board.workflow.findItem(item)
    if (foundItem.isDefined && foundItem.get.nestedWorkflow.workflowitems.size > 0) {
      return Right(ErrorDto("This workflowitem can not be deleted, because it has a nested workflow. Please delete it first"))
    }

    board.copy(workflow = board.workflow.removeItem(foundItem.getOrElse(
      return Right(ErrorDto("This workflowitem has been deleted by a different user. Please refresh your browser to get the current data"))
    ))).store

    Left(EmptyDto())
  }
}
