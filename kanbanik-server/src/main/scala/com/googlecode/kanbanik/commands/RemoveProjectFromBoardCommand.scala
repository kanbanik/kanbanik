package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.validation.ProjectValidation
import com.googlecode.kanbanik.dtos.{ErrorDto, ProjectWithBoardDto}


class RemoveProjectFromBoardCommand extends BaseProjectsOnBoardCommand with ProjectValidation {

  private val builder = new ProjectBuilder()

  override def executeSpecific(board: Board, project: Project): Either[ProjectWithBoardDto, ErrorDto] = {

    val (deletable, msg) = canBeRemoved(project, board)

    if (project.boards.isDefined) {
      if (!deletable) {
        return Right(ErrorDto(msg))
      }
      
      val newBoards = Some(project.boards.get.filter(_.id != board.id))

      val stored = project.copy(boards = newBoards).store
      Left(ProjectWithBoardDto(builder.buildDto2(stored), board.id.get.toString))
    } else {
      Right(ErrorDto("Project is on no boards - nothing to do"))
    }
  }
}