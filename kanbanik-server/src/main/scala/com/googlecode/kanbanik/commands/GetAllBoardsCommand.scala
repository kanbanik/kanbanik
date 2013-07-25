package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.dto.GetAllBoardsWithProjectsParams

class GetAllBoardsCommand extends ServerCommand[GetAllBoardsWithProjectsParams, SimpleParams[ListDto[BoardWithProjectsDto]]] {

  def execute(params: GetAllBoardsWithProjectsParams): SimpleParams[ListDto[BoardWithProjectsDto]] = {
    val res = new ListDto[BoardWithProjectsDto]()
    Board.all(params.isIncludeTasks()).foreach(board => { res.addItem(new BoardWithProjectsDto(getBoardBuilder.buildDto(board, None))) })
    
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
  
  // all only because of testing
  private[commands] def getBoardBuilder = new BoardBuilder()
  
  private[commands] def getProjectBuilder = new ProjectBuilder()
  
  private[commands] def allProjects = Project.all()
  
}