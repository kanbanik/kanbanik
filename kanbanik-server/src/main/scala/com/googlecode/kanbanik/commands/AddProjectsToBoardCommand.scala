package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.{User, Board, Project}
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, ProjectWithBoardDto}

class AddProjectsToBoardCommand extends BaseProjectsOnBoardCommand {

  private val builder = new ProjectBuilder()

  override def executeSpecific(board: Board, project: Project, user: User): Either[ProjectWithBoardDto, ErrorDto] = {

    val toStore = {
      if (project.boards.isDefined) {
        project.copy(boards = Some(board :: project.boards.get))
      } else {
        project.copy(boards = Some(List(board)))
      }
    }

    Left(ProjectWithBoardDto(builder.buildDto(toStore.store(user)), board.id.get.toString))
  }
}