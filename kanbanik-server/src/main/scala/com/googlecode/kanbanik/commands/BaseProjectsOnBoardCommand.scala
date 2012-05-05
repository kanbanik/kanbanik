package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.BoardScala
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.ProjectScala

abstract class BaseProjectsOnBoardCommand extends ServerCommand[SimpleParams[BoardWithProjectsDto], VoidParams] {

  def execute(params: SimpleParams[BoardWithProjectsDto]): VoidParams = {
    val board = BoardScala.byId(new ObjectId(params.getPayload().getBoard().getId()))

    val projects = params.getPayload().getProjectsOnBoard()

    for (i <- 0 until projects.size()) {
      executeSpecific(board, ProjectScala.byId(new ObjectId(projects.get(i).getId())))
    }

    new VoidParams
  }
  
  def executeSpecific(board: BoardScala, project: ProjectScala)
}