package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.validation.ProjectValidation


class RemoveProjectFromBoardCommand extends BaseProjectsOnBoardCommand with ProjectValidation {

  private val builder = new ProjectBuilder()

  override def executeSpecific(board: Board, project: Project): FailableResult[SimpleParams[ProjectDto]] = {

    val (deletable, msg) = canBeRemoved(project, board)

    if (project.boards.isDefined) {
      if (!deletable) {
        return new FailableResult(new SimpleParams, false, msg)
      }
      
      val newBoards = Some(project.boards.get.filter(_.id != board.id))

      return new FailableResult(new SimpleParams(builder.buildDto(project.withBoards(newBoards).store)))
    }

    new FailableResult(new SimpleParams)
  }
}