package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId

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
  
  def buildEntity(boardDto: BoardDto): BoardScala = {
    new BoardScala(
    		detrmineId(boardDto), 
    		boardDto.getName(),
    		// ignoring workflowitems for now...
    		None
    );
  }
  
  private def detrmineId(boardDto: BoardDto): Option[ObjectId] = {
    if (boardDto.getId() == null) {
      return None
    } else {
      return Some(new ObjectId(boardDto.getId()))
    }
  }
}