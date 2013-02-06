package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow

class BoardBuilder extends BaseBuilder {

  val workflowBuilder = new WorkflowBuilder

  def buildDto(board: Board): BoardDto = {
    buildDto(board, None)
  }

  def buildDto(board: Board, workflow: Option[WorkflowDto]): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setBalanceWorkflowitems(board.balanceWorkflowitems)
    res.setId(board.id.get.toString())
    res.setVersion(board.version)
    res.setWorkflow(workflow.getOrElse(workflowBuilder.buildDto(board.workflow, Some(res))))

    res
  }

  def buildEntity(boardDto: BoardDto): Board = {
    val board = new Board(
      determineId(boardDto),
      boardDto.getName(),
      boardDto.isBalanceWorkflowitems(),
      boardDto.getVersion(),
      Workflow()
    )
    
    board.withWorkflow(workflowBuilder.buildEntity(boardDto.getWorkflow(), Some(board)))
      
  }

  private[builders] def workflowitemBuilder = new WorkflowitemBuilder
}