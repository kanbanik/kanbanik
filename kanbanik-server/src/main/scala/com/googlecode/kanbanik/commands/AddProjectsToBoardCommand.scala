package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.dto.shell.FailableResult

class AddProjectsToBoardCommand extends BaseProjectsOnBoardCommand {

  override def executeSpecific(board: BoardScala, project: ProjectScala): FailableResult[VoidParams] = {
    if (project.boards.isDefined) {
      project.boards = Some(board :: project.boards.get)
      project.store
    } else {
      project.boards = Some(List(board))
      project.store
    }
    
    new FailableResult(new VoidParams)
  }
}