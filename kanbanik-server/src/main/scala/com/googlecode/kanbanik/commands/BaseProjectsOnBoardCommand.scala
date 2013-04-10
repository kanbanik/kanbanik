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

abstract class BaseProjectsOnBoardCommand extends ServerCommand[SimpleParams[BoardWithProjectsDto], FailableResult[SimpleParams[BoardWithProjectsDto]]] with HasEntityLoader {

  def execute(params: SimpleParams[BoardWithProjectsDto]): FailableResult[SimpleParams[BoardWithProjectsDto]] = {

    val board = loadBoard(new ObjectId(params.getPayload().getBoard().getId()), false)

    if (!board.isDefined) {
      return new FailableResult(new SimpleParams, false, ServerMessages.entityDeletedMessage("board " + params.getPayload().getBoard().getName()))
    }

    val projects = params.getPayload().getProjectsOnBoard()

    var projectsOnBoard = List[ProjectDto]()

    for (i <- 0 until projects.size()) {
      var res: FailableResult[SimpleParams[ProjectDto]] = null
      try {
        res = executeSpecific(board.get, Project.byId(new ObjectId(projects.get(i).getId())))
      } catch {
        case e: IllegalArgumentException =>
          return new FailableResult(new SimpleParams, false, ServerMessages.entityDeletedMessage("project"))
      }
      if (!res.isSucceeded()) {
        return new FailableResult(new SimpleParams, false, res.getMessage())
      }

      projectsOnBoard = res.getPayload().getPayload() :: projectsOnBoard
    }

    val retBoardWithProjects = new BoardWithProjectsDto
    retBoardWithProjects.setBoard(params.getPayload().getBoard())
    for (p <- projectsOnBoard) {
      retBoardWithProjects.addProject(p)
    }
    new FailableResult(new SimpleParams(retBoardWithProjects))
  }

  def executeSpecific(board: Board, project: Project): FailableResult[SimpleParams[ProjectDto]]
}