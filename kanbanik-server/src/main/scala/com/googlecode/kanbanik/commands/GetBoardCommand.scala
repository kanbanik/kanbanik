package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId

class GetBoardCommand extends ServerCommand[SimpleParams[BoardDto], SimpleParams[BoardDto]] {

  lazy val boardBuilder = new BoardBuilder()

  def execute(params: SimpleParams[BoardDto]): SimpleParams[BoardDto] = {
    
	try {
    	val board = Board.byId(new ObjectId(params.getPayload().getId()))
    	new SimpleParams(boardBuilder.buildDto(board, None))
    } catch {
      case e: IllegalArgumentException =>
        // board does not exists
        return new SimpleParams(null)
    }
    
    
  }
}