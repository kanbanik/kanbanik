package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.WorkflowitemScala

class BoardBuilder {

  val workflowitemBuilder = new WorkflowitemBuilder
  
  def buildDto(board: BoardScala): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setId(board.id.get.toString())
    if (board.workflowitems.isDefined) {
      res.setRootWorkflowitem(workflowitemBuilder.buildDto(board.workflowitems.get.head))
    }
    
    res
  }
}