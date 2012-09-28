package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.model.validation.ProjectValidation

abstract class BaseProjectsOnBoardCommand extends ServerCommand[SimpleParams[BoardWithProjectsDto], FailableResult[SimpleParams[BoardWithProjectsDto]]] with ProjectValidation{

  def execute(params: SimpleParams[BoardWithProjectsDto]): FailableResult[SimpleParams[BoardWithProjectsDto]] = {
    val board = Board.byId(new ObjectId(params.getPayload().getBoard().getId()))

    val projects = params.getPayload().getProjectsOnBoard()

    var projectsOnBoard = List[ProjectDto]() 
    
    for (i <- 0 until projects.size()) {
      val res = executeSpecific(board, Project.byId(new ObjectId(projects.get(i).getId())))
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