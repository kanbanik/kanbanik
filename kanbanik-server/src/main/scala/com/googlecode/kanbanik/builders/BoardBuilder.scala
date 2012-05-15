package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto

class BoardBuilder {

  def buildDto(board: BoardScala): BoardDto = {
    val res = buildShallowDto(board)
    if (board.workflowitems.isDefined) {
      res.setRootWorkflowitem(findRootWorkflowitem(board, board.workflowitems))
    }

    res
  }

  private[builders] def findRootWorkflowitem(board: BoardScala, workflowitems: Option[List[WorkflowitemScala]]): WorkflowitemDto = {
    if (!workflowitems.isDefined || workflowitems.get.size == 0) {
      return null;
    }

    for (candidate <- workflowitems.get) {
      if (isRoot(candidate, workflowitems.get)) {
        return workflowitemBuilder.buildDto(candidate)
      }
    }

    throw new IllegalStateException("The board + " + board.id.get.toString() + " has no root workflowitem")
  }

  private def isRoot(candidate: WorkflowitemScala, workflowitems: List[WorkflowitemScala]): Boolean = {
    for (potentialParent <- workflowitems) {
      if (potentialParent.nextItem.isDefined) {
        if (potentialParent.nextItem.get.id.get == candidate.id.get) {
          return false
        }
      }
    }
    
    return true
  }

  def buildShallowDto(board: BoardScala): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setId(board.id.get.toString())
    res
  }

  def buildEntity(boardDto: BoardDto): BoardScala = {
    new BoardScala(
      detrmineId(boardDto),
      boardDto.getName(),
      // ignoring workflowitems for now...
      None);
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