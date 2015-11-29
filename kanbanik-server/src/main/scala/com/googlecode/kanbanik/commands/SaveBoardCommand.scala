package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.{Permission, User, Board}
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.dtos.{PermissionType, ErrorDto, BoardDto}
import com.googlecode.kanbanik.security._

class SaveBoardCommand extends Command[BoardDto, BoardDto] {

  lazy val boardBuilder = new BoardBuilder()

  override def execute(boardDto: BoardDto, user: User): Either[BoardDto, ErrorDto] = {
    val storedBoard: Board = {
      try {
        boardBuilder.buildEntity(boardDto).store
      } catch {
        case e: IllegalArgumentException =>
          return Right(ErrorDto(ServerMessages.entityDeletedMessage("board")))

      }
    }

    addMePermissions(user, boardDto.id,
      storedBoard.id.get.toString,
      PermissionType.CreateTask_b,
      PermissionType.ReadBoard,
      PermissionType.EditBoard,
      PermissionType.DeleteBoard)
    Left(boardBuilder.buildDto(storedBoard, None, user))
  }

  override def checkPermissions(param: BoardDto, user: User): Option[List[String]] =
    checkSavePermissions(user, param.id, PermissionType.CreateBoard, PermissionType.EditBoard)

  override def filter(toReturn: BoardDto, user: User): Boolean =
    canRead(user, PermissionType.ReadBoard, toReturn.id.getOrElse(""))
}