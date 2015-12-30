package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{BoardBuilder, ProjectBuilder, TaskBuilder}
import com.googlecode.kanbanik.dtos.{BoardWithProjectsDto, ErrorDto, GetAllBoardsWithProjectsDto, ListDto, _}
import com.googlecode.kanbanik.model.{User, Board, Project}
import org.bson.types.ObjectId

class GetAllBoardsCommand extends Command[GetAllBoardsWithProjectsDto, ListDto[BoardWithProjectsDto]] {

  lazy val boardBuilder = new BoardBuilder()

  lazy val projectBuilder = new ProjectBuilder()

  val taskBuilder = new TaskBuilder

  override def execute(params: GetAllBoardsWithProjectsDto, user: User): Either[ListDto[BoardWithProjectsDto], ErrorDto] = {
    val loadedBoards = Board.all(
      params.includeTasks.getOrElse(false),
      params.includeTaskDescription.getOrElse(false),
      extractFilters(params.filters, x => if (x.bid.isDefined) Some(new ObjectId(x.bid.get)) else None),
      extractFilters(params.filters, _.bname),
      user)
    val loadedProjects = Project.all(
      user,
      extractFilters(params.filters, x => if (x.pid.isDefined) Some(new ObjectId(x.pid.get)) else None),
      extractFilters(params.filters, _.pname)
    )

    val res = ListDto(
      loadedBoards.map(
        board => BoardWithProjectsDto(
        boardBuilder.buildDto(board, user).copy(
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

  def extractFilters[T](filters: Option[List[FilterDto]], f: FilterDto => Option[T]): Option[List[T]] = {
    if (filters.isDefined) {
      val xs = for (x <- filters.get if f(x).isDefined) yield f(x).get
      if (xs.isEmpty) {
        None
      } else {
        Some(xs)
      }
    } else {
      None
    }
  }

}