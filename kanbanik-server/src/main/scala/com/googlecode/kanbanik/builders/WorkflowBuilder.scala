package com.googlecode.kanbanik.builders

import scala.collection.mutable.ListBuffer
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board

class WorkflowBuilder extends BaseBuilder {

  def buildEntity(workflow: WorkflowDto, board: Option[Board]): Workflow = {
    val res = new Workflow(
        determineId(workflow),
        workflow.getWorkflowitems().toArray.toList.map(item => workflowitemBuilder.buildEntity(item.asInstanceOf[WorkflowitemDto], None, board)),
        board
    )
    
    res.withWorkflowitems(res.workflowitems.map(_.withParentWorkflow(res)))
  }
  
  def buildShallowDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = new WorkflowDto
    res.setId(workflow.id.get.toString())
	res.setBoard(board.getOrElse(boardBuilder.buildShallowDto(workflow.board, Some(res))))
    res
  }
  
  def buildDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = buildShallowDto(workflow, board)
	res.setBoard(board.getOrElse(boardBuilder.buildDto(workflow.board, Some(res))))
    val workflowitems = workflow.workflowitems.map(workflowitemBuilder.buildDto(_, Some(res)))
    val javaList = new java.util.ArrayList[WorkflowitemDto](workflowitems.size)
    workflowitems.foreach(javaList.add(_))
    res.setWorkflowitems(javaList)
    
    res
  }
  
  def workflowitemBuilder = new WorkflowitemBuilder

  def boardBuilder = new BoardBuilder
}