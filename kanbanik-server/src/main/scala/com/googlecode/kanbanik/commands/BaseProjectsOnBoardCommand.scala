package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.model.validation.ProjectValidation

abstract class BaseProjectsOnBoardCommand extends ServerCommand[SimpleParams[BoardWithProjectsDto], FailableResult[VoidParams]] with ProjectValidation{

  def execute(params: SimpleParams[BoardWithProjectsDto]): FailableResult[VoidParams] = {
    val board = Board.byId(new ObjectId(params.getPayload().getBoard().getId()))

    val projects = params.getPayload().getProjectsOnBoard()

    for (i <- 0 until projects.size()) {
      val res = executeSpecific(board, Project.byId(new ObjectId(projects.get(i).getId())))
      if (!res.isSucceeded()) {
        return res
      }
    }

    new FailableResult(new VoidParams)
  }
  
  def executeSpecific(board: Board, project: Project): FailableResult[VoidParams]
}