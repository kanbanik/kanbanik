package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.commons._

class WorkflowBuilder extends BaseBuilder {

  def buildEntity(workflow: WorkflowDto, board: Option[Board]): Workflow = {
    val res = new Workflow(
        determineId(workflow),
        workflow.getWorkflowitems().toScalaList.map(item => workflowitemBuilder.buildEntity(item, None, board)),
        board
    )
    
    res.withWorkflowitems(res.workflowitems.map(_.withParentWorkflow(res)))
  }
  
  def buildShallowDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = new WorkflowDto
    res.setId(workflow.id.get.toString())
	res.setBoard(board.getOrElse(boardBuilder.buildShallowDto(workflow.board)))
    res
  }
  
  def buildDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = buildShallowDto(workflow, board)
	res.setBoard(board.getOrElse(boardBuilder.buildDto(workflow.board, Some(res))))
    val workflowitems = workflow.workflowitems.map(workflowitemBuilder.buildDto(_, Some(res)))
    val javaList = workflowitems.toJavaList
    res.setWorkflowitems(javaList)
    
    res
  }
  
  def workflowitemBuilder = new WorkflowitemBuilder

  def boardBuilder = new BoardBuilder
}