package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.{TaskBuilder, ProjectBuilder, BoardBuilder}
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.dtos.GetAllBoardsWithProjectsDto
import com.googlecode.kanbanik.dtos.ListDto
import scala.Some
import com.googlecode.kanbanik.dtos.BoardWithProjectsDto

class GetAllBoardsCommand extends Command[GetAllBoardsWithProjectsDto, ListDto[BoardWithProjectsDto]] {

  lazy val boardBuilder = new BoardBuilder()

  lazy val projectBuilder = new ProjectBuilder()

  val taskBuilder = new TaskBuilder

  def execute(params: GetAllBoardsWithProjectsDto): Either[ListDto[BoardWithProjectsDto], ErrorDto] = {

    val loadedBoards = Board.all(params.includeTasks.getOrElse(false), params.includeTaskDescription.getOrElse(false))
    val loadedProjects = Project.all()

    val res = ListDto(
      loadedBoards.map(
        board => BoardWithProjectsDto(
        boardBuilder.buildDto(board).copy(
          tasks = Some(board.tasks.map(taskBuilder.buildDto(_)))
        ), {
          val projectDtos = loadedProjects.filter(
            project => project.boards.getOrElse(List[Board]()).filter(
              projectsBoard => projectsBoard.id == board.id).size > 0
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