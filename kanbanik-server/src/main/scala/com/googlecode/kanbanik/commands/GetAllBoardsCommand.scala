package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.dtos.GetAllBoardsWithProjectsDto
import com.googlecode.kanbanik.dtos.ListDto
import scala.Some
import com.googlecode.kanbanik.dtos.BoardWithProjectsDto

class GetAllBoardsCommand extends Command[GetAllBoardsWithProjectsDto, ListDto[BoardWithProjectsDto]] {

  lazy val boardBuilder = new BoardBuilder()

  lazy val projectBuilder = new ProjectBuilder()

  def execute(params: GetAllBoardsWithProjectsDto): Either[ListDto[BoardWithProjectsDto], ErrorDto] = {

    val loadedBoards = Board.all(params.includeTasks.getOrElse(false))
    val loadedProjects = Project.all()

    val res = ListDto(
      loadedBoards.map(
        board => BoardWithProjectsDto(
        boardBuilder.buildDto(board), {
          val projectDtos = loadedProjects.filter(
            project => project.boards.getOrElse(List[Board]()).filter(
              projectsBoard => projectsBoard.id == board.id).size > 0
          ).map(projectOnBoard => projectBuilder.buildDto2(projectOnBoard))

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