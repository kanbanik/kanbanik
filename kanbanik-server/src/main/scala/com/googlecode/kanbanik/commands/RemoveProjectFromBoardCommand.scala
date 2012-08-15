package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.VoidParams

class RemoveProjectFromBoardCommand extends BaseProjectsOnBoardCommand {

  override def executeSpecific(board: BoardScala, project: ProjectScala): FailableResult[VoidParams] = {

    val (deletable, msg) = canBeRemoved(project, board)

    if (project.boards.isDefined) {
      if (!deletable) {
        return new FailableResult(new VoidParams, false, msg)
      }
      project.boards = Some(project.boards.get.filter(_.id != board.id))
      project.store
    }

    new FailableResult(new VoidParams)
  }
}