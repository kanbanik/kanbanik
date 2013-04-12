package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dto.ProjectDto

class AddProjectsToBoardCommand extends BaseProjectsOnBoardCommand {

  private val builder = new ProjectBuilder()

  override def executeSpecific(board: Board, project: Project): FailableResult[SimpleParams[ProjectDto]] = {

    val toStore = {
      if (project.boards.isDefined) {
        project.withBoards(Some(board :: project.boards.get))
      } else {
        project.withBoards(Some(List(board)))
      }
    }
    
    new FailableResult(new SimpleParams(builder.buildDto(toStore.store)))
  }
}