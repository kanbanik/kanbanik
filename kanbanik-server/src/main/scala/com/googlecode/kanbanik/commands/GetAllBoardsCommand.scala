package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{BoardBuilder, ProjectBuilder, TaskBuilder}
import com.googlecode.kanbanik.dtos.{BoardWithProjectsDto, ErrorDto, GetAllBoardsWithProjectsDto, ListDto, _}
import com.googlecode.kanbanik.model.{User, Board, Project}

class GetAllBoardsCommand extends Command[GetAllBoardsWithProjectsDto, ListDto[BoardWithProjectsDto]] {

  lazy val boardBuilder = new BoardBuilder()

  lazy val projectBuilder = new ProjectBuilder()

  val taskBuilder = new TaskBuilder

  def execute(params: GetAllBoardsWithProjectsDto, user: User): Either[ListDto[BoardWithProjectsDto], ErrorDto] = {

    val loadedBoards = Board.all(params.includeTasks.getOrElse(false), params.includeTaskDescription.getOrElse(false), user)
    val loadedProjects = Project.all()

    val res = ListDto(
      loadedBoards.map(
        board => BoardWithProjectsDto(
        boardBuilder.buildDto(board).copy(
          tasks = Some(board.tasks.map(taskBuilder.buildDto))
        ), {
          val projectDtos = loadedProjects.filter(
            project => project.boards.getOrElse(List[Board]()).exists(projectsBoard => projectsBoard.id == board.id)
          ).map(projectOnBoard => projectBuilder.buildDto(projectOnBoard))

          if (projectDtos.size > 0) {
            Some(ProjectsDto(projectDtos))
          } else {
            None
          }
        }
        )
      )
    )

    Left(res)
  }

}