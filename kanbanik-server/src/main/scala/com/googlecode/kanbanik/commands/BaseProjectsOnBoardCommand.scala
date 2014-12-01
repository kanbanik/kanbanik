package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{ProjectWithBoardDto, ErrorDto, ProjectDto}

abstract class BaseProjectsOnBoardCommand
  extends Command[ProjectWithBoardDto, ProjectWithBoardDto] with HasEntityLoader {

  def execute(params: ProjectWithBoardDto): Either[ProjectWithBoardDto, ErrorDto] = {

    val board = loadBoard(new ObjectId(params.boardId), includeTasks = false)

    if (!board.isDefined) {
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("board " + params.boardId)))
    }

    executeOne(params.project, board.get)
  }

  def executeSpecific(board: Board, project: Project): Either[ProjectWithBoardDto, ErrorDto]

  def executeOne(project: ProjectDto, board: Board): Either[ProjectWithBoardDto, ErrorDto] = {
    try {
      executeSpecific(board, Project.byId(new ObjectId(project.id.orNull)).copy(version = project.version))
    } catch {
      case e: IllegalArgumentException =>
        Right(ErrorDto(ServerMessages.entityDeletedMessage("project")))
    }
  }
}

