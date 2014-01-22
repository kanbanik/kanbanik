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
import com.googlecode.kanbanik.dtos.{ErrorDto, ProjectWithBoardDto}

class AddProjectsToBoardCommand extends BaseProjectsOnBoardCommand {

  private val builder = new ProjectBuilder()

  override def executeSpecific(board: Board, project: Project): Either[ProjectWithBoardDto, ErrorDto] = {

    val toStore = {
      if (project.boards.isDefined) {
        project.copy(boards = Some(board :: project.boards.get))
      } else {
        project.copy(boards = Some(List(board)))
      }
    }

    Left(ProjectWithBoardDto(builder.buildDto2(toStore.store), board.id.get.toString))
  }
}