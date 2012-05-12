package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.BoardScala
import org.bson.types.ObjectId

class GetBoardCommand extends ServerCommand[SimpleParams[BoardDto], SimpleParams[BoardDto]] {

  lazy val boardBuilder = new BoardBuilder()

  def execute(params: SimpleParams[BoardDto]): SimpleParams[BoardDto] = {
    val board = BoardScala.byId(new ObjectId(params.getPayload().getId()))
    new SimpleParams(boardBuilder.buildDto(board))
  }
}