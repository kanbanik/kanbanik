package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto

class BoardBuilder {

  def buildDto(board: Board): BoardDto = {
    val res = buildShallowDto(board)
    if (board.workflowitems.isDefined) {
      res.setRootWorkflowitem(findRootWorkflowitem(board, board.workflowitems))
    }

    res
  }

  private[builders] def findRootWorkflowitem(board: Board, workflowitems: Option[List[Workflowitem]]): WorkflowitemDto = {
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

  private def isRoot(candidate: Workflowitem, workflowitems: List[Workflowitem]): Boolean = {
    for (potentialParent <- workflowitems) {
      if (potentialParent.nextItem.isDefined) {
        if (potentialParent.nextItem.get.id.get == candidate.id.get) {
          return false
        }
      }
    }

    return true
  }

  def buildShallowDto(board: Board): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setId(board.id.get.toString())
    res.setVersion(board.version)
    res.setWorkflowVersion(board.workflowVersion)
    res.setWorkflowLocked(board.workflowLocked)
    res
  }

  def buildEntity(boardDto: BoardDto): Board = {
    if (boardDto.getId() == null) {
      new Board(
        detrmineId(boardDto),
        boardDto.getName(),
        boardDto.getVersion(),
        boardDto.getWorkflowVersion(),
        boardDto.isWorkflowLocked(),
        None)
    } else {
      // an ugly way because I'm too lazy to fetch the correct ones according to the root workflowitem
      val storedBoard = Board.byId(new ObjectId(boardDto.getId()))
      storedBoard.name = boardDto.getName()
      storedBoard.version = boardDto.getVersion()
      storedBoard.workflowVersion = boardDto.getWorkflowVersion()
      storedBoard.workflowLocked = boardDto.isWorkflowLocked()
      storedBoard
    }

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