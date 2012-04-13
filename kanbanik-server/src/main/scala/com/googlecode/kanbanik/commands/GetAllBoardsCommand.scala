package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.builders.BoardBuilder

class GetAllBoardsCommand extends ServerCommand[VoidParams, SimpleParams[ListDto[BoardWithProjectsDto]]] {

  private var boardBuilder = new BoardBuilder()

  private var projectBuilder = new ProjectBuilder()

  def execute(params: VoidParams): SimpleParams[ListDto[BoardWithProjectsDto]] = {
    val res = new ListDto[BoardWithProjectsDto]()
    BoardScala.all.foreach(board => { res.addItem(new BoardWithProjectsDto(getBoardBuilder.buildDto(board))) })

    buildProjectsForBoard(res)
    
    new SimpleParams(res)
  }
  
  private[commands] def buildProjectsForBoard(res: ListDto[BoardWithProjectsDto]) {
    for (project <- allProjects) {
      val projectDto = getProjectBuilder.buildDto(project)

      for (i <- 0 until res.getList().size()) {
        val boardToProjects = res.getList().get(i)
        if (projectDto.getBoards().contains(boardToProjects.getBoard())) {
          boardToProjects.addProject(projectDto)
        }

      }

    }    
  }
  
  // both only because of testing
  private[commands] def getBoardBuilder = boardBuilder
  
  private[commands] def getProjectBuilder = projectBuilder
  
  private[commands] def allProjects = ProjectScala.all()
  
}