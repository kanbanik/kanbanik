package com.googlecode.kanbanik.builders

import scala.collection.mutable.ListBuffer
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.BoardDto

class WorkflowBuilder {

  def buildDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = new WorkflowDto
    
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