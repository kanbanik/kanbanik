package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto

class BoardBuilder {

  val workflowBuilder = new WorkflowBuilder
  
  def buildDto(board: Board): BoardDto = {
    buildDto(board, None)
  }
  
  def buildDto(board: Board, workflow: Option[WorkflowDto]): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setId(board.id.get.toString())
    res.setVersion(board.version)
    res.setWorkflow(workflow.getOrElse(workflowBuilder.buildDto(board.workflow, Some(res))))
    
    res
  }

  def buildEntity(boardDto: BoardDto): Board = {
    //    TODO-ref
//    if (boardDto.getId() == null) {
//      new Board(
//        detrmineId(boardDto),
//        boardDto.getName(),
//        boardDto.getVersion(),
//        boardDto.getWorkflowVersion(),
//        boardDto.isWorkflowLocked(),
//        None)
//    } else {
//      // an ugly way because I'm too lazy to fetch the correct ones according to the root workflowitem
//      val storedBoard = Board.byId(new ObjectId(boardDto.getId()))
//      storedBoard.name = boardDto.getName()
//      storedBoard.version = boardDto.getVersion()
//      storedBoard.workflowVersion = boardDto.getWorkflowVersion()
//      storedBoard.workflowLocked = boardDto.isWorkflowLocked()
//      storedBoard
//    }
null
  }

  private def detrmineId(boardDto: BoardDto): Option[ObjectId] = {
    if (boardDto.getId() == null) {
      return None
    } else {
      return Some(new ObjectId(boardDto.getId()))
    }
  }

  private[builders] def workflowitemBuilder = new WorkflowitemBuilder
}