package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos.{ErrorDto, BoardDto}


class GetBoardCommand extends Command[BoardDto, BoardDto] {

  lazy val boardBuilder = new BoardBuilder()

  def execute(boardDto: BoardDto): Either[BoardDto, ErrorDto] = {
    
	try {
    	val board = Board.byId(new ObjectId(boardDto.id.getOrElse(return Right(ErrorDto("The board has to have the ID set")))), false)
    	Left(boardBuilder.buildDto(board, None))
    } catch {
      case e: IllegalArgumentException =>
        // it has been deleted
        return Left(new BoardDto(None, 1, "", 1, None, Some(false), None))
    }
    
    
  }
}