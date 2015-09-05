package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.{User, Board, Project}
import org.bson.types.ObjectId
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{ProjectWithBoardDto, ErrorDto, ProjectDto}

abstract class BaseProjectsOnBoardCommand
  extends Command[ProjectWithBoardDto, ProjectWithBoardDto] with HasEntityLoader {

  override def execute(params: ProjectWithBoardDto, user: User): Either[ProjectWithBoardDto, ErrorDto] = {

    val board = loadBoard(new ObjectId(params.boardId), includeTasks = false)

    if (!board.isDefined) {
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("board " + params.boardId)))
    }

    executeOne(params.project, board.get, user)
  }

  def executeSpecific(board: Board, project: Project, user: User): Either[ProjectWithBoardDto, ErrorDto]

  def executeOne(project: ProjectDto, board: Board, user: User): Either[ProjectWithBoardDto, ErrorDto] = {
    try {
      executeSpecific(board, Project.byId(new ObjectId(project.id.orNull)).copy(version = project.version), user)
    } catch {
      case e: IllegalArgumentException =>
        Right(ErrorDto(ServerMessages.entityDeletedMessage("project")))
    }
  }
}

