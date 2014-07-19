package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dtos.{WorkfloVerticalSizing, BoardDto, WorkflowDto}

class BoardBuilder extends BaseBuilder {

  val workflowBuilder = new WorkflowBuilder

  val taskBuilder = new TaskBuilder

  def buildDto(board: Board): BoardDto = {
    buildDto(board, None)
  }

  def buildDto(board: Board, workflow: Option[WorkflowDto]): BoardDto = {
    val res = buildShallowDto(board)

    res.copy(
      workflow = Some(workflow.getOrElse(workflowBuilder.buildDto(board.workflow, Some(res))))
    )
  }

  def buildShallowDto(board: Board): BoardDto = {
    BoardDto(
      Some(board.id.get.toString()),
      board.version,
      board.name,
      board.workfloVerticalSizing.id,
      None,
      Some(board.userPictureShowingEnabled),
      Some(board.fixedSizeShortDescription),
      None
    )

  }

  def buildEntity(boardDto: BoardDto): Board = {
    val board = new Board(
    {
      if (boardDto.id != null && boardDto.id != "" && boardDto.id.isDefined) {
        Some(new ObjectId(boardDto.id.get))
      } else {
        None
      }
    },
    boardDto.name,
    boardDto.version,
    Workflow(),
    boardDto.tasks.getOrElse(List()).map(task => taskBuilder.buildEntity(task)),
    boardDto.showUserPictureEnabled.getOrElse(false),
    boardDto.fixedSizeShortDescription.getOrElse(false),
    {
      if (boardDto.workflowVerticalSizing == null) {
        WorkfloVerticalSizing.BALANCED
      } else {
        WorkfloVerticalSizing.fromId(boardDto.workflowVerticalSizing)
      }
    })

    if (boardDto.workflow.isDefined) {
      board.copy(workflow = workflowBuilder.buildEntity(boardDto.workflow.get, Some(board)))
    } else {
      board
    }
  }

  private[builders] def workflowitemBuilder = new WorkflowitemBuilder
}