package com.googlecode.kanbanik.commands;

import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, WorkflowitemDto}
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId

class EditWorkflowitemDataCommand extends Command[WorkflowitemDto, WorkflowitemDto] with HasMongoConnection {

  private lazy val workflowitemBuilder = new WorkflowitemBuilder

  private lazy val boardBuilder = new BoardBuilder

  def execute(params: WorkflowitemDto): Either[WorkflowitemDto, ErrorDto] = {
    val parentWorkflow = params.parentWorkflow.getOrElse(return Right(ErrorDto("The parent workflow is not set")))
    val board = params.parentWorkflow.get.board

    val builtBoard = Board.byId(new ObjectId(board.id.get), includeTasks = false).copy(version = board.version)

    val workflowitem = workflowitemBuilder.buildEntity(params, None, Some(builtBoard))

    val workflowReplaced = builtBoard.workflow.replaceItem(workflowitem)
    val storedBoard = builtBoard.copy(workflow = workflowReplaced).store
    Left(workflowitemBuilder.buildDto(storedBoard.workflow.findItem(workflowitem).get, None))

  }
}
