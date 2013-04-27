package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.commons._

abstract class BaseProjectsOnBoardCommand extends ServerCommand[SimpleParams[BoardWithProjectsDto], FailableResult[SimpleParams[BoardWithProjectsDto]]] with HasEntityLoader {

  def execute(params: SimpleParams[BoardWithProjectsDto]): FailableResult[SimpleParams[BoardWithProjectsDto]] = {

    val board = loadBoard(new ObjectId(params.getPayload().getBoard().getId()), false)

    if (!board.isDefined) {
      return new FailableResult(new SimpleParams, false, ServerMessages.entityDeletedMessage("board " + params.getPayload().getBoard().getName()))
    }

    val projects = params.getPayload().getProjectsOnBoard()

    val projectsAsScalaList = projects.toScalaList

    val results = projectsAsScalaList.map(executeOne(_, board.get))

    val incorrectResults = results.filter(!_.isSucceeded)
    if (incorrectResults.size != 0) {
      val msgs = incorrectResults.map(_.getMessage()).mkString(", ")
      return new FailableResult(new SimpleParams, false, msgs)
    }

    val projectsOnBoard = results.map(_.getPayload().getPayload())
     
    val retBoardWithProjects = new BoardWithProjectsDto
    retBoardWithProjects.setBoard(params.getPayload().getBoard())
    for (p <- projectsOnBoard) {
      retBoardWithProjects.addProject(p)
    }
    new FailableResult(new SimpleParams(retBoardWithProjects))
  }

  def executeSpecific(board: Board, project: Project): FailableResult[SimpleParams[ProjectDto]]

  def executeOne(project: ProjectDto, board: Board): FailableResult[SimpleParams[ProjectDto]] = {
    try {
      executeSpecific(board, Project.byId(new ObjectId(project.getId())))
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new SimpleParams, false, ServerMessages.entityDeletedMessage("project"))
    }
  }
}

