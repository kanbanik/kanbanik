package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.BoardScala
import org.bson.types.ObjectId

class SaveBoardCommand extends ServerCommand[SimpleParams[BoardDto], SimpleParams[BoardDto]] {
  lazy val boardBuilder = new BoardBuilder()

  def execute(params: SimpleParams[BoardDto]): SimpleParams[BoardDto] = {
    var storedBoard: BoardScala = null

    if (params.getPayload().getId() == null) {
      storedBoard = boardBuilder.buildEntity(params.getPayload()).store
    } else {
      storedBoard = BoardScala.byId(new ObjectId(params.getPayload().getId()))
      storedBoard.name = params.getPayload().getName()
      storedBoard.store
    }

    new SimpleParams(boardBuilder.buildDto(storedBoard))
  }
}