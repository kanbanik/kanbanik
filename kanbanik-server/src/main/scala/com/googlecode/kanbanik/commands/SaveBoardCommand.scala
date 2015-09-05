package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.dtos.{ErrorDto, BoardDto}

class SaveBoardCommand extends Command[BoardDto, BoardDto] {

  lazy val boardBuilder = new BoardBuilder()

  override def execute(boardDto: BoardDto): Either[BoardDto, ErrorDto] = {
    val storedBoard: Board = {
      try {
        boardBuilder.buildEntity(boardDto).store
      } catch {
        case e: IllegalArgumentException =>
          return Right(ErrorDto(ServerMessages.entityDeletedMessage("board")))

      }
    }
    
    Left(boardBuilder.buildDto(storedBoard, None))
  }
}